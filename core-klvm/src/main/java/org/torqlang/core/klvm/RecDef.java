/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.util.SourceSpan;

import java.util.List;
import java.util.Set;

import static org.torqlang.core.util.ListTools.nullSafeCopyOf;

@SuppressWarnings("ClassCanBeRecord")
public final class RecDef implements Decl {

    public final LiteralOrIdent label;
    public final List<FieldDef> fieldDefs;
    public final SourceSpan sourceSpan;

    public RecDef(LiteralOrIdent label, List<FieldDef> fieldDefs, SourceSpan sourceSpan) {
        this.label = label;
        this.fieldDefs = nullSafeCopyOf(fieldDefs);
        this.sourceSpan = sourceSpan;
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitRecDef(this, state);
    }

    @Override
    public final void captureLexicallyFree(Set<Ident> knownBound, Set<Ident> lexicallyFree) {
        CompleteOrIdent.captureLexicallyFree(label, knownBound, lexicallyFree);
        for (FieldDef fd : fieldDefs) {
            fd.captureLexicallyFree(knownBound, lexicallyFree);
        }
    }

    public final int fieldCount() {
        return fieldDefs.size();
    }

    public final FieldDef fieldDefAtIndex(int i) {
        return fieldDefs.get(i);
    }

    @Override
    public final String toString() {
        return toKernelString();
    }

}
