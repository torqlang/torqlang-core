/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.klvm.*;

final class BuiltInsMod {
    static final CompleteRec moduleRec = createModuleRec();

    private static CompleteRec createModuleRec() {
        // system.FeatureIter
        // system.FieldIter
        // system.Math
        // system.StringBuilder
        return Rec.completeRecBuilder()
            .addField(Str.of("ArrayList"), ArrayListMod.ARRAY_LIST_CLS)
            .addField(Str.of("assert_bound"), KernelProcs.ASSERT_BOUND_PROC)
            .addField(Str.of("Cell"), CellMod.CELL_CLS)
            .addField(Str.of("HashMap"), HashMapMod.HASH_MAP_CLS)
            .addField(Str.of("is_bound"), KernelProcs.IS_BOUND_PROC)
            .addField(Str.of("is_det"), KernelProcs.IS_DET_PROC)
            .addField(Str.of("Iter"), IterMod.ITER_CLS)
            .addField(Str.of("LocalDate"), LocalDateMod.LOCAL_DATE_CLS)
            .addField(Str.of("RangeIter"), RangeIterMod.RANGE_ITER_CLS)
            .addField(Str.of("Rec"), RecMod.REC_CLS)
            .addField(Str.of("Timer"), TimerMod.TIMER_ACTOR)
            .addField(Str.of("Token"), TokenMod.TOKEN_CLS)
            .addField(Str.of("ValueIter"), ValueIterMod.VALUE_ITER_CLS)
            .build();
    }
}
