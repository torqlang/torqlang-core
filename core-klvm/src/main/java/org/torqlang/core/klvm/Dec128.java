/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Set;

public final class Dec128 implements Num {

    public static final Dec128 D128_0 = Dec128.of(0);
    public static final Dec128 D128_1 = Dec128.of(1);
    public static final Dec128 D128_10 = Dec128.of(10);

    private final BigDecimal value;

    private Dec128(BigDecimal value) {
        this.value = value;
    }

    public static BigDecimal bigDecimal128(double value) {
        return new BigDecimal(Double.toString(value), MathContext.DECIMAL128);
    }

    public static Dec128 decode(String text) {
        if (text.endsWith("M") || text.endsWith("m")) {
            text = text.substring(0, text.length() - 1);
        }
        BigDecimal bigDecimal;
        if (text.startsWith("0x")) {
            text = text.substring(2);
            bigDecimal = new BigDecimal(new BigInteger(text, 16), MathContext.DECIMAL128);
        } else {
            bigDecimal = new BigDecimal(text, MathContext.DECIMAL128);
        }
        return new Dec128(bigDecimal);
    }

    public static Dec128 of(String value) {
        return new Dec128(new BigDecimal(value, MathContext.DECIMAL128));
    }

    public static Dec128 of(double value) {
        return new Dec128(bigDecimal128(value));
    }

    public static Dec128 of(long value) {
        return new Dec128(new BigDecimal(value, MathContext.DECIMAL128));
    }

    public static Dec128 of(BigDecimal value) {
        return new Dec128(new BigDecimal(value.unscaledValue(), value.scale(), MathContext.DECIMAL128));
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitDec128(this, state);
    }

    @Override
    public final Num add(Value addend) {
        return Num.assertNum(addend).addFrom(this);
    }

    @Override
    public final Dec128 addFrom(Dec128 augend) {
        return new Dec128(augend.value.add(this.value, MathContext.DECIMAL128));
    }

    @Override
    public final Dec128 addFrom(Char augend) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final Dec128 addFrom(Int32 augend) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final Dec128 addFrom(Int64 augend) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final Dec128 addFrom(Flt32 augend) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final Dec128 addFrom(Flt64 augend) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final String appendToString(String string) {
        return string + value;
    }

    @Override
    public final int compareValueFrom(Dec128 left) {
        return left.value.compareTo(value);
    }

    @Override
    public final int compareValueFrom(Char left) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final int compareValueFrom(Int32 left) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final int compareValueFrom(Int64 left) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final int compareValueFrom(Flt32 left) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final int compareValueFrom(Flt64 left) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final int compareValueTo(Value right) {
        return Num.assertNum(right).compareValueFrom(this);
    }

    @Override
    public final BigDecimal decimal128Value() {
        return value;
    }

    @Override
    public final Num divide(Value divisor) {
        return Num.assertNum(divisor).divideFrom(this);
    }

    @Override
    public final Dec128 divideFrom(Dec128 dividend) {
        return Dec128.of(dividend.value.divide(value, MathContext.DECIMAL128));
    }

    @Override
    public final Dec128 divideFrom(Char dividend) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final Dec128 divideFrom(Int32 dividend) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final Dec128 divideFrom(Int64 dividend) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final Dec128 divideFrom(Flt32 dividend) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final Dec128 divideFrom(Flt64 dividend) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final double doubleValue() {
        return value.doubleValue();
    }

    @Override
    public final boolean entails(Value operand, Set<Memo> memos) {
        return this.equals(operand);
    }

    @Override
    public final boolean equals(Object right) {
        if (!(right instanceof Dec128 d)) {
            return false;
        }
        return value.compareTo(d.value) == 0;
    }

    @Override
    public final float floatValue() {
        return value.floatValue();
    }

    public final String formatValue() {
        return value().toString();
    }

    @Override
    public final int hashCode() {
        return value.hashCode();
    }

    @Override
    public final int intValue() {
        return value.intValue();
    }

    /*
     * Dec128 can be used as a key because Dec128 uses compareTo() to implement the equals() method. From the
     * JavaDoc for BigDecimal#compareTo: "Two BigDecimal objects that are equal in value but have a different
     * scale (like 2.0 and 2.00) are considered equal by this method."
     */
    @Override
    public final boolean isValidKey() {
        return true;
    }

    @Override
    public final long longValue() {
        return value.longValue();
    }

    @Override
    public final Num modulo(Value divisor) {
        return Num.assertNum(divisor).moduloFrom(this);
    }

    @Override
    public final Dec128 moduloFrom(Dec128 dividend) {
        return Dec128.of(dividend.value.remainder(value, MathContext.DECIMAL128));
    }

    @Override
    public final Dec128 moduloFrom(Char dividend) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final Dec128 moduloFrom(Int32 dividend) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final Dec128 moduloFrom(Int64 dividend) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final Dec128 moduloFrom(Flt32 dividend) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final Dec128 moduloFrom(Flt64 dividend) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final Num multiply(Value multiplicand) {
        return Num.assertNum(multiplicand).multiplyFrom(this);
    }

    @Override
    public final Dec128 multiplyFrom(Dec128 multiplicand) {
        return Dec128.of(multiplicand.value.multiply(value, MathContext.DECIMAL128));
    }

    @Override
    public final Dec128 multiplyFrom(Char multiplicand) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final Dec128 multiplyFrom(Int32 multiplicand) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final Dec128 multiplyFrom(Int64 multiplicand) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final Dec128 multiplyFrom(Flt32 multiplicand) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final Dec128 multiplyFrom(Flt64 multiplicand) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final Dec128 negate() {
        return Dec128.of(value.negate(MathContext.DECIMAL128));
    }

    @Override
    public final Num subtract(Value subtrahend) {
        return Num.assertNum(subtrahend).subtractFrom(this);
    }

    @Override
    public final Dec128 subtractFrom(Dec128 minuend) {
        return new Dec128(minuend.value.subtract(this.value, MathContext.DECIMAL128));
    }

    @Override
    public final Dec128 subtractFrom(Char minuend) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final Dec128 subtractFrom(Int32 minuend) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final Dec128 subtractFrom(Int64 minuend) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final Dec128 subtractFrom(Flt32 minuend) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final Dec128 subtractFrom(Flt64 minuend) {
        throw new IllegalArgumentException(NOT_AN_INT_OR_FLT);
    }

    @Override
    public final BigDecimal toNativeValue() {
        return value;
    }

    @Override
    public final String toString() {
        return formatValue();
    }

    public final BigDecimal value() {
        return value;
    }

}
