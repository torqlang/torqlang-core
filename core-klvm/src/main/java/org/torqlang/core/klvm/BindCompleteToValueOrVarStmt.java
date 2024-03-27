/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.util.SourceSpan;

import java.util.Set;

public final class BindCompleteToValueOrVarStmt extends AbstractStmt implements BindStmt {

    public final Complete a;
    public final ValueOrVar x;

    public BindCompleteToValueOrVarStmt(Complete a, ValueOrVar x, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.a = a;
        this.x = x;
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitBindCompleteToValueOrVarStmt(this, state);
    }

    @Override
    public final void captureLexicallyFree(Set<Ident> knownBound, Set<Ident> lexicallyFree) {
    }

    @Override
    public final void compute(Env env, Machine machine) throws WaitException {
        x.bindToValueOrVar(a, null);
    }

}
