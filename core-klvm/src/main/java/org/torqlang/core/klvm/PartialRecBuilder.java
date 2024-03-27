/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.ArrayList;
import java.util.List;

public final class PartialRecBuilder {

    private LiteralOrVar label;
    private boolean completeOnly = true;
    private final List<Field> fields = new ArrayList<>();

    PartialRecBuilder() {
    }

    public final PartialRecBuilder addField(Feature feature, ValueOrVar valueOrVar) {
        if (valueOrVar instanceof Complete complete) {
            addField(new CompleteField(feature, complete));
        } else {
            addField(new PartialField(feature, valueOrVar));
        }
        return this;
    }

    public final PartialRecBuilder addField(Field field) {
        if (!(field instanceof CompleteField)) {
            completeOnly = false;
        }
        fields.add(field);
        return this;
    }

    public final PartialRecBuilder addField(FeatureOrVar featureOrVar, ValueOrVar valueOrVar) {
        if (featureOrVar instanceof Feature feature) {
            addField(feature, valueOrVar);
        } else {
            addField(new FutureField((Var) featureOrVar, valueOrVar));
        }
        return this;
    }

    public final Rec build() {
        if (isComplete()) {
            List<CompleteField> completeFields = new ArrayList<>();
            for (Field f : fields) {
                completeFields.add((CompleteField) f);
            }
            return CompleteRec.create((Literal) label, completeFields);
        }
        // At this point we know we have either an undetermined record or a determined partial record
        List<FutureField> futureFields = new ArrayList<>(fields.size());
        List<PartialField> partialFields = new ArrayList<>(fields.size());
        for (Object f : fields) {
            if (f instanceof CompleteField cf) {
                partialFields.add(new PartialField(cf.feature, cf.value));
            } else if (f instanceof PartialField pf) {
                partialFields.add(pf);
            } else {
                futureFields.add((FutureField) f);
            }
        }
        return BasicPartialRec.createPrivatelyForKlvm(label, futureFields, partialFields);
    }

    private boolean isComplete() {
        return isLiteralOrNull(label) && completeOnly;
    }

    private boolean isLiteralOrNull(LiteralOrVar label) {
        return label == null || label instanceof Literal;
    }

    public final PartialRecBuilder setLabel(LiteralOrVar label) {
        this.label = label;
        return this;
    }

}
