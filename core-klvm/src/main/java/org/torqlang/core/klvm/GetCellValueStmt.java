/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.klvm.CellPack.CellObj;
import org.torqlang.core.util.SourceSpan;

import java.util.Set;

public final class GetCellValueStmt extends AbstractStmt {

    public final Ident cell;
    public final Ident target;

    public GetCellValueStmt(Ident cell, Ident target, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.cell = cell;
        this.target = target;
    }

    public final CompleteOrIdent a() {
        return cell;
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitGetCellValueStmt(this, state);
    }

    @Override
    public final void captureLexicallyFree(Set<Ident> knownBound, Set<Ident> lexicallyFree) {
        CompleteOrIdent.captureLexicallyFree(cell, knownBound, lexicallyFree);
        Ident.captureLexicallyFree(target, knownBound, lexicallyFree);
    }

    @Override
    public void compute(Env env, Machine machine) throws WaitException {
        CellObj cellObj = (CellObj) cell.resolveValue(env);
        ValueOrVar targetRes = target.resolveValueOrVar(env);
        targetRes.bindToValueOrVar(cellObj.get(), null);
    }

}
