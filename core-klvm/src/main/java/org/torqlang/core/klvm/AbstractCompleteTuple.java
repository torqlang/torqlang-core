/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.Collection;
import java.util.Collections;

public abstract class AbstractCompleteTuple extends AbstractTuple implements CompleteTuple {

    private Literal label;
    private Complete[] values;
    private int hashCode;
    private boolean hashCodeIsZero;

    @Override
    public final void addAllTo(Collection<? super Complete> collection) {
        Collections.addAll(collection, values);
    }

    @Override
    public final boolean equals(Object other) {
        return equalsComplete(other);
    }

    @Override
    public final CompleteField fieldAt(int index) {
        return new CompleteField(Int32.of(index), values[index]);
    }

    @Override
    public final int fieldCount() {
        return values.length;
    }

    /*
     * Return the value at feature. If not found, return null.
     */
    @Override
    public final Complete findValue(Feature feature) {
        if (!(feature instanceof Int64 int64)) {
            return null;
        }
        int index = int64.intValue();
        return (index > -1 && index < values.length) ? values[index] : null;
    }

    @Override
    public final int hashCode() {
        int h = hashCode;
        if (h == 0 && !this.hashCodeIsZero) {
            h = hashCodeComplete(null);
            if (h == 0) {
                hashCodeIsZero = true;
            } else {
                hashCode = h;
            }
        }
        return h;
    }

    @Override
    public final boolean isValidKey() {
        return true;
    }

    @Override
    public final Literal label() {
        return label;
    }

    void restore(Literal label, Complete[] values) {
        this.label = label == null ? Rec.DEFAULT_LABEL : label;
        this.values = values;
    }

    @Override
    public final Complete select(Feature feature) {
        return valueAt(featureToIndex(feature, values.length));
    }

    /*
     * When called, two fields of two records have been unified. This method is called on each record to store the
     * unified value or variable. Accepting the given argument can result in replacing a variable with a value or
     * dropping one of two duplicate values.
     */
    @Override
    public final void setUnifiedValue(int index, ValueOrVar unifiedValueOrVar) {
        Complete currentValue = values[index];
        if (currentValue != unifiedValueOrVar) {
            values[index] = (Complete) unifiedValueOrVar;
        }
    }

    @Override
    public final Complete valueAt(int i) {
        return values[i];
    }

}
