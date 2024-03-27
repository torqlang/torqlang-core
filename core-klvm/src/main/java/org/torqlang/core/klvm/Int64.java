/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

public interface Int64 extends Num, Feature {

    String MAX_INT64_STR = "" + Long.MAX_VALUE;
    String MIN_INT64_STR = "" + Long.MIN_VALUE;

    Int64 I64_0 = Int64Impl.of(0);
    Int64 I64_1 = Int64Impl.of(1);
    Int64 I64_2 = Int64Impl.of(2);
    Int64 I64_3 = Int64Impl.of(3);
    Int64 I64_4 = Int64Impl.of(4);
    Int64 I64_5 = Int64Impl.of(5);
    Int64 I64_6 = Int64Impl.of(6);
    Int64 I64_7 = Int64Impl.of(7);
    Int64 I64_8 = Int64Impl.of(8);
    Int64 I64_9 = Int64Impl.of(9);

    Int64 I64_MAX = Int64Impl.of(Long.MAX_VALUE);
    Int64 I64_MIN = Int64Impl.of(Long.MIN_VALUE);

    static Int64 decode(String num) {
        return Int64Impl.of(Long.decode(num));
    }

    static Int64 of(long num) {
        return Int64Impl.of(num);
    }

    @Override
    Int64 negate();

}
