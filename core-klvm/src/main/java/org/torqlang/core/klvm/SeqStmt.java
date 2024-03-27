/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.util.SourceSpan;

import java.util.Collection;
import java.util.Set;

public final class SeqStmt extends AbstractStmt {

    public final StmtList seq;

    public SeqStmt(Iterable<Stmt> stmts, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.seq = new StmtList(stmts);
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitSeqStmt(this, state);
    }

    @Override
    public final void captureLexicallyFree(Set<Ident> knownBound, Set<Ident> lexicallyFree) {
        Stmt.captureLexicallyFree(seq, knownBound, lexicallyFree);
    }

    @Override
    public void compute(Env env, Machine machine) {
        machine.pushStackEntries(seq, env);
    }

    @Override
    public void pushStackEntries(Machine machine, Env env) {
        machine.pushStackEntries(this.seq, env);
    }

}
