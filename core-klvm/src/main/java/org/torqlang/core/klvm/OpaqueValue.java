/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.Set;

public abstract class OpaqueValue implements Complete {

    public OpaqueValue() {
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitOpaqueValue(this, state);
    }

    @Override
    public final Value bindToValue(Value value, Set<Memo> memos) {
        if (this != value) {
            throw new UnificationError(this, value);
        }
        return value;
    }

    @Override
    public final boolean entails(Value operand, Set<Memo> memos) {
        return this.equals(operand);
    }

    @Override
    public final boolean entailsRec(Rec operand, Set<Memo> memos) {
        return false;
    }

    @Override
    public final boolean equals(Object other) {
        return this == other;
    }

    @Override
    public final int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public final boolean isValidKey() {
        return true;
    }

    @Override
    public String toString() {
        return toKernelString();
    }

}
