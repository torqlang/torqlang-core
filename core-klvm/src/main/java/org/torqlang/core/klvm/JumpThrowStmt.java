/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.util.SourceSpan;

import java.util.Set;

public class JumpThrowStmt extends AbstractStmt {

    public final int id;

    public JumpThrowStmt(int id, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.id = id;
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitJumpThrowStmt(this, state);
    }

    @Override
    public final void captureLexicallyFree(Set<Ident> knownBound, Set<Ident> lexicallyFree) {
        // There are no identifiers and there is nothing to do
    }

    @Override
    public final void compute(Env env, Machine machine) {
        machine.unwindToJumpCatchStmt(this);
    }

}
