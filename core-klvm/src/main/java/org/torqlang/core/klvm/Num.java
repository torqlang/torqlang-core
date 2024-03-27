/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.math.BigDecimal;
import java.util.Set;

/*
 * The 19 specific conversions on Java primitive types are called the widening primitive conversions:
 *     -- byte to short, int, long, float, or double
 *     -- short to int, long, float, or double
 *     -- char to int, long, float, or double
 *     -- int to long, float, or double
 *     -- long to float or double
 *     -- float to double
 *
 * Torqlang performs the same Java widening conversions.
 *
 * Operand naming:
 *     dividend / divisor = quotient
 *     augend + addend = sum
 *     minuend - subtrahend - difference
 *     multiplicand * multiplier = product
 */
public interface Num extends Complete {

    String NOT_A_DEC_128 = "Not a Dec128";
    String NOT_A_NUM = "Not a Num";
    String NOT_AN_INT_OR_FLT = "Not an Int or Flt";

    static Num assertNum(Value value) {
        if (value instanceof Num num) {
            return num;
        }
        throw new IllegalArgumentException(NOT_A_NUM);
    }

    // augend + addend = sum
    Num add(Value addend);

    Num addFrom(Dec128 augend);

    Num addFrom(Char augend);

    Num addFrom(Int32 augend);

    Num addFrom(Int64 augend);

    Num addFrom(Flt32 augend);

    Num addFrom(Flt64 augend);

    @Override
    default Num bindToValue(Value value, Set<Memo> memos) {
        if (!this.equals(value)) {
            throw new UnificationError(this, value);
        }
        return this;
    }

    int compareValueFrom(Dec128 left);

    int compareValueFrom(Char left);

    int compareValueFrom(Int32 left);

    int compareValueFrom(Int64 left);

    int compareValueFrom(Flt32 left);

    int compareValueFrom(Flt64 left);

    BigDecimal decimal128Value();

    // dividend / divisor = quotient
    Num divide(Value divisor);

    Num divideFrom(Dec128 dividend);

    Num divideFrom(Char dividend);

    Num divideFrom(Int32 dividend);

    Num divideFrom(Int64 dividend);

    Num divideFrom(Flt32 dividend);

    Num divideFrom(Flt64 dividend);

    double doubleValue();

    @Override
    default boolean entailsRec(Rec operand, Set<Memo> memos) {
        return false;
    }

    float floatValue();

    String formatValue();

    @Override
    default Bool greaterThan(Value operand) {
        return Bool.of(compareValueTo(Num.assertNum(operand)) > 0);
    }

    @Override
    default Bool greaterThanOrEqualTo(Value operand) {
        return Bool.of(compareValueTo(Num.assertNum(operand)) >= 0);
    }

    int intValue();

    @Override
    boolean isValidKey();

    @Override
    default Bool lessThan(Value operand) {
        return Bool.of(compareValueTo(Num.assertNum(operand)) < 0);
    }

    @Override
    default Bool lessThanOrEqualTo(Value operand) {
        return Bool.of(compareValueTo(Num.assertNum(operand)) <= 0);
    }

    long longValue();

    // dividend / divisor = quotient
    Num modulo(Value divisor);

    Num moduloFrom(Dec128 dividend);

    Num moduloFrom(Char dividend);

    Num moduloFrom(Int32 dividend);

    Num moduloFrom(Int64 dividend);

    Num moduloFrom(Flt32 dividend);

    Num moduloFrom(Flt64 dividend);

    // multiplicand * multiplier = product
    Num multiply(Value multiplier);

    Num multiplyFrom(Dec128 multiplicand);

    Num multiplyFrom(Char multiplicand);

    Num multiplyFrom(Int32 multiplicand);

    Num multiplyFrom(Int64 multiplicand);

    Num multiplyFrom(Flt32 multiplicand);

    Num multiplyFrom(Flt64 multiplicand);

    @Override
    Num negate();

    // minuend - subtrahend - difference
    Num subtract(Value subtrahend);

    Num subtractFrom(Dec128 minuend);

    Num subtractFrom(Char minuend);

    Num subtractFrom(Int32 minuend);

    Num subtractFrom(Int64 minuend);

    Num subtractFrom(Flt32 minuend);

    Num subtractFrom(Flt64 minuend);

}
