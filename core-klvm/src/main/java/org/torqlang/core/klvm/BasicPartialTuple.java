/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;

final class BasicPartialTuple extends AbstractTuple implements PartialTuple {

    private final ValueOrVar[] values;
    private Var futureLabel;
    private Literal label;

    private BasicPartialTuple(LiteralOrVar literalOrVar, ValueOrVar[] values) {
        if (literalOrVar == null) {
            label = Rec.DEFAULT_LABEL;
        } else if (literalOrVar instanceof Literal literal) {
            label = literal;
        } else {
            futureLabel = (Var) literalOrVar;
        }
        this.values = values;
        sweepUndeterminedVars();
    }

    static BasicPartialTuple createPrivatelyForKlvm(LiteralOrVar label, ValueOrVar[] values) {
        return new BasicPartialTuple(label, values);
    }

    @Override
    public final Complete checkComplete() throws WaitVarException {
        return checkComplete(new IdentityHashMap<>());
    }

    @Override
    public final Complete checkComplete(IdentityHashMap<Partial, Complete> memos) throws WaitVarException {
        Complete previous = memos.get(this);
        if (previous != null) {
            return previous;
        }
        sweepUndeterminedVars();
        // If we get here, we know the label and features are determined
        BasicCompleteTuple thisCompleteTuple = BasicCompleteTuple.instanceForRestore();
        // Place an empty complete value into memos so that circular references can be closed
        memos.put(this, thisCompleteTuple);
        Complete[] completeValues = new Complete[values.length];
        for (int i = 0; i < values.length; i++) {
            ValueOrVar valueOrVar = values[i];
            // Resolve will suspend the current stack if the value is not yet bound
            Value value = valueOrVar.resolveValue();
            Complete completeValue;
            if (value instanceof Partial partial) {
                completeValue = partial.checkComplete(memos);
            } else {
                // If we get here, we have a complete record, non-record value, or variable. Some values can never be
                // complete, so this check may throw an exception other than a WaitException.
                completeValue = value.checkComplete();
            }
            completeValues[i] = completeValue;
        }
        thisCompleteTuple.restore(label, completeValues);
        return thisCompleteTuple;
    }

    public final PartialField fieldAt(int index) {
        return new PartialField(Int32.of(index), values[index]);
    }

    @Override
    public final int fieldCount() {
        return values.length;
    }

    /**
     * Return the value at feature. If not found, return null.
     */
    @Override
    public final ValueOrVar findValue(Feature feature) {
        if (!(feature instanceof Int64 int64)) {
            return null;
        }
        int index = int64.intValue();
        return (index > -1 && index < values.length) ? values[index] : null;
    }

    @Override
    public final int futureFieldCount() {
        return 0;
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
    public final ValueOrVar select(Feature feature) {
        return valueAt(featureToIndex(feature, values.length));
    }

    /**
     * At this point, two fields of two records have been unified. This method is called so that each record can store
     * the unified valueOrVar. Accepting the given ValueOrVar can result in replacing a Var with a Value or dropping
     * one of two duplicate values.
     */
    @Override
    public final void setUnifiedValue(int index, ValueOrVar unifiedValueOrVar) {
        values[index] = unifiedValueOrVar;
    }

    /**
     * Visit unbound label and features checking if they are now bound. Return a list of label and feature variables
     * that are still unbound. Since a Tuple has no explicit features, we only have to consider the label.
     */
    @Override
    public final Collection<Var> sweepUndeterminedVars() {
        if (label != null) {
            return EMPTY_VAR_COLLECTION;
        }
        ValueOrVar labelRes = futureLabel.resolveValueOrVar();
        if (labelRes instanceof Literal literal) {
            label = literal;
            futureLabel = null;
            return EMPTY_VAR_COLLECTION;
        }
        return Collections.singleton((Var) labelRes);
    }

    @Override
    public final int totalFieldCount() {
        return fieldCount();
    }

    @Override
    public final int unificationPriority() {
        return UnificationPriority.PARTIAL_TUPLE;
    }

    @Override
    public final ValueOrVar valueAt(int i) {
        return values[i];
    }

}
