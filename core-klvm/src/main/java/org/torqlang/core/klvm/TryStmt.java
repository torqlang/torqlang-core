/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.util.SourceSpan;

import java.util.HashSet;
import java.util.Set;

public class TryStmt extends AbstractStmt {

    public final Stmt body;
    public final Stmt catchStmt;

    public TryStmt(Stmt body, Stmt catchStmt, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.body = body;
        this.catchStmt = catchStmt;
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitTryStmt(this, state);
    }

    @Override
    public final void captureLexicallyFree(Set<Ident> knownBound, Set<Ident> lexicallyFree) {
        // Copy knownBound to hide out-of-scope identifiers from the catch statement
        body.captureLexicallyFree(new HashSet<>(knownBound), lexicallyFree);
        // No need to copy knownBound since there are no more peer statements
        catchStmt.captureLexicallyFree(knownBound, lexicallyFree);
    }

    @Override
    public final void compute(Env env, Machine machine) throws WaitException {
        // CRITICAL: CatchStmt cannot be nested inside another statement, such as a 'local'
        catchStmt.pushStackEntries(machine, env);
        body.pushStackEntries(machine, env);
    }

}
