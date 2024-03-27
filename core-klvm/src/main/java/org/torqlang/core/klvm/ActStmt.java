/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.util.SourceSpan;

import java.util.Collections;
import java.util.Set;

public class ActStmt extends AbstractStmt {

    public final Stmt stmt;
    public final Ident target;

    public ActStmt(Stmt stmt, Ident target, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.stmt = stmt;
        this.target = target;
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitActStmt(this, state);
    }

    @Override
    public void captureLexicallyFree(Set<Ident> knownBound, Set<Ident> lexicallyFree) {
        Ident.captureLexicallyFree(Ident.ACT, knownBound, lexicallyFree);
        stmt.captureLexicallyFree(knownBound, lexicallyFree);
    }

    @Override
    public void compute(Env env, Machine machine) throws WaitException {
        Proc act = (Proc) env.get(Ident.ACT).resolveValue();
        act.apply(Collections.emptyList(), env, machine);
    }

}
