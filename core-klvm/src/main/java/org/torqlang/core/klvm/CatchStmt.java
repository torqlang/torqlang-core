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

/**
 * A CatchStmt is only used internally when a TryStmt pushes it onto the machine stack. A CatchStmt is
 * simply a holder for a CaseStmt.
 */
public class CatchStmt extends AbstractStmt {

    public final Ident arg;
    public final Stmt caseStmt;

    public CatchStmt(Ident arg, Stmt caseStmt, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.arg = arg;
        this.caseStmt = caseStmt;
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitCatchStmt(this, state);
    }

    @Override
    public final void captureLexicallyFree(Set<Ident> knownBound, Set<Ident> lexicallyFree) {
        knownBound.add(arg);
        caseStmt.captureLexicallyFree(knownBound, lexicallyFree);
    }

    @Override
    public final void compute(Env env, Machine machine) throws WaitException {
        // Do nothing -- a catch statement encountered in the normal course of processing is ignored
    }

}
