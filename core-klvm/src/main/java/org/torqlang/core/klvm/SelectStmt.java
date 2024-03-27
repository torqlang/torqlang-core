/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.util.SourceSpan;

import java.util.Set;

public final class SelectStmt extends AbstractStmt {

    public final CompleteOrIdent rec;
    public final CompleteOrIdent feature;
    public final Ident target;

    public SelectStmt(CompleteOrIdent rec, CompleteOrIdent feature, Ident target, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.rec = rec;
        this.feature = feature;
        this.target = target;
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitSelectStmt(this, state);
    }

    @Override
    public final void captureLexicallyFree(Set<Ident> knownBound, Set<Ident> lexicallyFree) {
        CompleteOrIdent.captureLexicallyFree(rec, knownBound, lexicallyFree);
        CompleteOrIdent.captureLexicallyFree(feature, knownBound, lexicallyFree);
        Ident.captureLexicallyFree(target, knownBound, lexicallyFree);
    }

    @Override
    public final void compute(Env env, Machine machine) throws WaitException {
        Composite recRes = (Composite) rec.resolveValue(env);
        Feature featureRes = (Feature) feature.resolveValue(env);
        ValueOrVar selectedValue = recRes.select(featureRes);

        // CRITICAL: Within this method, DO NOT resolve identifiers to their Value -- stop at Var. We must unify
        //           on Vars so that matching values become just one value in memory.

        Var targetVar = env.get(target);
        if (targetVar == null) {
            throw new IdentNotFoundError(target, this);
        }
        targetVar.bindToValueOrVar(selectedValue, null);
    }

}
