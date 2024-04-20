/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.Set;

public final class Var implements LiteralOrVar {

    private ValueOrVarSet valueOrVarSet;
    private BindCallback bindCallback;

    public Var(ValueOrVarSet valueOrVarSet) {
        this.valueOrVarSet = valueOrVarSet;
    }

    public Var() {
        // An empty VarSet marks this Var as containing only itself
        this(VarSet.EMPTY_VAR_SET);
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state) throws Exception {
        return visitor.visitVar(this, state);
    }

    public final BindCallback bindCallback() {
        return bindCallback;
    }

    /*
     * Bind VarSet to Value, or Value to Value.
     */
    @Override
    public final Value bindToValue(Value value, Set<Memo> memos) throws WaitVarException {
        if (value == null) {
            throw new NullPointerException("value");
        }
        // An empty VarSet marks this Var as containing only itself
        if (valueOrVarSet == VarSet.EMPTY_VAR_SET) {
            // Bind Value to a single Var location
            valueOrVarSet = value;
            if (bindCallback != null) {
                bindCallback.onBound(this, value);
            }
            return value;
        }
        if (valueOrVarSet instanceof VarSet thisVarSet) {
            // Bind Value to MULTIPLE Var locations (for each Var in VarSet)
            for (Var varElem : thisVarSet) {
                varElem.valueOrVarSet = value;
                if (varElem.bindCallback != null) {
                    varElem.bindCallback.onBound(this, value);
                }
            }
            return value;
        }
        // Bind Value to Value (unify a partial record or validate equality)
        valueOrVarSet = ((Value) valueOrVarSet).bindToValue(value, memos);
        return (Value) valueOrVarSet;
    }

    @Override
    public final ValueOrVar bindToValueOrVar(ValueOrVar valueOrVar, Set<Memo> memos) throws WaitVarException {
        return valueOrVar.bindToVar(this, memos);
    }

    /*
     * Bind VarSet to VarSet, or delegate to bindToValue(....) and deduplicate.
     */
    @Override
    public final ValueOrVar bindToVar(Var other, Set<Memo> memos) throws WaitVarException {
        // Bind any combination involving a right-side Value
        if (other.valueOrVarSet instanceof Value otherValue) {
            Value unifiedValue = bindToValue(otherValue, memos);
            // CRITICAL: We may have just unified two different but equivalent values. Now we must reference
            // just one value in memory.
            if (otherValue != unifiedValue) {
                other.valueOrVarSet = unifiedValue;
            }
            return unifiedValue;
        }
        // We know the right-side is not a value, bind the left-side value with a right-side Var or VarSet.
        if (valueOrVarSet instanceof Value thisValue) {
            other.bindToValue(thisValue, memos);
            return thisValue;
        }
        // We now know we must add a Var to a VarSet
        if (valueOrVarSet == VarSet.EMPTY_VAR_SET) {
            if (other.valueOrVarSet == VarSet.EMPTY_VAR_SET) {
                VarSet pair = VarSet.createPrivatelyForKlvm(new Var[]{this, other}, 2);
                this.valueOrVarSet = pair;
                other.valueOrVarSet = pair;
            } else {
                VarSet plusOne = ((VarSet) other.valueOrVarSet).add(this);
                for (Var v : plusOne) {
                    v.valueOrVarSet = plusOne;
                }
            }
        } else if (other.valueOrVarSet == VarSet.EMPTY_VAR_SET) {
            VarSet plusOne = ((VarSet) valueOrVarSet).add(other);
            for (Var v : plusOne) {
                v.valueOrVarSet = plusOne;
            }
        } else {
            VarSet union = VarSet.union((VarSet) valueOrVarSet, (VarSet) other.valueOrVarSet);
            for (Var v : union) {
                v.valueOrVarSet = union;
            }
        }
        return this;
    }

    @Override
    public final Complete checkComplete() throws WaitVarException {
        throw new WaitVarException(this);
    }

    @Override
    public final boolean entails(Value operand, Set<Memo> memos) throws WaitVarException {
        if (valueOrVarSet instanceof Value thisValue) {
            return thisValue.entails(operand, memos);
        }
        throw new WaitVarException(this);
    }

    @Override
    public final boolean entailsValueOrIdent(ValueOrIdent operand, Env env) throws WaitVarException {
        return entailsValueOrVar(operand.resolveValueOrVar(env), null);
    }

    @Override
    public final boolean entailsValueOrVar(ValueOrVar operand, Set<Memo> memos) throws WaitVarException {
        return operand.entailsVar(this, memos);
    }

    @Override
    public final boolean entailsVar(Var operand, Set<Memo> memos) throws WaitVarException {
        /*
         * If x is in ES, and y is in ES, then return TRUE
         * Else if x is in ESx, and y is in ESy, then WAIT(x)
         * Else if x is in ESx, and y is determined, then WAIT(x)
         * Else if y is in ESy, and x is determined, then WAIT(y)
         */
        if (this == operand) {
            return true;
        }
        if (valueOrVarSet == VarSet.EMPTY_VAR_SET || operand.valueOrVarSet == VarSet.EMPTY_VAR_SET) {
            throw new WaitVarException(this);
        }
        if (valueOrVarSet == operand.valueOrVarSet) {
            return true;
        }
        if (operand.valueOrVarSet instanceof Value operandValue) {
            return entails(operandValue, memos);
        }
        if (valueOrVarSet instanceof Value thisValue) {
            return operand.entails(thisValue, memos);
        }
        throw new WaitVarException(this);
    }

    public final String formatValue() {
        return "<<$var " + Integer.toHexString(System.identityHashCode(this)) + ">>";
    }

    @Override
    public final Value resolveValue() throws WaitVarException {
        if (valueOrVarSet instanceof Value thisValue) {
            return thisValue.checkNotFailedValue();
        }
        throw new WaitVarException(this);
    }

    @Override
    public final ValueOrVar resolveValueOrVar() {
        return (valueOrVarSet instanceof Value thisValue) ?
            thisValue.checkNotFailedValue() : this;
    }

    public final void setBindCallback(BindCallback bindCallback) {
        this.bindCallback = bindCallback;
    }

    @Override
    public final String toString() {
        return formatValue();
    }

    public final ValueOrVarSet valueOrVarSet() {
        return this.valueOrVarSet;
    }

}
