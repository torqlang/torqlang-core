/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.util.SourceSpan;

import java.util.Set;

@SuppressWarnings("ClassCanBeRecord")
public final class FieldPtn implements Decl {

    public final FeatureOrIdentPtn feature;
    public final ValueOrIdentPtn value;
    public final SourceSpan sourceSpan;

    public FieldPtn(FeatureOrIdentPtn feature, ValueOrIdentPtn value, SourceSpan sourceSpan) {
        this.feature = feature;
        this.value = value;
        this.sourceSpan = sourceSpan;
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state) throws Exception {
        return visitor.visitFieldPtn(this, state);
    }

    @Override
    public final void captureLexicallyFree(Set<Ident> knownBound, Set<Ident> lexicallyFree) {
        ValueOrIdentPtn.captureLexicallyFree(feature, knownBound, lexicallyFree);
        ValueOrIdentPtn.captureLexicallyFree(value, knownBound, lexicallyFree);
    }

    @Override
    public final String toString() {
        return toKernelString();
    }

}
