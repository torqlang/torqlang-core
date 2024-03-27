/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

public interface Int32 extends Int64 {

    Int32 I32_0 = Int32Impl.of(0);
    Int32 I32_1 = Int32Impl.of(1);
    Int32 I32_2 = Int32Impl.of(2);
    Int32 I32_3 = Int32Impl.of(3);
    Int32 I32_4 = Int32Impl.of(4);
    Int32 I32_5 = Int32Impl.of(5);
    Int32 I32_6 = Int32Impl.of(6);
    Int32 I32_7 = Int32Impl.of(7);
    Int32 I32_8 = Int32Impl.of(8);
    Int32 I32_9 = Int32Impl.of(9);

    Int32 I32_MAX = Int32Impl.of(Integer.MAX_VALUE);
    Int32 I32_MIN = Int32Impl.of(Integer.MIN_VALUE);

    static Int32 decode(String num) {
        return Int32Impl.of(Integer.decode(num));
    }

    static Int32 of(int num) {
        return Int32Impl.of(num);
    }

    float floatValue();

    int intValue();

    @Override
    Int32 negate();

}
