/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

public final class Bool implements Literal {

    public static final Bool FALSE = new Bool(false);
    public static final Bool TRUE = new Bool(true);

    public final boolean value;

    private Bool(boolean value) {
        this.value = value;
    }

    public static Bool of(boolean value) {
        return value ? Bool.TRUE : Bool.FALSE;
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state) throws Exception {
        return visitor.visitBool(this, state);
    }

    @Override
    public final String appendToString(String string) {
        return string + value;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public final boolean equals(Object other) {
        return value && other == Bool.TRUE || !value && other == Bool.FALSE;
    }

    @Override
    public final String formatValue() {
        return value ? "true" : "false";
    }

    @Override
    public final Bool greaterThan(Value right) {
        if (!(right instanceof Bool b)) {
            throw new IllegalArgumentException(KlvmMessageText.ARGUMENT_MUST_BE_A_BOOL);
        }
        return Bool.of(Boolean.compare(value, b.value) > 0);
    }

    @Override
    public final Bool greaterThanOrEqualTo(Value right) {
        if (!(right instanceof Bool b)) {
            throw new IllegalArgumentException(KlvmMessageText.ARGUMENT_MUST_BE_A_BOOL);
        }
        return Bool.of(Boolean.compare(value, b.value) >= 0);
    }

    @Override
    public final int hashCode() {
        return Boolean.hashCode(value);
    }

    @Override
    public final boolean isValidKey() {
        return true;
    }

    @Override
    public final Bool lessThan(Value right) {
        if (!(right instanceof Bool b)) {
            throw new IllegalArgumentException(KlvmMessageText.ARGUMENT_MUST_BE_A_BOOL);
        }
        return Bool.of(Boolean.compare(value, b.value) < 0);
    }

    @Override
    public final Bool lessThanOrEqualTo(Value right) {
        if (!(right instanceof Bool b)) {
            throw new IllegalArgumentException(KlvmMessageText.ARGUMENT_MUST_BE_A_BOOL);
        }
        return Bool.of(Boolean.compare(value, b.value) <= 0);
    }

    @Override
    public final Bool not() {
        return value ? Bool.FALSE : Bool.TRUE;
    }

    @Override
    public final Boolean toNativeValue() {
        return value;
    }

    @Override
    public final String toString() {
        return formatValue();
    }

}
