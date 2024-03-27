/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.Set;

public final class IdentPtn implements Ptn, FeatureOrIdentPtn {

    public final Ident ident;
    public final boolean escaped;

    public IdentPtn(Ident ident, boolean escaped) {
        this.ident = ident;
        this.escaped = escaped;
    }

    public IdentPtn(Ident ident) {
        this(ident, false);
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitIdentPtn(this, state);
    }

    @Override
    public final void captureLexicallyFree(Set<Ident> knownBound, Set<Ident> lexicallyFree) {
        if (escaped) {
            Ident.captureLexicallyFree(ident, knownBound, lexicallyFree);
        } else {
            knownBound.add(ident);
        }
    }

    /*
     * This method is a polymorphic callback requesting "case Value of IdentPtn then..."
     */
    @Override
    public final ValueOrResolvedPtn caseNonRecOfThis(Value nonRecValue, Env env) throws WaitException {
        if (escaped) {
            return nonRecValue.entails(ident.resolveValue(env), null) ? nonRecValue : null;
        }
        return new ResolvedIdentPtn(ident);
    }

    /*
     * This method is a polymorphic callback requesting "case Rec of IdentPtn then..."
     */
    @Override
    public final ValueOrResolvedPtn caseRecOfThis(Rec rec, Env env) throws WaitException {
        if (escaped) {
            return rec.entails(ident.resolveValue(env), null) ? rec : null;
        }
        return new ResolvedIdentPtn(ident);
    }

    @Override
    public final Value resolveValue(Env env) throws WaitException {
        if (!escaped) {
            throw new IllegalArgumentException("An unescaped identifier cannot appear as a pattern label or feature");
        }
        return ident.resolveValue(env);
    }

    @Override
    public final ValueOrIdent resolveValueOrIdent(Env env) throws WaitException {
        return escaped ? ident.resolveValue(env) : ident;
    }

    @Override
    public final String toString() {
        return toKernelString();
    }

}
