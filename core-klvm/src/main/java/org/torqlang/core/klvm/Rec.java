/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.*;

/*
 * Algorithms over records exploit the fact that record arity is sorted. All features are sorted according
 * to FeatureComparator. Two records with the same arity can be easily compared by iterating the records in
 * tandem by index, and unification of two records can only proceed if the records have the same arity.
 */
public interface Rec extends Composite, FieldIterSource, ValueIterSource {

    Collection<Var> EMPTY_VAR_COLLECTION = Collections.emptyList();
    FeatureComparator FEATURE_COMPARATOR = FeatureComparator.SINGLETON;

    Null DEFAULT_LABEL = Null.SINGLETON;

    String $LABEL = "$label";
    String $REC = "$rec";

    static CompleteRecBuilder completeRecBuilder() {
        return new CompleteRecBuilder();
    }

    static CompleteRecBuilder completeRecBuilder(Literal label, List<CompleteField> fields) {
        return new CompleteRecBuilder(label, fields);
    }

    static CompleteTupleBuilder completeTupleBuilder() {
        return new CompleteTupleBuilder();
    }

    static PartialRecBuilder partialRecBuilder() {
        return new PartialRecBuilder();
    }

    static PartialTupleBuilder partialTupleBuilder() {
        return new PartialTupleBuilder();
    }

    @Override
    default <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitRec(this, state);
    }

    /*
     * Unify records -- Binding vars turns into unifying when both vars are actually values
     */
    @Override
    default Rec bindToValue(Value value, Set<Memo> memos) throws WaitVarException {
        return value.unifyRecs(this, memos);
    }

    /*
     * This method is a polymorphic callback requesting "case nonRecValue of Rec then..."
     */
    @Override
    default ValueOrResolvedPtn caseNonRecOfThis(Value nonRecValue, Env env) {
        return null;
    }

    @Override
    default ValueOrResolvedPtn caseOf(ValueOrPtn valueOrPtn, Env env) throws WaitException {
        return valueOrPtn.caseRecOfThis(this, env);
    }

    /*
     * This method is a polymorphic callback requesting "case Rec of Rec then..."
     */
    @Override
    default ValueOrResolvedPtn caseRecOfThis(Rec rec, Env env) throws WaitException {
        return rec.entails(this, null) ? this : null;
    }

    /*
     * Called before attempting to use a feature or a feature-value pair:
     *     - Rec::entailsRec() -- checking entailment
     *     - Rec::unifyRecs() -- unifying records
     *     - Rec::valueIter() -- creating iterators
     *     - BasicPartialRec::select() -- selecting by feature
     *     - BasicPartialRec::checkComplete() -- converting to complete
     *     - BasicRecPtn::caseRecOfThis() -- matching a pattern
     *     - RecClsAssign::apply() -- utility procedures
     *     - KernelFormatter::visitRec() -- formatting records
     */
    default void checkDetermined() throws WaitVarException {
        Collection<Var> undetermined = sweepUndeterminedVars();
        if (!undetermined.isEmpty()) {
            Var any = undetermined.iterator().next();
            throw new WaitVarException(any);
        }
    }

    /*
     * This method is the second step of a two-step process. The first step is to determine if a value matches the
     * structure of a pattern. If the structures match, we use this method to deconstruct the record according to the
     * pattern.
     *
     * The parameter `valueOrResolvedPtn` must be a `ResolvedRecPtn` produced by `Value#caseOf(ValueOrPtn, Env)`.
     */
    @Override
    default Env deconstruct(ValueOrResolvedPtn valueOrResolvedPtn, Env env) {
        ResolvedRecPtn resRecPtn = (ResolvedRecPtn) valueOrResolvedPtn;
        // E + {<x>1 -> E(<x>).<feat>1,...,<x>n -> E(<x>).<feat>n}
        List<EnvEntry> bindings = new ArrayList<>(resRecPtn.fields.size());
        for (ResolvedFieldPtn fieldPtn : resRecPtn.fields) {
            if (fieldPtn.value instanceof Ident valueIdent) {
                ValueOrVar valueOrVar = findValue(fieldPtn.feature);
                if (valueOrVar instanceof Value value) {
                    bindings.add(new EnvEntry(valueIdent, new Var(value)));
                } else {
                    bindings.add(new EnvEntry(valueIdent, (Var) valueOrVar));
                }
            }
        }
        return Env.create(env, bindings);
    }

    @Override
    default boolean entails(Value operand, Set<Memo> memos) throws WaitVarException {
        return operand.entailsRec(this, memos);
    }

    @Override
    default boolean entailsRec(Rec other, Set<Memo> memos) throws WaitVarException {
        if (this == other) {
            return true;
        }
        checkDetermined();
        other.checkDetermined();
        Memo here = new Memo(this, other);
        if (memos == null) {
            memos = new HashSet<>();
        } else if (memos.contains(here)) {
            return true;
        }
        memos.add(here);
        if (!this.label().equals(other.label())) {
            return false;
        }
        if (!equalFeatures(other)) {
            return false;
        }
        int fc = fieldCount();
        for (int i = 0; i < fc; i++) {
            boolean entails = valueAt(i).entailsValueOrVar(other.valueAt(i), memos);
            if (!entails) {
                return false;
            }
        }
        return true;
    }

    /*
     * PRECONDITION: the features of this record and the other record must be determined.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    default boolean equalFeatures(Rec other) {
        int fc = fieldCount();
        if (fc != other.fieldCount()) {
            return false;
        }
        for (int i = 0; i < fc; i++) {
            if (!featureAt(i).equals(other.featureAt(i))) {
                return false;
            }
        }
        return true;
    }

    /*
     * Return the feature at the arity index
     */
    Feature featureAt(int index);

    Field fieldAt(int index);

    int fieldCount();

    @Override
    default ValueOrVar fieldIter() throws WaitException {
        checkDetermined();
        return new RecFieldIter(this);
    }

    /*
     * Return the value at feature. If not found, return null.
     */
    ValueOrVar findValue(Feature feature);

    Literal label();

    /*
     * At this point, two fields of two records have been unified. This method is called so that each record can store
     * the unified valueOrVar. Accepting the given ValueOrVar can result in replacing a Var with a Value or dropping
     * one of two duplicate values.
     */
    void setUnifiedValue(int index, ValueOrVar unifiedValueOrVar);

    /*
     * Replace current undetermined label and feature variables with newly available memory values. This method will
     * be called repeatedly to ensure that a record that can be determined is determined.
     */
    default Collection<Var> sweepUndeterminedVars() {
        // The default returns an empty list because Literal and Tuple are always determined
        return EMPTY_VAR_COLLECTION;
    }

    int unificationPriority();

    /*
     * Unification of two records can only proceed if the records have the same arity. Therefore, we can
     * unify any two record values by index (position in the arity) without specifying the actual feature.
     */
    default void unifyFields(Rec other, int index, Set<Memo> memos) throws WaitVarException {
        ValueOrVar thisValue = valueAt(index);
        ValueOrVar otherValue = other.valueAt(index);
        ValueOrVar unifiedValueOrVar = thisValue.bindToValueOrVar(otherValue, memos);
        setUnifiedValue(index, unifiedValueOrVar);
        other.setUnifiedValue(index, unifiedValueOrVar);
    }

    /*
     * At this point in the binding procedure we know that both arguments are records. Now our objective is to
     * unify them. In the end, unification should return the more efficient implementation of the two arguments.
     */
    @Override
    default Rec unifyRecs(Rec other, Set<Memo> memos) throws WaitVarException {
        if (this == other) {
            return this;
        }
        checkDetermined();
        other.checkDetermined();
        Memo here = new Memo(this, other);
        if (memos == null) {
            memos = new HashSet<>();
        } else if (memos.contains(here)) {
            return this;
        }
        memos.add(here);
        if (!this.label().equals(other.label())) {
            throw new UnificationError(this, other.label());
        }
        if (!equalFeatures(other)) {
            throw new UnificationError(this, other);
        }
        int fc = fieldCount();
        for (int i = 0; i < fc; i++) {
            unifyFields(other, i, memos);
        }
        return other.unificationPriority() > unificationPriority() ? other : this;
    }

    /*
     * Return the value at the arity index
     */
    ValueOrVar valueAt(int index);

    @Override
    default ValueOrVar valueIter() throws WaitException {
        checkDetermined();
        return new RecValueIter(this);
    }

    class RecFieldIter implements FieldIter {

        private final Rec rec;
        private int nextIndex = 0;

        RecFieldIter(Rec rec) {
            this.rec = rec;
        }

        @Override
        public void apply(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
            if (ys.size() != FIELD_ITER_ARG_COUNT) {
                throw new InvalidArgCountError(FIELD_ITER_ARG_COUNT, ys, this);
            }
            ValueOrVar next;
            int size = rec.fieldCount();
            if (nextIndex < size) {
                Field nextField = rec.fieldAt(nextIndex);
                next = PartialTuple.create(null, List.of(nextField.feature(), nextField.value()));
                nextIndex++;
            } else {
                next = Eof.SINGLETON;
            }
            ValueOrVar target = ys.get(0).resolveValueOrVar(env);
            target.bindToValueOrVar(next, null);
        }

    }

    class RecValueIter implements ValueIter {

        private final Rec rec;
        private int nextIndex = 0;

        RecValueIter(Rec rec) {
            this.rec = rec;
        }

        @Override
        public void apply(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
            if (ys.size() != VALUE_ITER_ARG_COUNT) {
                throw new InvalidArgCountError(VALUE_ITER_ARG_COUNT, ys, this);
            }
            ValueOrVar next;
            int size = rec.fieldCount();
            if (nextIndex < size) {
                next = rec.valueAt(nextIndex);
                nextIndex++;
            } else {
                next = Eof.SINGLETON;
            }
            ValueOrVar target = ys.get(0).resolveValueOrVar(env);
            target.bindToValueOrVar(next, null);
        }

    }

}
