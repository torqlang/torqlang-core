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
public final class FieldDef implements Decl {

    public final FeatureOrIdent feature;
    public final CompleteOrIdent value;
    public final SourceSpan sourceSpan;

    public FieldDef(FeatureOrIdent feature, CompleteOrIdent value, SourceSpan sourceSpan) {
        this.feature = feature;
        this.value = value;
        this.sourceSpan = sourceSpan;
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state) throws Exception {
        return visitor.visitFieldDef(this, state);
    }

    @Override
    public final void captureLexicallyFree(Set<Ident> knownBound, Set<Ident> lexicallyFree) {
        CompleteOrIdent.captureLexicallyFree(feature, knownBound, lexicallyFree);
        CompleteOrIdent.captureLexicallyFree(value, knownBound, lexicallyFree);
    }

    @Override
    public final String toString() {
        return toKernelString();
    }

}
