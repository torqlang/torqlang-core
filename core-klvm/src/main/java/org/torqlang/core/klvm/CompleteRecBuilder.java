/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.ArrayList;
import java.util.List;

public final class CompleteRecBuilder {

    private final List<CompleteField> fields;
    private Literal label;

    CompleteRecBuilder() {
        this.fields = new ArrayList<>();
    }

    CompleteRecBuilder(Literal label, List<CompleteField> fields) {
        this.label = label;
        this.fields = new ArrayList<>(fields);
    }

    public final CompleteRecBuilder addField(CompleteField field) {
        fields.add(field);
        return this;
    }

    public final CompleteRecBuilder addField(Feature feature, Complete value) {
        addField(new CompleteField(feature, value));
        return this;
    }

    public final CompleteRec build() {
        if (fields.isEmpty()) {
            return BasicCompleteTuple.createPrivatelyForKlvm(label, new Complete[0]);
        }
        fields.sort(FeatureProviderComparator.comparator());
        // We know we have a tuple if the last feature is the expected tuple index. This is true because
        // Int features sort before all other types. See FeatureComparator for more information. If there
        // are duplicate features, the record constructor will throw an exception.
        Complete lastFeature = fields.get(fields.size() - 1).feature;
        if (lastFeature instanceof Int64 lastInt && lastInt.longValue() == fields.size() - 1) {
            Complete[] values = new Complete[fields.size()];
            for (int i = 0; i < fields.size(); i++) {
                values[i] = fields.get(i).value;
            }
            return BasicCompleteTuple.createPrivatelyForKlvm(label, values);
        }
        return BasicCompleteRec.createPrivatelyForKlvm(label, fields);
    }

    public final CompleteRecBuilder setLabel(Literal label) {
        this.label = label;
        return this;
    }

}
