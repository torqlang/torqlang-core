/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.util.BinarySearchTools;

import java.util.Arrays;

public abstract class AbstractCompleteRec implements CompleteRec {

    private CompleteField[] completeFields;
    private Literal label;
    private int hashCode;
    private boolean hashCodeIsZero;

    /*
     * Return the index of the field if found, otherwise return -(low + 1).
     */
    private int binarySearchFields(Feature feature) {
        return BinarySearchTools.search(completeFields, (f) -> FEATURE_COMPARATOR.compare(feature, f.feature));
    }

    private void checkForDuplicateFeatures() {
        CompleteField prev = null;
        for (CompleteField f : completeFields) {
            if (prev != null) {
                if (FeatureProviderComparator.SINGLETON.compare(prev, f) == 0) {
                    throw new DuplicateFeatureError(this, f.feature);
                }
            }
            prev = f;
        }
    }

    @Override
    public final boolean equals(Object other) {
        return equalsComplete(other);
    }

    @Override
    public final Feature featureAt(int i) {
        return completeFields[i].feature;
    }

    @Override
    public final CompleteField fieldAt(int index) {
        return completeFields[index];
    }

    @Override
    public final int fieldCount() {
        return completeFields.length;
    }

    /*
     * Use binarySearchFields() to return the field if found, otherwise return null.
     */
    private CompleteField findField(Feature feature) {
        int index = binarySearchFields(feature);
        return index > -1 ? completeFields[index] : null;
    }

    /*
     * Return the value at feature. If not found, return null. This implementation uses findField() to return the
     * value or null if the value is not found.
     */
    @Override
    public final Complete findValue(Feature feature) {
        CompleteField f = findField(feature);
        return f == null ? null : f.value;
    }

    @Override
    public final int hashCode() {
        int h = hashCode;
        if (h == 0 && !hashCodeIsZero) {
            h = hashCodeComplete(null);
            if (h == 0) {
                hashCodeIsZero = true;
            } else {
                hashCode = h;
            }
        }
        return h;
    }

    /*
     * A complete record is a valid key
     */
    @Override
    public final boolean isValidKey() {
        return true;
    }

    @Override
    public final Literal label() {
        return label;
    }

    void restore(Literal label, CompleteField[] completeFields) {
        this.label = label == null ? Rec.DEFAULT_LABEL : label;
        this.completeFields = completeFields;
        Arrays.sort(completeFields, FeatureProviderComparator.comparator());
        checkForDuplicateFeatures();
    }

    @Override
    public final Complete select(Feature feature) {
        Complete result = findValue(feature);
        if (result == null) {
            throw new FeatureNotFoundError(this, feature);
        }
        return result;
    }

    /*
     * When called, two fields of two records have been unified. This method is called on each record to store the
     * unified value or variable. Accepting the given argument can result in replacing a variable with a value or
     * dropping one of two duplicate values.
     */
    @Override
    public final void setUnifiedValue(int index, ValueOrVar unifiedValueOrVar) {
        CompleteField currentField = completeFields[index];
        if (currentField.value != unifiedValueOrVar) {
            completeFields[index] = new CompleteField(currentField.feature, (Complete) unifiedValueOrVar);
        }
    }

    @Override
    public final String toString() {
        return toKernelString();
    }

    @Override
    public final Complete valueAt(int i) {
        return completeFields[i].value;
    }

}
