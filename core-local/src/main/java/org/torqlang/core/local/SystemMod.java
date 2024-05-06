/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.klvm.*;

final class SystemMod {
    static final CompleteRec moduleRec = createModuleRec();

    private static CompleteRec createModuleRec() {
        // system.FeatureIter
        // system.Math
        // system.StringBuilder
        return Rec.completeRecBuilder()
            .addField(Str.of("ArrayList"), ArrayListPack.ARRAY_LIST_CLS)
            .addField(Str.of("Cell"), CellPack.CELL_CLS)
            .addField(Str.of("HashMap"), HashMapPack.HASH_MAP_CLS)
            .addField(Str.of("FieldIter"), FieldIterPack.FIELD_ITER_CLS)
            .addField(Str.of("LocalDate"), LocalDatePack.LOCAL_DATE_CLS)
            .addField(Str.of("RangeIter"), RangeIterPack.RANGE_ITER_CLS)
            .addField(Str.of("Rec"), RecPack.REC_CLS)
            .addField(Str.of("Stream"), LocalActor.StreamCls.SINGLETON)
            .addField(Str.of("Timer"), TimerPack.TIMER_ACTOR)
            .addField(Str.of("Token"), TokenPack.TOKEN_CLS)
            .addField(Str.of("ValueIter"), ValueIterPack.VALUE_ITER_CLS)
            .build();
    }
}
