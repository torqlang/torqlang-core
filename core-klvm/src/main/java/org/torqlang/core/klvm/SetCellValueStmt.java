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

public final class SetCellValueStmt extends AbstractStmt {

    public final Ident cell;
    public final CompleteOrIdent value;

    public SetCellValueStmt(Ident cell, CompleteOrIdent value, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.cell = cell;
        this.value = value;
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitSetCellValueStmt(this, state);
    }

    @Override
    public final void captureLexicallyFree(Set<Ident> knownBound, Set<Ident> lexicallyFree) {
        CompleteOrIdent.captureLexicallyFree(cell, knownBound, lexicallyFree);
        CompleteOrIdent.captureLexicallyFree(value, knownBound, lexicallyFree);
    }

    public final CompleteOrIdent cell() {
        return cell;
    }

    @Override
    public void compute(Env env, Machine machine) throws WaitException {
        CellObj cellObj = (CellObj) cell.resolveValue(env);
        ValueOrVar valueRes = value.resolveValueOrVar(env);
        cellObj.set(valueRes);
    }

    public final CompleteOrIdent value() {
        return value;
    }

}
