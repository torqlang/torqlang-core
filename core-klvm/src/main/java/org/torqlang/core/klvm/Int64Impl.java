/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Set;

final class Int64Impl implements Int64 {

    private static final int I64_CACHE_SIZE = 10;
    private static final Int64Impl[] I64_CACHE;

    static {
        I64_CACHE = new Int64Impl[I64_CACHE_SIZE];
        for (int i = 0; i < I64_CACHE_SIZE; i++) {
            I64_CACHE[i] = new Int64Impl(i);
        }
    }

    public final long value;

    private Int64Impl(long value) {
        this.value = value;
    }

    static Int64Impl of(long num) {
        if (num > -1 && num < I64_CACHE_SIZE) {
            return I64_CACHE[(int) num];
        }
        return new Int64Impl(num);
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitInt64(this, state);
    }

    @Override
    public final Num add(Value addend) {
        return Num.assertNum(addend).addFrom(this);
    }

    @Override
    public final Dec128 addFrom(Dec128 augend) {
        throw new IllegalArgumentException(NOT_A_DEC_128);
    }

    @Override
    public final Int64 addFrom(Char augend) {
        return Int64Impl.of(augend.longValue() + value);
    }

    @Override
    public final Int64 addFrom(Int32 augend) {
        return Int64Impl.of(augend.longValue() + value);
    }

    @Override
    public final Int64 addFrom(Int64 augend) {
        return Int64Impl.of(augend.longValue() + value);
    }

    @Override
    public final Flt32 addFrom(Flt32 augend) {
        return Flt32Impl.of(augend.floatValue() + value);
    }

    @Override
    public final Flt64 addFrom(Flt64 augend) {
        return Flt64Impl.of(augend.doubleValue() + value);
    }

    @Override
    public final String appendToString(String string) {
        return string + value;
    }

    @Override
    public final int compareValueFrom(Dec128 left) {
        throw new IllegalArgumentException(NOT_A_DEC_128);
    }

    @Override
    public final int compareValueFrom(Char left) {
        return Long.compare(left.longValue(), value);
    }

    @Override
    public final int compareValueFrom(Int32 left) {
        return Long.compare(left.longValue(), value);
    }

    @Override
    public final int compareValueFrom(Int64 left) {
        return Long.compare(left.longValue(), value);
    }

    @Override
    public final int compareValueFrom(Flt32 left) {
        return Float.compare(left.floatValue(), value);
    }

    @Override
    public final int compareValueFrom(Flt64 left) {
        return Double.compare(left.doubleValue(), value);
    }

    @Override
    public final int compareValueTo(Value right) {
        return Num.assertNum(right).compareValueFrom(this);
    }

    @Override
    public final BigDecimal decimal128Value() {
        return new BigDecimal(value, MathContext.DECIMAL128);
    }

    @Override
    public final Num divide(Value divisor) {
        return Num.assertNum(divisor).divideFrom(this);
    }

    @Override
    public final Dec128 divideFrom(Dec128 dividend) {
        throw new IllegalArgumentException(NOT_A_DEC_128);
    }

    @Override
    public final Int64 divideFrom(Char dividend) {
        return Int64Impl.of(dividend.intValue() / value);
    }

    @Override
    public final Int64 divideFrom(Int32 dividend) {
        return Int64Impl.of(dividend.intValue() / value);
    }

    @Override
    public final Int64 divideFrom(Int64 dividend) {
        return Int64Impl.of(dividend.longValue() / value);
    }

    @Override
    public final Flt32 divideFrom(Flt32 dividend) {
        return Flt32Impl.of(dividend.floatValue() / value);
    }

    @Override
    public final Flt64 divideFrom(Flt64 dividend) {
        return Flt64Impl.of(dividend.doubleValue() / value);
    }

    @Override
    public final double doubleValue() {
        return value;
    }

    @Override
    public final boolean entails(Value operand, Set<Memo> memos) {
        return this.equals(operand);
    }

    @Override
    public final boolean equals(Object right) {
        if (!(right instanceof Int64Impl i)) {
            return false;
        }
        return value == i.value;
    }

    @Override
    public final float floatValue() {
        return (float) value;
    }

    public final String formatValue() {
        return Long.toString(value);
    }

    @Override
    public final int hashCode() {
        return Long.hashCode(value);
    }

    /*
     * Convert the long value to an integer value.
     */
    public final int intValue() {
        return (int) this.value;
    }

    @Override
    public final boolean isValidKey() {
        return true;
    }

    @Override
    public final long longValue() {
        return this.value;
    }

    @Override
    public final Num modulo(Value divisor) {
        return Num.assertNum(divisor).moduloFrom(this);
    }

    @Override
    public final Dec128 moduloFrom(Dec128 dividend) {
        throw new IllegalArgumentException(NOT_A_DEC_128);
    }

    @Override
    public final Int64 moduloFrom(Char dividend) {
        return Int64Impl.of(dividend.intValue() % value);
    }

    @Override
    public final Int64 moduloFrom(Int32 dividend) {
        return Int64Impl.of(dividend.intValue() % value);
    }

    @Override
    public final Int64 moduloFrom(Int64 dividend) {
        return Int64Impl.of(dividend.longValue() % value);
    }

    @Override
    public final Flt32 moduloFrom(Flt32 dividend) {
        return Flt32Impl.of(dividend.floatValue() % value);
    }

    @Override
    public final Flt64 moduloFrom(Flt64 dividend) {
        return Flt64Impl.of(dividend.doubleValue() % value);
    }

    @Override
    public final Num multiply(Value multiplicand) {
        return Num.assertNum(multiplicand).multiplyFrom(this);
    }

    @Override
    public final Dec128 multiplyFrom(Dec128 multiplicand) {
        throw new IllegalArgumentException(NOT_A_DEC_128);
    }

    @Override
    public final Int64 multiplyFrom(Char multiplicand) {
        return Int64Impl.of(multiplicand.longValue() * value);
    }

    @Override
    public final Int64 multiplyFrom(Int32 multiplicand) {
        return Int64Impl.of(multiplicand.longValue() * value);
    }

    @Override
    public final Int64 multiplyFrom(Int64 multiplicand) {
        return Int64Impl.of(multiplicand.longValue() * value);
    }

    @Override
    public final Flt32 multiplyFrom(Flt32 multiplicand) {
        return Flt32Impl.of(multiplicand.floatValue() * value);
    }

    @Override
    public final Flt64 multiplyFrom(Flt64 multiplicand) {
        return Flt64Impl.of(multiplicand.doubleValue() * value);
    }

    @Override
    public final Int64 negate() {
        return Int64Impl.of(-1 * value);
    }

    @Override
    public final Num subtract(Value subtrahend) {
        return Num.assertNum(subtrahend).subtractFrom(this);
    }

    @Override
    public final Dec128 subtractFrom(Dec128 minuend) {
        throw new IllegalArgumentException(NOT_A_DEC_128);
    }

    @Override
    public final Int64 subtractFrom(Char minuend) {
        return Int64Impl.of(minuend.longValue() - value);
    }

    @Override
    public final Int64 subtractFrom(Int32 minuend) {
        return Int64Impl.of(minuend.longValue() - value);
    }

    @Override
    public final Int64 subtractFrom(Int64 minuend) {
        return Int64Impl.of(minuend.longValue() - value);
    }

    @Override
    public final Flt32 subtractFrom(Flt32 minuend) {
        return Flt32Impl.of(minuend.floatValue() - value);
    }

    @Override
    public final Flt64 subtractFrom(Flt64 minuend) {
        return Flt64Impl.of(minuend.doubleValue() - value);
    }

    @Override
    public final Long toNativeValue() {
        return value;
    }

    @Override
    public final String toString() {
        return formatValue();
    }

}
