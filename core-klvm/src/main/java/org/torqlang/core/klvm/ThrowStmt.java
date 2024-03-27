/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.util.SourceSpan;

import java.util.Set;

public final class ThrowStmt extends AbstractStmt {

    public final CompleteOrIdent error;
    public final Throwable nativeCause;

    public ThrowStmt(CompleteOrIdent error, SourceSpan sourceSpan) {
        this(error, null, sourceSpan);
    }

    public ThrowStmt(CompleteOrIdent error, Throwable nativeCause, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.error = error;
        this.nativeCause = nativeCause;
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitThrowStmt(this, state);
    }

    @Override
    public final void captureLexicallyFree(Set<Ident> knownBound, Set<Ident> lexicallyFree) {
        CompleteOrIdent.captureLexicallyFree(error, knownBound, lexicallyFree);
    }

    @Override
    public final void compute(Env env, Machine machine) {
        ValueOrVar errorValueOrVar = error.resolveValueOrVar(env);
        if (errorValueOrVar instanceof Complete complete) {
            machine.unwindToNextCatchStmt(complete, nativeCause);
        } else if (errorValueOrVar instanceof Var) {
            throw new IllegalArgumentException(
                "This code is attempting to throw an exception with an unbound identifier: " + error +
                    ". The actual exception cannot be thrown.");
        } else {
            throw new IllegalStateException("This code is attempting to throw an exception with a partial value: " +
                errorValueOrVar + ". The actual exception cannot be thrown.");
        }
    }

}
