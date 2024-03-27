/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.util.SourceSpan;

import java.util.ArrayList;
import java.util.List;

public final class TuplePatBuilder {

    private final List<Pat> valuePats;
    private boolean partialArity;
    private LabelPat labelPat;

    private TuplePatBuilder() {
        valuePats = new ArrayList<>();
    }

    public static TuplePatBuilder builder() {
        return new TuplePatBuilder();
    }

    public final TuplePatBuilder addValuePat(Pat valuePat) {
        valuePats.add(valuePat);
        return this;
    }

    public final TuplePat build() {
        return new TuplePat(labelPat, valuePats, partialArity, SourceSpan.emptySourceSpan());
    }

    public final List<Pat> getValuePats() {
        return valuePats;
    }

    public final LabelPat labelPat() {
        return labelPat;
    }

    public final boolean partialArity() {
        return partialArity;
    }

    public final TuplePatBuilder setLabelPat(LabelPat labelPat) {
        this.labelPat = labelPat;
        return this;
    }

    public final TuplePatBuilder setPartialArity(boolean partialArity) {
        this.partialArity = partialArity;
        return this;
    }

}
