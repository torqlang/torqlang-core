/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

public interface Flt64 extends Num {

    Flt64 F64_MAX = Flt64Impl.of(Double.MAX_VALUE);
    Flt64 F64_MIN = Flt64Impl.of(Double.MIN_VALUE);

    /*
     * ATTRIBUTION: This technique was derived from Guava DoubleMath fuzzyCompare()
     *
     * See: https://stackoverflow.com/questions/47018744/effects-of-violating-compareto-transitivity-contract-due-to-numerical-precision
     */
    static int fuzzyCompare(double a, double b, double tolerance) {
        if (fuzzyEquals(a, b, tolerance)) {
            return 0;
        } else if (a < b) {
            return -1;
        } else if (a > b) {
            return 1;
        } else {
            return Boolean.compare(Double.isNaN(a), Double.isNaN(b));
        }
    }

    /*
     * ATTRIBUTION: This technique was derived from Guava DoubleMath fuzzyEquals().
     *
     * See: https://stackoverflow.com/questions/47018744/effects-of-violating-compareto-transitivity-contract-due-to-numerical-precision
     */
    static boolean fuzzyEquals(double a, double b, double tolerance) {
        if (!(tolerance >= 0.0)) { // not x < 0, to work with NaN
            throw new IllegalArgumentException("Tolerance must be greater than or equal to zero");
        }
        return
            Math.copySign(a - b, 1.0) <= tolerance
                // copySign(x, 1.0) is a branch-free version of abs(x), but with different NaN semantics
                | (a == b) // needed to ensure that infinities equal themselves
                || (Double.isNaN(a) && Double.isNaN(b));
    }

    static Flt64 of(double num) {
        return Flt64Impl.of(num);
    }

    static Flt64 of(String num) {
        return Flt64Impl.of(Double.parseDouble(num));
    }

    /*
     * ATTRIBUTION: This technique was copied from Guava#fuzzyEqual.
     *
     * Return true if the two arguments are effectively equal within a tolerance.
     */
    default boolean fuzzyEquals(Flt64 other, Flt64 tolerance) {
        return fuzzyEquals(doubleValue(), other.doubleValue(), tolerance.doubleValue());
    }

    @Override
    Flt64 negate();

}
