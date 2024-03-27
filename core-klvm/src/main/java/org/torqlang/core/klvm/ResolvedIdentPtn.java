/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

public final class ResolvedIdentPtn implements ResolvedPtn {

    public final Ident ident;

    public ResolvedIdentPtn(Ident ident) {
        this.ident = ident;
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitResolvedIdentPtn(this, state);
    }

    @Override
    public final String toString() {
        return toKernelString();
    }

}
