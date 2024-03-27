/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.List;
import java.util.Set;

/*
 * Value Concepts
 * ==============
 *
 * Partial -- a value that contains unbound variables, directly or indirectly, and is potentially complete. Partial
 * values evolve, and partial values can be de-duplicated when we set one value equal to another (unification). A
 * partial value can become complete or stateful.
 *
 * Complete -- a literal, number, procedure, or composite that only contains complete values. Complete values can
 * contain other complete values directly or reference them indirectly through variables.
 *
 * Stateful -- a value that contains mutable state. Stateful values can change over time. Unlike partial, which is
 * potentially complete, a stateful value will never be complete because it can change indefinitely. A java HashMap
 * wrapped as an Obj, for example, is a Stateful value.
 *
 * Valid Key -- A valid key has equals and hash code methods based on consistent and immutable state, which may or may
 * not be its content. For example, a Cell is a valid key because its equals and hash code methods do not depend on its
 * contained value, they depend on its identity. In contrast, a Flt64 seems to be a valid key, but it is not because
 * there is no guarantee that a == (a/b) * b. Valid keys can be used for lookups, such as a key in a java HashMap.
 *
 * Determined Values
 * =================
 *
 * All values are determined, except for records. A record becomes determined when all of its features are bound.
 *
 * Concrete Complete
 * =================
 *
 * Subtypes of the `Complete` interface are immutable, and if a type is a composite, it only contains `Complete` types.
 * Only `Complete` types are shared between actors. When an actor sends a message or spawns a new actor, the controller
 * calls `checkComplete()` to assert or convert arguments to one of the `Complete` values.
 *
 * Complete values hierarchy:
 *   - Complete
 *     - CompleteActorCfgCtor
 *     - CompleteObj
 *     - CompleteProc
 *     - CompleteRec
 *       - CompleteTuple
 *     - FailedValue
 *     - Feature
 *       - Int64
 *         - Int32
 *           - Char
 *       - Literal
 *         - Token
 *         - Nothing
 *         - Eof
 *         - Bool
 *         - Str
 *     - Num
 *       - Flt64
 *         - Flt32
 *       - Int64
 *         - Int32
 *           - Char
 *     - OpaqueValue
 *     - RequestId
 *
 * The `CompleteActorCfgCtor` exists for modularity. An actor system can contain modules, and a module can export an
 * actor construct as a `Rec` containing a `CompleteActorCfgCtor`.
 *
 * There is one exception to all of the above. When an actor spawns another actor, we delay verifying the actor
 * configuration for as long as possible to opportunistically increase concurrency. During the spawn callback, we
 * dynamically verify that the `ActorCfg` is effectively complete by verifying that its free variables are complete.
 */
public interface Value extends ValueOrIdent, ValueOrVar, ValueOrVarSet, ValueOrIdentPtn, ValueOrResolvedPtn {

    Object PRESENT = new Object();

    default Value add(Value addend) {
        throw new UnsupportedOperationException("Add not supported");
    }

    default String appendToString(String string) {
        return string + this;
    }

    /*
     * See the Var class to learn about the unification procedure.
     */
    @Override
    Value bindToValue(Value value, Set<Memo> memos) throws WaitVarException;

    @Override
    default Value bindToValueOrVar(ValueOrVar valueOrVar, Set<Memo> memos) throws WaitVarException {
        return valueOrVar.bindToValue(this, memos);
    }

    @Override
    default Value bindToVar(Var var, Set<Memo> memos) throws WaitVarException {
        return var.bindToValue(this, memos);
    }

    /*
     * This method is a polymorphic callback requesting "case nonRecValue of Value then...".  Return this
     * if nonRecValue.entails(this, ...) is true
     */
    @Override
    default ValueOrResolvedPtn caseNonRecOfThis(Value nonRecValue, Env env) throws WaitException {
        return nonRecValue.entails(this, null) ? this : null;
    }

    /*
     * This method determines if the value matches the structure of the given pattern. If the two structures match,
     * the value will be deconstructed later into the identifiers present in the pattern.
     *
     * The possible outcomes of `caseOf` are:
     *   1) A null is returned because the two structures do not match
     *   2) A value is returned because we compared to another value, and they matched according to entails
     *   3) A ResolvedPtn is returned because the value structure matched the pattern structure
     */
    default ValueOrResolvedPtn caseOf(ValueOrPtn valueOrPtn, Env env) throws WaitException {
        return valueOrPtn.caseNonRecOfThis(this, env);
    }

    /*
     * This is a callback requesting "case Rec of nonRecValue then...". Return null (false) because this method was
     * invoked to compare a record to a non-record.
     */
    @Override
    default ValueOrResolvedPtn caseRecOfThis(Rec rec, Env env) throws WaitException {
        return null;
    }

    /*
     * Subtypes must return a complete representation or throw a `WaitException` if waiting to become complete. If a
     * value can never be complete, throw a CannotCompleteError.
     */
    @Override
    default Complete checkComplete() throws WaitVarException {
        throw new CannotCompleteError(this);
    }

    default int compareValueTo(Value right) {
        throw new UnsupportedOperationException("compareValueTo not supported");
    }

    /*
     * Deconstruct the receiver according to the given pattern. The bindings created during the deconstruction
     * process are returned in a new child environment.
     */
    default Env deconstruct(ValueOrResolvedPtn valueOrResolvedPtn, Env env) {
        if (valueOrResolvedPtn instanceof ResolvedIdentPtn identPtn) {
            return Env.create(env, List.of(new EnvEntry(identPtn.ident, new Var(this))));
        } else {
            return env;
        }
    }

    default Bool disentails(Value operand) throws WaitException {
        return Bool.of(!entails(operand, null));
    }

    default Value divide(Value divisor) {
        throw new UnsupportedOperationException("Divide not supported");
    }

    default Bool entails(Value operand) throws WaitVarException {
        return Bool.of(entails(operand, null));
    }

    @Override
    default boolean entails(Value operand, Set<Memo> memos) throws WaitVarException {
        return this == operand;
    }

    boolean entailsRec(Rec operand, Set<Memo> memos) throws WaitVarException;

    @Override
    default boolean entailsValueOrIdent(ValueOrIdent operand, Env env) throws WaitVarException {
        return entails(operand.resolveValue(env), null);
    }

    @Override
    default boolean entailsValueOrVar(ValueOrVar operand, Set<Memo> memos) throws WaitVarException {
        return operand.entails(this, memos);
    }

    @Override
    default boolean entailsVar(Var operand, Set<Memo> memos) throws WaitVarException {
        return operand.entails(this, memos);
    }

    default Bool greaterThan(Value operand) {
        throw new UnsupportedOperationException("> not supported");
    }

    default Bool greaterThanOrEqualTo(Value operand) {
        throw new UnsupportedOperationException(">= not supported");
    }

    /*
     * By default, values cannot be used as keys. A subtype of Value can be used as a key if it has an equals() and
     * hashCode() based on consistent and immutable state, which may or may not be its content.
     */
    default boolean isValidKey() {
        return false;
    }

    default Bool lessThan(Value operand) {
        throw new UnsupportedOperationException("< not supported");
    }

    default Bool lessThanOrEqualTo(Value operand) {
        throw new UnsupportedOperationException("<= not supported");
    }

    default Value modulo(Value divisor) {
        throw new UnsupportedOperationException("% not supported");
    }

    default Value multiply(Value multiplicand) {
        throw new UnsupportedOperationException("* not supported");
    }

    default Value negate() {
        throw new UnsupportedOperationException("Negate not supported");
    }

    default Value not() {
        throw new UnsupportedOperationException("! not supported");
    }

    @Override
    default Value resolveValue(Env env) {
        return this;
    }

    @Override
    default Value resolveValue() {
        return this;
    }

    @Override
    default ValueOrIdent resolveValueOrIdent(Env env) {
        return this;
    }

    @Override
    default ValueOrVar resolveValueOrVar(Env env) {
        return this;
    }

    @Override
    default ValueOrVar resolveValueOrVar() {
        return this;
    }

    default Value subtract(Value subtrahend) {
        throw new UnsupportedOperationException("Subtract not supported");
    }

    default Rec unifyRecs(Rec rec, Set<Memo> memos) throws WaitVarException {
        throw new UnificationError(this, rec);
    }

}
