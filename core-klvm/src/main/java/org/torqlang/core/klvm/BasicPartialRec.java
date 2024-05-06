/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.util.BinarySearchTools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;

final class BasicPartialRec implements PartialRec {

    private final List<FutureField> futureFields;
    private final List<PartialField> partialFields;
    private final int totalFieldCount;
    private Var futureLabel;
    private Literal label;

    private BasicPartialRec(LiteralOrVar literalOrVar, List<FutureField> futureFields, List<PartialField> partialFields) {
        if (literalOrVar == null) {
            label = Rec.DEFAULT_LABEL;
        } else if (literalOrVar instanceof Literal literal) {
            label = literal;
        } else {
            futureLabel = (Var) literalOrVar;
        }
        this.futureFields = futureFields;
        this.partialFields = partialFields;
        this.totalFieldCount = futureFields.size() + partialFields.size();
        if (futureFields.isEmpty()) {
            partialFields.sort(FeatureProviderComparator.comparator());
        }
        sweepUndeterminedVars();
        checkForDuplicateFeatures();
    }

    static BasicPartialRec createPrivatelyForKlvm(LiteralOrVar label, List<FutureField> futureFields, List<PartialField> partialFields) {
        return new BasicPartialRec(label, futureFields, partialFields);
    }

    /*
     * Return the index of the field if found, otherwise return -(low + 1).
     * Undetermined fields (future fields) are not searched.
     */
    private int binarySearchFields(Feature feature) {
        return BinarySearchTools.search(partialFields, (f) -> FEATURE_COMPARATOR.compare(feature, f.feature));
    }

    @Override
    public final Complete checkComplete() throws WaitVarException {
        return checkComplete(new IdentityHashMap<>());
    }

    @Override
    public final Complete checkComplete(IdentityHashMap<Rec, Complete> memos) throws WaitVarException {
        Complete previous = memos.get(this);
        if (previous != null) {
            return previous;
        }
        checkDetermined();
        // If we get here, we know the label and features are determined
        BasicCompleteRec thisCompleteRec = BasicCompleteRec.instanceForRestore();
        // Place an empty complete value into memos so that circular references can be closed
        memos.put(this, thisCompleteRec);
        CompleteField[] thisCompleteFields = new CompleteField[partialFields.size()];
        for (int i = 0; i < partialFields.size(); i++) {
            // We know we have partial fields (as opposed to future fields) because sweepUndeterminedVars did
            // not throw a WaitException
            PartialField partialField = partialFields.get(i);
            // Resolve will suspend the current stack if the value is not yet bound
            Value value = partialField.value.resolveValue();
            Complete completeValue;
            if (value instanceof Partial partial) {
                completeValue = partial.checkComplete(memos);
            } else {
                // If we get here, we have a complete record, non-record value, or variable. Some values can never be
                // complete, so this check may throw an exception other than a WaitException.
                completeValue = value.checkComplete();
            }
            CompleteField completeField = new CompleteField(partialField.feature, completeValue);
            thisCompleteFields[i] = completeField;
        }
        thisCompleteRec.restore(label, thisCompleteFields);
        return thisCompleteRec;
    }

    private void checkForDuplicateFeatures() {
        PartialField prev = null;
        for (PartialField f : partialFields) {
            if (prev != null) {
                if (FeatureProviderComparator.SINGLETON.compare(prev, f) == 0) {
                    throw new DuplicateFeatureError(this, f.feature);
                }
            }
            prev = f;
        }
    }

    @Override
    public final Feature featureAt(int i) {
        return partialFields.get(i).feature;
    }

    @Override
    public final PartialField fieldAt(int index) {
        return partialFields.get(index);
    }

    @Override
    public final int fieldCount() {
        return partialFields.size();
    }

    /**
     * Use binarySearchFields() to return the field if found, otherwise return null.
     */
    private PartialField findField(Feature feature) {
        int index = binarySearchFields(feature);
        return index > -1 ? partialFields.get(index) : null;
    }

    /**
     * Return the value at feature. If not found, return null. This implementation uses findField() to return the
     * value or null if the values is not found.
     */
    @Override
    public final ValueOrVar findValue(Feature feature) {
        PartialField f = findField(feature);
        return f == null ? null : f.value;
    }

    @Override
    public final int futureFieldCount() {
        return futureFields.size();
    }

    @Override
    public final Var futureLabel() {
        return futureLabel;
    }

    @Override
    public final Literal label() {
        return label;
    }

    @Override
    public final ValueOrVar select(Feature feature) throws WaitException {
        checkDetermined();
        ValueOrVar result = findValue(feature);
        if (result == null) {
            throw new FeatureNotFoundError(this, feature);
        }
        return result;
    }

    /**
     * At this point, two fields of two records have been unified. This method is called so that each record can store
     * the unified valueOrVar. Accepting the given ValueOrVar can result in replacing a Var with a Value or dropping
     * one of two duplicate values.
     */
    @Override
    public final void setUnifiedValue(int index, ValueOrVar unifiedValueOrVar) {
        PartialField currentField = partialFields.get(index);
        if (currentField.value != unifiedValueOrVar) {
            partialFields.set(index, new PartialField(currentField.feature, unifiedValueOrVar));
        }
    }

    /**
     * Visit unbound label and features checking if they are now bound. Return a list of label and feature variables
     * that are still unbound. If a newly bound feature is encountered, also check if the field's value is now bound.
     * Replace undetermined fields with a determined fields as they are encountered.
     */
    @Override
    public final Collection<Var> sweepUndeterminedVars() {
        if (label != null && futureFields.isEmpty()) {
            return EMPTY_VAR_COLLECTION;
        }
        List<Var> answer = new ArrayList<>(futureFields.size() + 1);
        if (label == null) {
            ValueOrVar labelRes = futureLabel.resolveValueOrVar();
            if (labelRes instanceof Literal literal) {
                label = literal;
                futureLabel = null;
            } else {
                answer.add((Var) labelRes);
            }
        }
        if (!futureFields.isEmpty()) {
            List<FutureField> nowDetermined = new ArrayList<>(futureFields.size());
            for (FutureField ff : futureFields) {
                ValueOrVar featureRes = ff.feature.resolveValueOrVar();
                if (featureRes instanceof Feature feature) {
                    ValueOrVar valueRes = ff.value.resolveValueOrVar();
                    partialFields.add(new PartialField(feature, valueRes));
                    nowDetermined.add(ff);
                } else {
                    answer.add((Var) featureRes);
                }
            }
            futureFields.removeAll(nowDetermined);
            if (futureFields.isEmpty()) {
                // All fields are now determined, so sort and check for duplicates
                partialFields.sort(FeatureProviderComparator.comparator());
                checkForDuplicateFeatures();
            }
        }
        return answer;
    }

    @Override
    public final String toString() {
        return toKernelString();
    }

    @Override
    public final int totalFieldCount() {
        return totalFieldCount;
    }

    @Override
    public final int unificationPriority() {
        return UnificationPriority.PARTIAL_REC;
    }

    @Override
    public final ValueOrVar valueAt(int i) {
        return partialFields.get(i).value;
    }

}
