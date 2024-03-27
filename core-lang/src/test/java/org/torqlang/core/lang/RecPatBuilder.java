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

public final class RecPatBuilder {

    private final List<FieldPat> fieldPats;
    private boolean partialArity;
    private LabelPat labelPat;

    private RecPatBuilder() {
        fieldPats = new ArrayList<>();
    }

    public static RecPatBuilder builder() {
        return new RecPatBuilder();
    }

    public final RecPatBuilder addFieldPat(FeaturePat featurePat, Pat valuePat) {
        fieldPats.add(new FieldPat(featurePat, valuePat, SourceSpan.emptySourceSpan()));
        return this;
    }

    public final RecPat build() {
        return new RecPat(labelPat, fieldPats, partialArity, SourceSpan.emptySourceSpan());
    }

    public final List<FieldPat> fieldPats() {
        return fieldPats;
    }

    public final LabelPat labelPat() {
        return labelPat;
    }

    public final boolean partialArity() {
        return partialArity;
    }

    public final RecPatBuilder setLabelPat(LabelPat labelPat) {
        this.labelPat = labelPat;
        return this;
    }

    public final RecPatBuilder setPartialArity(boolean partialArity) {
        this.partialArity = partialArity;
        return this;
    }

}
