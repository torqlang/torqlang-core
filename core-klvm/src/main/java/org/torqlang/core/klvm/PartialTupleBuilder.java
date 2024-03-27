/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.ArrayList;
import java.util.List;

public final class PartialTupleBuilder {

    private final List<ValueOrVar> values = new ArrayList<>();
    private LiteralOrVar label;
    private boolean completeOnly = true;

    PartialTupleBuilder() {
    }

    public final PartialTupleBuilder addValue(ValueOrVar valueOrVar) {
        if (!(valueOrVar instanceof Complete)) {
            completeOnly = false;
        }
        values.add(valueOrVar);
        return this;
    }

    public final Tuple build() {
        if (!isComplete()) {
            return PartialTuple.create(label, values);
        }
        List<Complete> completeValues = new ArrayList<>(values.size());
        for (ValueOrVar valueOrVar : values) {
            completeValues.add((Complete) valueOrVar);
        }
        return CompleteTuple.create((Literal) label, completeValues);
    }

    private boolean isComplete() {
        return isLiteralOrNull(label) && completeOnly;
    }

    private boolean isLiteralOrNull(LiteralOrVar label) {
        return label == null || label instanceof Literal;
    }

    public final PartialTupleBuilder setLabel(LiteralOrVar label) {
        this.label = label;
        return this;
    }

}
