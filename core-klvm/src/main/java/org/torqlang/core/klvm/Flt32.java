/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

public interface Flt32 extends Flt64 {

    Flt32 F32_MAX = Flt32Impl.of(Float.MAX_VALUE);
    Flt32 F32_MIN = Flt32Impl.of(Float.MIN_VALUE);

    static Flt32 of(float num) {
        return Flt32Impl.of(num);
    }

    static Flt32 of(String num) {
        return Flt32Impl.of(Float.parseFloat(num));
    }

    @Override
    Flt32 negate();

}
