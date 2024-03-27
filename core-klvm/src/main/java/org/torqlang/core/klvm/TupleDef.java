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

public final class TupleDef implements Decl {

    public final LiteralOrIdent label;
    public final List<ValueDef> valueDefs;
    public final SourceSpan sourceSpan;

    public TupleDef(LiteralOrIdent label, List<ValueDef> valueDefs, SourceSpan sourceSpan) {
        this.label = label;
        this.valueDefs = nullSafeCopyOf(valueDefs);
        this.sourceSpan = sourceSpan;
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitTupleDef(this, state);
    }

    @Override
    public final void captureLexicallyFree(Set<Ident> knownBound, Set<Ident> lexicallyFree) {
        for (ValueDef vd : valueDefs) {
            vd.captureLexicallyFree(knownBound, lexicallyFree);
        }
    }

    @Override
    public final String toString() {
        return toKernelString();
    }

    public final int valueCount() {
        return valueDefs.size();
    }

    public final ValueDef valueDefAtIndex(int i) {
        return valueDefs.get(i);
    }

}
