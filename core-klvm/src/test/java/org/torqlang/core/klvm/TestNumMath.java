/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static java.math.MathContext.DECIMAL128;
import static org.junit.jupiter.api.Assertions.*;
import static org.torqlang.core.klvm.Dec128.bigDecimal128;

/*
 * Num type hierarchy:
 *   - Num
 *     - Int64
 *       - Int32
 *         - Char
 *     - Flt64
 *       - Flt32
 *     - Dec128
 */
public class TestNumMath {

    public static final BigDecimal BIG_DECIMAL_3 = new BigDecimal(3, DECIMAL128);

    private static BigDecimal bigDecimal(double d) {
        String ds = Double.toString(d);
        return new BigDecimal(ds, DECIMAL128);
    }

    @Test
    public void testAdd() {
        Num sum;

        // ----- Char ----- //

        // Recall that operations on chars produce integers

        sum = Char.of('a').add(Char.of('b'));
        assertInstanceOf(Int32.class, sum);
        assertEquals('a' + 'b', sum.intValue());

        sum = Char.of(Character.MAX_VALUE).add(Char.of(Character.MAX_VALUE));
        assertInstanceOf(Int32.class, sum);
        assertEquals(Character.MAX_VALUE + Character.MAX_VALUE, sum.intValue());

        sum = Char.of('a').add(Int32.of(3));
        assertInstanceOf(Int32.class, sum);
        assertEquals('a' + 3, sum.intValue());

        // Int32 overflow test
        sum = Char.of('a').add(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Int32.class, sum);
        @SuppressWarnings("all")
        int intOverflow_1 = 'a' + Integer.MAX_VALUE;
        assertEquals(intOverflow_1, sum.intValue());

        sum = Char.of('a').add(Int64.of(3));
        assertInstanceOf(Int64.class, sum);
        assertEquals('a' + 3L, sum.longValue());

        // Int64 overflow test
        sum = Char.of('a').add(Int64.of(Long.MAX_VALUE));
        assertInstanceOf(Int64.class, sum);
        @SuppressWarnings("all")
        long longOverflow_1 = 'a' + Long.MAX_VALUE;
        assertEquals(longOverflow_1, sum.longValue());

        sum = Char.of('a').add(Flt32.of(3));
        assertInstanceOf(Flt32.class, sum);
        assertEquals('a' + 3f, sum.floatValue(), 0.000001);

        sum = Char.of('a').add(Flt64.of(3));
        assertInstanceOf(Flt64.class, sum);
        assertEquals('a' + 3d, sum.doubleValue(), 0.000001);

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Char.of('a').add(Dec128.of(3)));
            assertEquals(Num.NOT_AN_INT_OR_FLT, exc.getMessage());
        }

        // ----- Int32 ----- //

        sum = Int32.of(3).add(Char.of('b'));
        assertInstanceOf(Int32.class, sum);
        assertEquals(3 + 'b', sum.intValue());

        sum = Int32.of(Integer.MAX_VALUE).add(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Int32.class, sum);
        @SuppressWarnings("all")
        int intOverflow_2 = Integer.MAX_VALUE + Integer.MAX_VALUE;
        assertEquals(intOverflow_2, sum.intValue());

        sum = Int32.of(5).add(Int32.of(3));
        assertInstanceOf(Int32.class, sum);
        assertEquals(5 + 3, sum.intValue());

        // Int32 overflow test
        sum = Int32.of(5).add(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Int32.class, sum);
        @SuppressWarnings("all")
        int intOverflow_3 = 5 + Integer.MAX_VALUE;
        assertEquals(intOverflow_3, sum.intValue());

        sum = Int32.of(5).add(Int64.of(3));
        assertInstanceOf(Int64.class, sum);
        assertEquals(5 + 3L, sum.longValue());

        // Int64 overflow test
        sum = Int32.of(5).add(Int64.of(Long.MAX_VALUE));
        assertInstanceOf(Int64.class, sum);
        @SuppressWarnings("all")
        long longOverflow_2 = 5 + Long.MAX_VALUE;
        assertEquals(longOverflow_2, sum.longValue());

        sum = Int32.of(5).add(Flt32.of(3));
        assertInstanceOf(Flt32.class, sum);
        assertEquals(5 + 3f, sum.floatValue(), 0.000001);

        sum = Int32.of(5).add(Flt64.of(3));
        assertInstanceOf(Flt64.class, sum);
        assertEquals(5 + 3d, sum.doubleValue(), 0.000001);

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Int32.of(5).add(Dec128.of(3)));
            assertEquals(Num.NOT_AN_INT_OR_FLT, exc.getMessage());
        }

        // ----- Int64 ----- //

        sum = Int64.of(3).add(Char.of('b'));
        assertInstanceOf(Int64.class, sum);
        assertEquals(3L + 'b', sum.longValue());

        sum = Int64.of(Long.MAX_VALUE).add(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Int64.class, sum);
        @SuppressWarnings("all")
        long longOverflow_3 = Long.MAX_VALUE + Integer.MAX_VALUE;
        assertEquals(longOverflow_3, sum.longValue());

        sum = Int64.of(5).add(Int32.of(3));
        assertInstanceOf(Int64.class, sum);
        assertEquals(5L + 3, sum.longValue());

        // Int32 overflow test
        sum = Int64.of(5).add(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Int64.class, sum);
        @SuppressWarnings("all")
        long longOverflow_4 = 5L + Integer.MAX_VALUE;
        assertEquals(longOverflow_4, sum.longValue());

        sum = Int64.of(5).add(Int64.of(3));
        assertInstanceOf(Int64.class, sum);
        assertEquals(5L + 3L, sum.longValue());

        // Int64 overflow test
        sum = Int64.of(5).add(Int64.of(Long.MAX_VALUE));
        assertInstanceOf(Int64.class, sum);
        @SuppressWarnings("all")
        long longOverflow_5 = 5L + Long.MAX_VALUE;
        assertEquals(longOverflow_5, sum.longValue());

        sum = Int64.of(5).add(Flt32.of(3));
        assertInstanceOf(Flt32.class, sum);
        assertEquals(5L + 3f, sum.floatValue(), 0.000001);

        sum = Int64.of(5).add(Flt64.of(3));
        assertInstanceOf(Flt64.class, sum);
        assertEquals(5L + 3d, sum.doubleValue(), 0.000001);

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Int64.of(5).add(Dec128.of(3)));
            assertEquals(Num.NOT_AN_INT_OR_FLT, exc.getMessage());
        }

        // ----- Flt32 ----- //

        sum = Flt32.of(3f).add(Char.of('b'));
        assertInstanceOf(Flt32.class, sum);
        assertEquals(3f + 'b', sum.floatValue(), 0.000001);

        sum = Flt32.of(Float.MAX_VALUE).add(Flt32.of(Float.MAX_VALUE));
        assertInstanceOf(Flt32.class, sum);
        @SuppressWarnings("all")
        float floatOverflow_1 = Float.MAX_VALUE + Float.MAX_VALUE;
        assertEquals(floatOverflow_1, sum.floatValue(), 0.000001);

        sum = Flt32.of(5).add(Int32.of(3));
        assertInstanceOf(Flt32.class, sum);
        assertEquals(5f + 3, sum.floatValue(), 0.000001);

        sum = Flt32.of(5f).add(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Flt32.class, sum);
        assertEquals(5f + Integer.MAX_VALUE, sum.floatValue(), 0.000001);

        sum = Flt32.of(5f).add(Int64.of(3));
        assertInstanceOf(Flt32.class, sum);
        assertEquals(5f + 3L, sum.floatValue(), 0.000001);

        sum = Flt32.of(5f).add(Int64.of(Long.MAX_VALUE));
        assertInstanceOf(Flt32.class, sum);
        assertEquals(5f + Long.MAX_VALUE, sum.floatValue(), 0.000001);

        sum = Flt32.of(5f).add(Flt32.of(3f));
        assertInstanceOf(Flt32.class, sum);
        assertEquals(5f + 3f, sum.floatValue(), 0.000001);

        sum = Flt32.of(5f).add(Flt64.of(3d));
        assertInstanceOf(Flt64.class, sum);
        assertEquals(5f + 3d, sum.doubleValue(), 0.000001);

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Flt32.of(5f).add(Dec128.of(3)));
            assertEquals(Num.NOT_AN_INT_OR_FLT, exc.getMessage());
        }

        // ----- Flt64 ----- //

        sum = Flt64.of(3d).add(Char.of('b'));
        assertInstanceOf(Flt64.class, sum);
        assertEquals(3d + 'b', sum.doubleValue(), 0.000001);

        sum = Flt64.of(Double.MAX_VALUE).add(Flt64.of(Double.MAX_VALUE));
        assertInstanceOf(Flt64.class, sum);
        @SuppressWarnings("all")
        double doubleOverflow_1 = Double.MAX_VALUE + Double.MAX_VALUE;
        assertEquals(doubleOverflow_1, sum.doubleValue(), 0.000001);

        sum = Flt64.of(5d).add(Int32.of(3));
        assertInstanceOf(Flt64.class, sum);
        assertEquals(5d + 3, sum.doubleValue(), 0.000001);

        sum = Flt64.of(5d).add(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Flt64.class, sum);
        assertEquals(5d + Integer.MAX_VALUE, sum.doubleValue(), 0.000001);

        sum = Flt64.of(5d).add(Int64.of(3));
        assertInstanceOf(Flt64.class, sum);
        assertEquals(5d + 3L, sum.doubleValue(), 0.000001);

        sum = Flt64.of(5d).add(Int64.of(Long.MAX_VALUE));
        assertInstanceOf(Flt64.class, sum);
        assertEquals(5d + Long.MAX_VALUE, sum.doubleValue(), 0.000001);

        sum = Flt64.of(5d).add(Flt32.of(3f));
        assertInstanceOf(Flt64.class, sum);
        assertEquals(5d + 3f, sum.doubleValue(), 0.000001);

        sum = Flt64.of(5d).add(Flt64.of(3d));
        assertInstanceOf(Flt64.class, sum);
        assertEquals(5d + 3d, sum.doubleValue(), 0.000001);

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Flt64.of(5d).add(Dec128.of(3)));
            assertEquals(Num.NOT_AN_INT_OR_FLT, exc.getMessage());
        }

        // ----- Dec128 ----- //

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(3).add(Char.of('b')));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        sum = Dec128.of(Double.MAX_VALUE).add(Dec128.of(Double.MAX_VALUE));
        assertInstanceOf(Dec128.class, sum);
        assertEquals(bigDecimal(Double.MAX_VALUE).add(bigDecimal(Double.MAX_VALUE), DECIMAL128), sum.decimal128Value());

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(5).add(Int32.of(3)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(5).add(Int32.of(Integer.MAX_VALUE)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(bigDecimal128(5d)).add(Int64.of(3)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(bigDecimal128(5d)).add(Int64.of(Long.MAX_VALUE)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(bigDecimal128(5d)).add(Flt32.of(3f)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(5d).add(Flt64.of(3d)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        sum = Dec128.of(5d).add(Dec128.of(3));
        assertInstanceOf(Dec128.class, sum);
        assertEquals(bigDecimal(5d).add(BIG_DECIMAL_3, DECIMAL128), sum.decimal128Value());
    }

    @Test
    public void testDivide() {
        Num quotient;

        // ----- Char ----- //

        // Recall that operations on chars produce integers

        quotient = Char.of('a').divide(Char.of('b'));
        assertInstanceOf(Int32.class, quotient);
        assertEquals('a' / 'b', quotient.intValue());

        quotient = Char.of('b').divide(Char.of('a'));
        assertInstanceOf(Int32.class, quotient);
        assertEquals('b' / 'a', quotient.intValue());

        quotient = Char.of(Character.MAX_VALUE).divide(Char.of(Character.MAX_VALUE));
        assertInstanceOf(Int32.class, quotient);
        assertEquals(1, quotient.intValue());

        quotient = Char.of('a').divide(Int32.of(3));
        assertInstanceOf(Int32.class, quotient);
        assertEquals('a' / 3, quotient.intValue());

        quotient = Char.of('a').divide(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Int32.class, quotient);
        assertEquals('a' / Integer.MAX_VALUE, quotient.intValue());

        quotient = Char.of('a').divide(Int64.of(3));
        assertInstanceOf(Int64.class, quotient);
        assertEquals('a' / 3L, quotient.longValue());

        quotient = Char.of('a').divide(Int64.of(Long.MAX_VALUE));
        assertInstanceOf(Int64.class, quotient);
        assertEquals('a' / Long.MAX_VALUE, quotient.longValue());

        quotient = Char.of('a').divide(Flt32.of(3));
        assertInstanceOf(Flt32.class, quotient);
        assertEquals('a' / 3f, quotient.floatValue(), 0.000001);

        quotient = Char.of('a').divide(Flt64.of(3));
        assertInstanceOf(Flt64.class, quotient);
        assertEquals('a' / 3d, quotient.doubleValue(), 0.000001);

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Char.of('a').divide(Dec128.of(3)));
            assertEquals(Num.NOT_AN_INT_OR_FLT, exc.getMessage());
        }

        // ----- Int32 ----- //

        quotient = Int32.of(3).divide(Char.of('b'));
        assertInstanceOf(Int32.class, quotient);
        assertEquals(3 / 'b', quotient.intValue());

        quotient = Int32.of(Integer.MAX_VALUE).divide(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Int32.class, quotient);
        assertEquals(1, quotient.intValue());

        quotient = Int32.of(5).divide(Int32.of(3));
        assertInstanceOf(Int32.class, quotient);
        assertEquals(5 / 3, quotient.intValue());

        // Int32 overflow test
        quotient = Int32.of(5).divide(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Int32.class, quotient);
        assertEquals(5 / Integer.MAX_VALUE, quotient.intValue());

        quotient = Int32.of(5).divide(Int64.of(3));
        assertInstanceOf(Int64.class, quotient);
        assertEquals(5 / 3L, quotient.longValue());

        quotient = Int32.of(5).divide(Int64.of(Long.MAX_VALUE));
        assertInstanceOf(Int64.class, quotient);
        assertEquals(5 / Long.MAX_VALUE, quotient.longValue());

        quotient = Int32.of(5).divide(Flt32.of(3));
        assertInstanceOf(Flt32.class, quotient);
        assertEquals(5 / 3f, quotient.floatValue(), 0.000001);

        quotient = Int32.of(5).divide(Flt64.of(3));
        assertInstanceOf(Flt64.class, quotient);
        assertEquals(5 / 3d, quotient.doubleValue(), 0.000001);

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Int32.of(5).divide(Dec128.of(3)));
            assertEquals(Num.NOT_AN_INT_OR_FLT, exc.getMessage());
        }

        // ----- Int64 ----- //

        quotient = Int64.of(3).divide(Char.of('b'));
        assertInstanceOf(Int64.class, quotient);
        assertEquals(3L / 'b', quotient.longValue());

        quotient = Int64.of(Long.MAX_VALUE).divide(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Int64.class, quotient);
        assertEquals(Long.MAX_VALUE / Integer.MAX_VALUE, quotient.longValue());

        quotient = Int64.of(5).divide(Int32.of(3));
        assertInstanceOf(Int64.class, quotient);
        assertEquals(5L / 3, quotient.longValue());

        quotient = Int64.of(5).divide(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Int64.class, quotient);
        assertEquals(5L / Integer.MAX_VALUE, quotient.longValue());

        quotient = Int64.of(5).divide(Int64.of(3));
        assertInstanceOf(Int64.class, quotient);
        assertEquals(5L / 3L, quotient.longValue());

        quotient = Int64.of(5).divide(Int64.of(Long.MAX_VALUE));
        assertInstanceOf(Int64.class, quotient);
        assertEquals(5L / Long.MAX_VALUE, quotient.longValue());

        quotient = Int64.of(5).divide(Flt32.of(3));
        assertInstanceOf(Flt32.class, quotient);
        assertEquals(5L / 3f, quotient.floatValue(), 0.000001);

        quotient = Int64.of(5).divide(Flt64.of(3));
        assertInstanceOf(Flt64.class, quotient);
        assertEquals(5L / 3d, quotient.doubleValue(), 0.000001);

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Int64.of(5).divide(Dec128.of(3)));
            assertEquals(Num.NOT_AN_INT_OR_FLT, exc.getMessage());
        }

        // ----- Flt32 ----- //

        quotient = Flt32.of(3f).divide(Char.of('b'));
        assertInstanceOf(Flt32.class, quotient);
        assertEquals(3f / 'b', quotient.floatValue(), 0.000001);

        quotient = Flt32.of(Float.MAX_VALUE).divide(Flt32.of(Float.MAX_VALUE));
        assertInstanceOf(Flt32.class, quotient);
        assertEquals(1f, quotient.floatValue(), 0.000001);

        quotient = Flt32.of(5).divide(Int32.of(3));
        assertInstanceOf(Flt32.class, quotient);
        assertEquals(5f / 3, quotient.floatValue(), 0.000001);

        quotient = Flt32.of(5f).divide(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Flt32.class, quotient);
        assertEquals(5f / Integer.MAX_VALUE, quotient.floatValue(), 0.000001);

        quotient = Flt32.of(5f).divide(Int64.of(3));
        assertInstanceOf(Flt32.class, quotient);
        assertEquals(5f / 3L, quotient.floatValue(), 0.000001);

        quotient = Flt32.of(5f).divide(Int64.of(Long.MAX_VALUE));
        assertInstanceOf(Flt32.class, quotient);
        assertEquals(5f / Long.MAX_VALUE, quotient.floatValue(), 0.000001);

        quotient = Flt32.of(5f).divide(Flt32.of(3f));
        assertInstanceOf(Flt32.class, quotient);
        assertEquals(5f / 3f, quotient.floatValue(), 0.000001);

        quotient = Flt32.of(5f).divide(Flt64.of(3d));
        assertInstanceOf(Flt64.class, quotient);
        assertEquals(5f / 3d, quotient.doubleValue(), 0.000001);

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Flt32.of(5f).divide(Dec128.of(3)));
            assertEquals(Num.NOT_AN_INT_OR_FLT, exc.getMessage());
        }

        // ----- Flt64 ----- //

        quotient = Flt64.of(3d).divide(Char.of('b'));
        assertInstanceOf(Flt64.class, quotient);
        assertEquals(3d / 'b', quotient.doubleValue(), 0.000001);

        quotient = Flt64.of(Double.MAX_VALUE).divide(Flt64.of(Double.MAX_VALUE));
        assertInstanceOf(Flt64.class, quotient);
        assertEquals(1d, quotient.doubleValue(), 0.000001);

        quotient = Flt64.of(5d).divide(Int32.of(3));
        assertInstanceOf(Flt64.class, quotient);
        assertEquals(5d / 3, quotient.doubleValue(), 0.000001);

        quotient = Flt64.of(5d).divide(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Flt64.class, quotient);
        assertEquals(5d / Integer.MAX_VALUE, quotient.doubleValue(), 0.000001);

        quotient = Flt64.of(5d).divide(Int64.of(3));
        assertInstanceOf(Flt64.class, quotient);
        assertEquals(5d / 3L, quotient.doubleValue(), 0.000001);

        quotient = Flt64.of(5d).divide(Int64.of(Long.MAX_VALUE));
        assertInstanceOf(Flt64.class, quotient);
        assertEquals(5d / Long.MAX_VALUE, quotient.doubleValue(), 0.000001);

        quotient = Flt64.of(5d).divide(Flt32.of(3f));
        assertInstanceOf(Flt64.class, quotient);
        assertEquals(5d / 3f, quotient.doubleValue(), 0.000001);

        quotient = Flt64.of(5d).divide(Flt64.of(3d));
        assertInstanceOf(Flt64.class, quotient);
        assertEquals(5d / 3d, quotient.doubleValue(), 0.000001);

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Flt64.of(5d).divide(Dec128.of(3)));
            assertEquals(Num.NOT_AN_INT_OR_FLT, exc.getMessage());
        }

        // ----- Dec128 ----- //

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(3).divide(Char.of('b')));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        quotient = Dec128.of(Double.MAX_VALUE).divide(Dec128.of(Double.MAX_VALUE));
        assertInstanceOf(Dec128.class, quotient);
        assertEquals(bigDecimal(Double.MAX_VALUE).divide(bigDecimal(Double.MAX_VALUE), DECIMAL128), quotient.decimal128Value());

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(5).divide(Int32.of(3)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(5).divide(Int32.of(Integer.MAX_VALUE)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(bigDecimal128(5d)).divide(Int64.of(3)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(bigDecimal128(5d)).divide(Int64.of(Long.MAX_VALUE)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(bigDecimal128(5d)).divide(Flt32.of(3f)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(5d).divide(Flt64.of(3d)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        quotient = Dec128.of(5d).divide(Dec128.of(3));
        assertInstanceOf(Dec128.class, quotient);
        assertEquals(bigDecimal(5d).divide(BIG_DECIMAL_3, DECIMAL128), quotient.decimal128Value());
    }

    @Test
    public void testModulo() {
        Num quotient;

        // ----- Char ----- //

        // Recall that operations on chars produce integers

        quotient = Char.of('a').modulo(Char.of('b'));
        assertInstanceOf(Int32.class, quotient);
        assertEquals('a' % 'b', quotient.intValue());

        quotient = Char.of('b').modulo(Char.of('a'));
        assertInstanceOf(Int32.class, quotient);
        assertEquals('b' % 'a', quotient.intValue());

        quotient = Char.of(Character.MAX_VALUE).modulo(Char.of(Character.MAX_VALUE));
        assertInstanceOf(Int32.class, quotient);
        assertEquals(0, quotient.intValue());

        quotient = Char.of('a').modulo(Int32.of(3));
        assertInstanceOf(Int32.class, quotient);
        assertEquals('a' % 3, quotient.intValue());

        quotient = Char.of('a').modulo(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Int32.class, quotient);
        assertEquals('a' % Integer.MAX_VALUE, quotient.intValue());

        quotient = Char.of('a').modulo(Int64.of(3));
        assertInstanceOf(Int64.class, quotient);
        assertEquals('a' % 3L, quotient.longValue());

        quotient = Char.of('a').modulo(Int64.of(Long.MAX_VALUE));
        assertInstanceOf(Int64.class, quotient);
        assertEquals('a' % Long.MAX_VALUE, quotient.longValue());

        quotient = Char.of('a').modulo(Flt32.of(3));
        assertInstanceOf(Flt32.class, quotient);
        assertEquals('a' % 3f, quotient.floatValue(), 0.000001);

        quotient = Char.of('a').modulo(Flt64.of(3));
        assertInstanceOf(Flt64.class, quotient);
        assertEquals('a' % 3d, quotient.doubleValue(), 0.000001);

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Char.of('a').modulo(Dec128.of(3)));
            assertEquals(Num.NOT_AN_INT_OR_FLT, exc.getMessage());
        }

        // ----- Int32 ----- //

        quotient = Int32.of(3).modulo(Char.of('b'));
        assertInstanceOf(Int32.class, quotient);
        assertEquals(3 % 'b', quotient.intValue());

        quotient = Int32.of(Integer.MAX_VALUE).modulo(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Int32.class, quotient);
        assertEquals(0, quotient.intValue());

        quotient = Int32.of(5).modulo(Int32.of(3));
        assertInstanceOf(Int32.class, quotient);
        assertEquals(5 % 3, quotient.intValue());

        // Int32 overflow test
        quotient = Int32.of(5).modulo(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Int32.class, quotient);
        assertEquals(5 % Integer.MAX_VALUE, quotient.intValue());

        quotient = Int32.of(5).modulo(Int64.of(3));
        assertInstanceOf(Int64.class, quotient);
        assertEquals(5 % 3L, quotient.longValue());

        quotient = Int32.of(5).modulo(Int64.of(Long.MAX_VALUE));
        assertInstanceOf(Int64.class, quotient);
        assertEquals(5 % Long.MAX_VALUE, quotient.longValue());

        quotient = Int32.of(5).modulo(Flt32.of(3));
        assertInstanceOf(Flt32.class, quotient);
        assertEquals(5 % 3f, quotient.floatValue(), 0.000001);

        quotient = Int32.of(5).modulo(Flt64.of(3));
        assertInstanceOf(Flt64.class, quotient);
        assertEquals(5 % 3d, quotient.doubleValue(), 0.000001);

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Int32.of(5).modulo(Dec128.of(3)));
            assertEquals(Num.NOT_AN_INT_OR_FLT, exc.getMessage());
        }

        // ----- Int64 ----- //

        quotient = Int64.of(3).modulo(Char.of('b'));
        assertInstanceOf(Int64.class, quotient);
        assertEquals(3L % 'b', quotient.longValue());

        quotient = Int64.of(Long.MAX_VALUE).modulo(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Int64.class, quotient);
        assertEquals(Long.MAX_VALUE % Integer.MAX_VALUE, quotient.longValue());

        quotient = Int64.of(5).modulo(Int32.of(3));
        assertInstanceOf(Int64.class, quotient);
        assertEquals(5L % 3, quotient.longValue());

        quotient = Int64.of(5).modulo(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Int64.class, quotient);
        assertEquals(5L % Integer.MAX_VALUE, quotient.longValue());

        quotient = Int64.of(5).modulo(Int64.of(3));
        assertInstanceOf(Int64.class, quotient);
        assertEquals(5L % 3L, quotient.longValue());

        quotient = Int64.of(5).modulo(Int64.of(Long.MAX_VALUE));
        assertInstanceOf(Int64.class, quotient);
        assertEquals(5L % Long.MAX_VALUE, quotient.longValue());

        quotient = Int64.of(5).modulo(Flt32.of(3));
        assertInstanceOf(Flt32.class, quotient);
        assertEquals(5L % 3f, quotient.floatValue(), 0.000001);

        quotient = Int64.of(5).modulo(Flt64.of(3));
        assertInstanceOf(Flt64.class, quotient);
        assertEquals(5L % 3d, quotient.doubleValue(), 0.000001);

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Int64.of(5).modulo(Dec128.of(3)));
            assertEquals(Num.NOT_AN_INT_OR_FLT, exc.getMessage());
        }

        // ----- Flt32 ----- //

        quotient = Flt32.of(3f).modulo(Char.of('b'));
        assertInstanceOf(Flt32.class, quotient);
        assertEquals(3f % 'b', quotient.floatValue(), 0.000001);

        quotient = Flt32.of(Float.MAX_VALUE).modulo(Flt32.of(Float.MAX_VALUE));
        assertInstanceOf(Flt32.class, quotient);
        assertEquals(0f, quotient.floatValue(), 0.000001);

        quotient = Flt32.of(5).modulo(Int32.of(3));
        assertInstanceOf(Flt32.class, quotient);
        assertEquals(5f % 3, quotient.floatValue(), 0.000001);

        quotient = Flt32.of(5f).modulo(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Flt32.class, quotient);
        assertEquals(5f % Integer.MAX_VALUE, quotient.floatValue(), 0.000001);

        quotient = Flt32.of(5f).modulo(Int64.of(3));
        assertInstanceOf(Flt32.class, quotient);
        assertEquals(5f % 3L, quotient.floatValue(), 0.000001);

        quotient = Flt32.of(5f).modulo(Int64.of(Long.MAX_VALUE));
        assertInstanceOf(Flt32.class, quotient);
        assertEquals(5f % Long.MAX_VALUE, quotient.floatValue(), 0.000001);

        quotient = Flt32.of(5f).modulo(Flt32.of(3f));
        assertInstanceOf(Flt32.class, quotient);
        assertEquals(5f % 3f, quotient.floatValue(), 0.000001);

        quotient = Flt32.of(5f).modulo(Flt64.of(3d));
        assertInstanceOf(Flt64.class, quotient);
        assertEquals(5f % 3d, quotient.doubleValue(), 0.000001);

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Flt32.of(5f).modulo(Dec128.of(3)));
            assertEquals(Num.NOT_AN_INT_OR_FLT, exc.getMessage());
        }

        // ----- Flt64 ----- //

        quotient = Flt64.of(3d).modulo(Char.of('b'));
        assertInstanceOf(Flt64.class, quotient);
        assertEquals(3d % 'b', quotient.doubleValue(), 0.000001);

        quotient = Flt64.of(Double.MAX_VALUE).modulo(Flt64.of(Double.MAX_VALUE));
        assertInstanceOf(Flt64.class, quotient);
        assertEquals(0d, quotient.doubleValue(), 0.000001);

        quotient = Flt64.of(5d).modulo(Int32.of(3));
        assertInstanceOf(Flt64.class, quotient);
        assertEquals(5d % 3, quotient.doubleValue(), 0.000001);

        quotient = Flt64.of(5d).modulo(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Flt64.class, quotient);
        assertEquals(5d % Integer.MAX_VALUE, quotient.doubleValue(), 0.000001);

        quotient = Flt64.of(5d).modulo(Int64.of(3));
        assertInstanceOf(Flt64.class, quotient);
        assertEquals(5d % 3L, quotient.doubleValue(), 0.000001);

        quotient = Flt64.of(5d).modulo(Int64.of(Long.MAX_VALUE));
        assertInstanceOf(Flt64.class, quotient);
        assertEquals(5d % Long.MAX_VALUE, quotient.doubleValue(), 0.000001);

        quotient = Flt64.of(5d).modulo(Flt32.of(3f));
        assertInstanceOf(Flt64.class, quotient);
        assertEquals(5d % 3f, quotient.doubleValue(), 0.000001);

        quotient = Flt64.of(5d).modulo(Flt64.of(3d));
        assertInstanceOf(Flt64.class, quotient);
        assertEquals(5d % 3d, quotient.doubleValue(), 0.000001);

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Flt64.of(5d).modulo(Dec128.of(3)));
            assertEquals(Num.NOT_AN_INT_OR_FLT, exc.getMessage());
        }

        // ----- Dec128 ----- //

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(3).modulo(Char.of('b')));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        quotient = Dec128.of(Double.MAX_VALUE).modulo(Dec128.of(Double.MAX_VALUE));
        assertInstanceOf(Dec128.class, quotient);
        assertEquals(bigDecimal(Double.MAX_VALUE).remainder(bigDecimal(Double.MAX_VALUE), DECIMAL128), quotient.decimal128Value());

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(5).modulo(Int32.of(3)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(5).modulo(Int32.of(Integer.MAX_VALUE)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(bigDecimal128(5d)).modulo(Int64.of(3)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(bigDecimal128(5d)).modulo(Int64.of(Long.MAX_VALUE)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(bigDecimal128(5d)).modulo(Flt32.of(3f)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(5d).modulo(Flt64.of(3d)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        quotient = Dec128.of(5d).modulo(Dec128.of(3));
        assertInstanceOf(Dec128.class, quotient);
        assertEquals(bigDecimal(5d).remainder(BIG_DECIMAL_3, DECIMAL128), quotient.decimal128Value());
    }

    @Test
    public void testMultiply() {
        Num product;

        // ----- Char ----- //

        // Recall that operations on chars produce integers

        product = Char.of('a').multiply(Char.of('b'));
        assertInstanceOf(Int32.class, product);
        assertEquals('a' * 'b', product.intValue());

        product = Char.of(Character.MAX_VALUE).multiply(Char.of(Character.MAX_VALUE));
        assertInstanceOf(Int32.class, product);
        @SuppressWarnings("all")
        int intOverflow_1 = Character.MAX_VALUE * Character.MAX_VALUE;
        assertEquals(intOverflow_1, product.intValue());

        product = Char.of('a').multiply(Int32.of(3));
        assertInstanceOf(Int32.class, product);
        assertEquals('a' * 3, product.intValue());

        product = Char.of('a').multiply(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Int32.class, product);
        @SuppressWarnings("all")
        int intOverflow_2 = 'a' * Integer.MAX_VALUE;
        assertEquals(intOverflow_2, product.intValue());

        product = Char.of('a').multiply(Int64.of(3));
        assertInstanceOf(Int64.class, product);
        assertEquals('a' * 3L, product.longValue());

        product = Char.of('a').multiply(Int64.of(Long.MAX_VALUE));
        assertInstanceOf(Int64.class, product);
        @SuppressWarnings("all")
        long longOverflow_1 = 'a' * Long.MAX_VALUE;
        assertEquals(longOverflow_1, product.longValue());

        product = Char.of('a').multiply(Flt32.of(3));
        assertInstanceOf(Flt32.class, product);
        assertEquals('a' * 3f, product.floatValue(), 0.000001);

        product = Char.of('a').multiply(Flt64.of(3));
        assertInstanceOf(Flt64.class, product);
        assertEquals('a' * 3d, product.doubleValue(), 0.000001);

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Char.of('a').multiply(Dec128.of(3)));
            assertEquals(Num.NOT_AN_INT_OR_FLT, exc.getMessage());
        }

        // ----- Int32 ----- //

        product = Int32.of(3).multiply(Char.of('b'));
        assertInstanceOf(Int32.class, product);
        assertEquals(3 * 'b', product.intValue());

        product = Int32.of(Integer.MAX_VALUE).multiply(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Int32.class, product);
        @SuppressWarnings("all")
        int intOverflow_3 = Integer.MAX_VALUE * Integer.MAX_VALUE;
        assertEquals(intOverflow_3, product.intValue());

        product = Int32.of(5).multiply(Int32.of(3));
        assertInstanceOf(Int32.class, product);
        assertEquals(5 * 3, product.intValue());

        // Int32 overflow test
        product = Int32.of(5).multiply(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Int32.class, product);
        @SuppressWarnings("all")
        int intOverflow_4 = 5 * Integer.MAX_VALUE;
        assertEquals(intOverflow_4, product.intValue());

        product = Int32.of(5).multiply(Int64.of(3));
        assertInstanceOf(Int64.class, product);
        assertEquals(5 * 3L, product.longValue());

        product = Int32.of(5).multiply(Int64.of(Long.MAX_VALUE));
        assertInstanceOf(Int64.class, product);
        @SuppressWarnings("all")
        long longOverflow_2 = 5 * Long.MAX_VALUE;
        assertEquals(longOverflow_2, product.longValue());

        product = Int32.of(5).multiply(Flt32.of(3));
        assertInstanceOf(Flt32.class, product);
        assertEquals(5 * 3f, product.floatValue(), 0.000001);

        product = Int32.of(5).multiply(Flt64.of(3));
        assertInstanceOf(Flt64.class, product);
        assertEquals(5 * 3d, product.doubleValue(), 0.000001);

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Int32.of(5).multiply(Dec128.of(3)));
            assertEquals(Num.NOT_AN_INT_OR_FLT, exc.getMessage());
        }

        // ----- Int64 ----- //

        product = Int64.of(3).multiply(Char.of('b'));
        assertInstanceOf(Int64.class, product);
        assertEquals(3L * 'b', product.longValue());

        product = Int64.of(Long.MAX_VALUE).multiply(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Int64.class, product);
        @SuppressWarnings("all")
        long longOverflow_3 = Long.MAX_VALUE * Integer.MAX_VALUE;
        assertEquals(longOverflow_3, product.longValue());

        product = Int64.of(5).multiply(Int32.of(3));
        assertInstanceOf(Int64.class, product);
        assertEquals(5L * 3, product.longValue());

        product = Int64.of(5).multiply(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Int64.class, product);
        assertEquals(5L * Integer.MAX_VALUE, product.longValue());

        product = Int64.of(5).multiply(Int64.of(3));
        assertInstanceOf(Int64.class, product);
        assertEquals(5L * 3L, product.longValue());

        product = Int64.of(5).multiply(Int64.of(Long.MAX_VALUE));
        assertInstanceOf(Int64.class, product);
        @SuppressWarnings("all")
        long longOverflow_4 = 5L * Long.MAX_VALUE;
        assertEquals(longOverflow_4, product.longValue());

        product = Int64.of(5).multiply(Flt32.of(3));
        assertInstanceOf(Flt32.class, product);
        assertEquals(5L * 3f, product.floatValue(), 0.000001);

        product = Int64.of(5).multiply(Flt64.of(3));
        assertInstanceOf(Flt64.class, product);
        assertEquals(5L * 3d, product.doubleValue(), 0.000001);

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Int64.of(5).multiply(Dec128.of(3)));
            assertEquals(Num.NOT_AN_INT_OR_FLT, exc.getMessage());
        }

        // ----- Flt32 ----- //

        product = Flt32.of(3f).multiply(Char.of('b'));
        assertInstanceOf(Flt32.class, product);
        assertEquals(3f * 'b', product.floatValue(), 0.000001);

        product = Flt32.of(Float.MAX_VALUE).multiply(Flt32.of(Float.MAX_VALUE));
        assertInstanceOf(Flt32.class, product);
        @SuppressWarnings("all")
        float floatOverflow_1 = Float.MAX_VALUE * Float.MAX_VALUE;
        assertEquals(floatOverflow_1, product.floatValue(), 0.000001);

        product = Flt32.of(5).multiply(Int32.of(3));
        assertInstanceOf(Flt32.class, product);
        assertEquals(5f * 3, product.floatValue(), 0.000001);

        product = Flt32.of(5f).multiply(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Flt32.class, product);
        assertEquals(5f * Integer.MAX_VALUE, product.floatValue(), 0.000001);

        product = Flt32.of(5f).multiply(Int64.of(3));
        assertInstanceOf(Flt32.class, product);
        assertEquals(5f * 3L, product.floatValue(), 0.000001);

        product = Flt32.of(5f).multiply(Int64.of(Long.MAX_VALUE));
        assertInstanceOf(Flt32.class, product);
        assertEquals(5f * Long.MAX_VALUE, product.floatValue(), 0.000001);

        product = Flt32.of(5f).multiply(Flt32.of(3f));
        assertInstanceOf(Flt32.class, product);
        assertEquals(5f * 3f, product.floatValue(), 0.000001);

        product = Flt32.of(5f).multiply(Flt64.of(3d));
        assertInstanceOf(Flt64.class, product);
        assertEquals(5f * 3d, product.doubleValue(), 0.000001);

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Flt32.of(5f).multiply(Dec128.of(3)));
            assertEquals(Num.NOT_AN_INT_OR_FLT, exc.getMessage());
        }

        // ----- Flt64 ----- //

        product = Flt64.of(3d).multiply(Char.of('b'));
        assertInstanceOf(Flt64.class, product);
        assertEquals(3d * 'b', product.doubleValue(), 0.000001);

        product = Flt64.of(Double.MAX_VALUE).multiply(Flt64.of(Double.MAX_VALUE));
        assertInstanceOf(Flt64.class, product);
        @SuppressWarnings("all")
        double doubleOverflow_1 = Double.MAX_VALUE * Double.MAX_VALUE;
        assertEquals(doubleOverflow_1, product.doubleValue(), 0.000001);

        product = Flt64.of(5d).multiply(Int32.of(3));
        assertInstanceOf(Flt64.class, product);
        assertEquals(5d * 3, product.doubleValue(), 0.000001);

        product = Flt64.of(5d).multiply(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Flt64.class, product);
        assertEquals(5d * Integer.MAX_VALUE, product.doubleValue(), 0.000001);

        product = Flt64.of(5d).multiply(Int64.of(3));
        assertInstanceOf(Flt64.class, product);
        assertEquals(5d * 3L, product.doubleValue(), 0.000001);

        product = Flt64.of(5d).multiply(Int64.of(Long.MAX_VALUE));
        assertInstanceOf(Flt64.class, product);
        assertEquals(5d * Long.MAX_VALUE, product.doubleValue(), 0.000001);

        product = Flt64.of(5d).multiply(Flt32.of(3f));
        assertInstanceOf(Flt64.class, product);
        assertEquals(5d * 3f, product.doubleValue(), 0.000001);

        product = Flt64.of(5d).multiply(Flt64.of(3d));
        assertInstanceOf(Flt64.class, product);
        assertEquals(5d * 3d, product.doubleValue(), 0.000001);

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Flt64.of(5d).multiply(Dec128.of(3)));
            assertEquals(Num.NOT_AN_INT_OR_FLT, exc.getMessage());
        }

        // ----- Dec128 ----- //

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(3).multiply(Char.of('b')));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        product = Dec128.of(Double.MAX_VALUE).multiply(Dec128.of(Double.MAX_VALUE));
        assertInstanceOf(Dec128.class, product);
        assertEquals(bigDecimal(Double.MAX_VALUE).multiply(bigDecimal(Double.MAX_VALUE), DECIMAL128), product.decimal128Value());

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(5).multiply(Int32.of(3)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(5).multiply(Int32.of(Integer.MAX_VALUE)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(bigDecimal128(5d)).multiply(Int64.of(3)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(bigDecimal128(5d)).multiply(Int64.of(Long.MAX_VALUE)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(bigDecimal128(5d)).multiply(Flt32.of(3f)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(5d).multiply(Flt64.of(3d)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        product = Dec128.of(5d).multiply(Dec128.of(3));
        assertInstanceOf(Dec128.class, product);
        assertEquals(bigDecimal(5d).multiply(BIG_DECIMAL_3, DECIMAL128), product.decimal128Value());
    }

    @Test
    public void testSubtract() {
        Num diff;

        // ----- Char ----- //

        // Recall that operations on chars produce integers

        diff = Char.of('a').subtract(Char.of('b'));
        assertInstanceOf(Int32.class, diff);
        assertEquals('a' - 'b', diff.intValue());

        diff = Char.of(Character.MAX_VALUE).subtract(Char.of(Character.MAX_VALUE));
        assertInstanceOf(Int32.class, diff);
        assertEquals(0, diff.intValue());

        diff = Char.of('a').subtract(Int32.of(3));
        assertInstanceOf(Int32.class, diff);
        assertEquals('a' - 3, diff.intValue());

        diff = Char.of('a').subtract(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Int32.class, diff);
        assertEquals('a' - Integer.MAX_VALUE, diff.intValue());

        diff = Char.of('a').subtract(Int64.of(3));
        assertInstanceOf(Int64.class, diff);
        assertEquals('a' - 3L, diff.longValue());

        diff = Char.of('a').subtract(Int64.of(Long.MAX_VALUE));
        assertInstanceOf(Int64.class, diff);
        assertEquals('a' - Long.MAX_VALUE, diff.longValue());

        diff = Char.of('a').subtract(Flt32.of(3));
        assertInstanceOf(Flt32.class, diff);
        assertEquals('a' - 3f, diff.floatValue(), 0.000001);

        diff = Char.of('a').subtract(Flt64.of(3));
        assertInstanceOf(Flt64.class, diff);
        assertEquals('a' - 3d, diff.doubleValue(), 0.000001);

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Char.of('a').subtract(Dec128.of(3)));
            assertEquals(Num.NOT_AN_INT_OR_FLT, exc.getMessage());
        }

        // ----- Int32 ----- //

        diff = Int32.of(3).subtract(Char.of('b'));
        assertInstanceOf(Int32.class, diff);
        assertEquals(3 - 'b', diff.intValue());

        diff = Int32.of(Integer.MAX_VALUE).subtract(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Int32.class, diff);
        assertEquals(0, diff.intValue());

        diff = Int32.of(5).subtract(Int32.of(3));
        assertInstanceOf(Int32.class, diff);
        assertEquals(5 - 3, diff.intValue());

        // Int32 overflow test
        diff = Int32.of(5).subtract(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Int32.class, diff);
        assertEquals(5 - Integer.MAX_VALUE, diff.intValue());

        diff = Int32.of(5).subtract(Int64.of(3));
        assertInstanceOf(Int64.class, diff);
        assertEquals(5 - 3L, diff.longValue());

        diff = Int32.of(5).subtract(Int64.of(Long.MAX_VALUE));
        assertInstanceOf(Int64.class, diff);
        assertEquals(5 - Long.MAX_VALUE, diff.longValue());

        diff = Int32.of(5).subtract(Flt32.of(3));
        assertInstanceOf(Flt32.class, diff);
        assertEquals(5 - 3f, diff.floatValue(), 0.000001);

        diff = Int32.of(5).subtract(Flt64.of(3));
        assertInstanceOf(Flt64.class, diff);
        assertEquals(5 - 3d, diff.doubleValue(), 0.000001);

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Int32.of(5).subtract(Dec128.of(3)));
            assertEquals(Num.NOT_AN_INT_OR_FLT, exc.getMessage());
        }

        // ----- Int64 ----- //

        diff = Int64.of(3).subtract(Char.of('b'));
        assertInstanceOf(Int64.class, diff);
        assertEquals(3L - 'b', diff.longValue());

        diff = Int64.of(Long.MAX_VALUE).subtract(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Int64.class, diff);
        assertEquals(Long.MAX_VALUE - Integer.MAX_VALUE, diff.longValue());

        diff = Int64.of(5).subtract(Int32.of(3));
        assertInstanceOf(Int64.class, diff);
        assertEquals(5L - 3, diff.longValue());

        diff = Int64.of(5).subtract(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Int64.class, diff);
        assertEquals(5L - Integer.MAX_VALUE, diff.longValue());

        diff = Int64.of(5).subtract(Int64.of(3));
        assertInstanceOf(Int64.class, diff);
        assertEquals(5L - 3L, diff.longValue());

        diff = Int64.of(5).subtract(Int64.of(Long.MAX_VALUE));
        assertInstanceOf(Int64.class, diff);
        assertEquals(5L - Long.MAX_VALUE, diff.longValue());

        diff = Int64.of(5).subtract(Flt32.of(3));
        assertInstanceOf(Flt32.class, diff);
        assertEquals(5L - 3f, diff.floatValue(), 0.000001);

        diff = Int64.of(5).subtract(Flt64.of(3));
        assertInstanceOf(Flt64.class, diff);
        assertEquals(5L - 3d, diff.doubleValue(), 0.000001);

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Int64.of(5).subtract(Dec128.of(3)));
            assertEquals(Num.NOT_AN_INT_OR_FLT, exc.getMessage());
        }

        // ----- Flt32 ----- //

        diff = Flt32.of(3f).subtract(Char.of('b'));
        assertInstanceOf(Flt32.class, diff);
        assertEquals(3f - 'b', diff.floatValue(), 0.000001);

        diff = Flt32.of(Float.MAX_VALUE).subtract(Flt32.of(Float.MAX_VALUE));
        assertInstanceOf(Flt32.class, diff);
        assertEquals(0f, diff.floatValue(), 0.000001);

        diff = Flt32.of(5).subtract(Int32.of(3));
        assertInstanceOf(Flt32.class, diff);
        assertEquals(5f - 3, diff.floatValue(), 0.000001);

        diff = Flt32.of(5f).subtract(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Flt32.class, diff);
        assertEquals(5f - Integer.MAX_VALUE, diff.floatValue(), 0.000001);

        diff = Flt32.of(5f).subtract(Int64.of(3));
        assertInstanceOf(Flt32.class, diff);
        assertEquals(5f - 3L, diff.floatValue(), 0.000001);

        diff = Flt32.of(5f).subtract(Int64.of(Long.MAX_VALUE));
        assertInstanceOf(Flt32.class, diff);
        assertEquals(5f - Long.MAX_VALUE, diff.floatValue(), 0.000001);

        diff = Flt32.of(5f).subtract(Flt32.of(3f));
        assertInstanceOf(Flt32.class, diff);
        assertEquals(5f - 3f, diff.floatValue(), 0.000001);

        diff = Flt32.of(5f).subtract(Flt64.of(3d));
        assertInstanceOf(Flt64.class, diff);
        assertEquals(5f - 3d, diff.doubleValue(), 0.000001);

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Flt32.of(5f).subtract(Dec128.of(3)));
            assertEquals(Num.NOT_AN_INT_OR_FLT, exc.getMessage());
        }

        // ----- Flt64 ----- //

        diff = Flt64.of(3d).subtract(Char.of('b'));
        assertInstanceOf(Flt64.class, diff);
        assertEquals(3d - 'b', diff.doubleValue(), 0.000001);

        diff = Flt64.of(Double.MAX_VALUE).subtract(Flt64.of(Double.MAX_VALUE));
        assertInstanceOf(Flt64.class, diff);
        assertEquals(0d, diff.doubleValue(), 0.000001);

        diff = Flt64.of(5d).subtract(Int32.of(3));
        assertInstanceOf(Flt64.class, diff);
        assertEquals(5d - 3, diff.doubleValue(), 0.000001);

        diff = Flt64.of(5d).subtract(Int32.of(Integer.MAX_VALUE));
        assertInstanceOf(Flt64.class, diff);
        assertEquals(5d - Integer.MAX_VALUE, diff.doubleValue(), 0.000001);

        diff = Flt64.of(5d).subtract(Int64.of(3));
        assertInstanceOf(Flt64.class, diff);
        assertEquals(5d - 3L, diff.doubleValue(), 0.000001);

        diff = Flt64.of(5d).subtract(Int64.of(Long.MAX_VALUE));
        assertInstanceOf(Flt64.class, diff);
        assertEquals(5d - Long.MAX_VALUE, diff.doubleValue(), 0.000001);

        diff = Flt64.of(5d).subtract(Flt32.of(3f));
        assertInstanceOf(Flt64.class, diff);
        assertEquals(5d - 3f, diff.doubleValue(), 0.000001);

        diff = Flt64.of(5d).subtract(Flt64.of(3d));
        assertInstanceOf(Flt64.class, diff);
        assertEquals(5d - 3d, diff.doubleValue(), 0.000001);

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Flt64.of(5d).subtract(Dec128.of(3)));
            assertEquals(Num.NOT_AN_INT_OR_FLT, exc.getMessage());
        }

        // ----- Dec128 ----- //

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(3).subtract(Char.of('b')));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        diff = Dec128.of(Double.MAX_VALUE).subtract(Dec128.of(Double.MAX_VALUE));
        assertInstanceOf(Dec128.class, diff);
        assertEquals(bigDecimal(Double.MAX_VALUE).subtract(bigDecimal(Double.MAX_VALUE), DECIMAL128), diff.decimal128Value());

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(5).subtract(Int32.of(3)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(5).subtract(Int32.of(Integer.MAX_VALUE)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(bigDecimal128(5d)).subtract(Int64.of(3)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(bigDecimal128(5d)).subtract(Int64.of(Long.MAX_VALUE)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(bigDecimal128(5d)).subtract(Flt32.of(3f)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        {
            Exception exc = assertThrows(IllegalArgumentException.class, () -> Dec128.of(5d).subtract(Flt64.of(3d)));
            assertEquals(Num.NOT_A_DEC_128, exc.getMessage());
        }

        diff = Dec128.of(5d).subtract(Dec128.of(3));
        assertInstanceOf(Dec128.class, diff);
        assertEquals(bigDecimal(5d).subtract(BIG_DECIMAL_3, DECIMAL128), diff.decimal128Value());
    }

}
