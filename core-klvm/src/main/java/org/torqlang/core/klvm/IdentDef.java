/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.Set;

public final class IdentDef implements Decl {

    public final Ident ident;
    public final Complete value;

    public IdentDef(Ident ident, Complete value) {
        this.ident = ident;
        this.value = value;
    }

    public IdentDef(Ident ident) {
        this(ident, null);
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state) throws Exception {
        return visitor.visitIdentDef(this, state);
    }

    @Override
    public final void captureLexicallyFree(Set<Ident> knownBound, Set<Ident> lexicallyFree) {
        knownBound.add(ident);
    }

    @Override
    public final String toString() {
        return toKernelString();
    }

}
