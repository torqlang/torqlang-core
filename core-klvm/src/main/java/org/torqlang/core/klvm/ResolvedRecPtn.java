/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.List;

import static org.torqlang.core.util.ListTools.nullSafeCopyOf;

/**
 * Record patterns have to be resolved for each pattern matching case. Patterns are not memory values, but are
 * declarations that change meaning depending on the environment mapping of identifiers to variables.
 */
@SuppressWarnings("ClassCanBeRecord")
public final class ResolvedRecPtn implements ResolvedPtn {

    public final Value label;
    public final List<ResolvedFieldPtn> fields;
    public final boolean partialArity;

    public ResolvedRecPtn(Value label, List<ResolvedFieldPtn> fields, boolean partialArity) {
        this.label = label;
        this.fields = nullSafeCopyOf(fields);
        this.partialArity = partialArity;
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitResolvedRecPtn(this, state);
    }

    public final int fieldCount() {
        return fields.size();
    }

    @Override
    public final String toString() {
        return toKernelString();
    }

}
