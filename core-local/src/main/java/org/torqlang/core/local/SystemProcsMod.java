/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.klvm.*;

final class SystemProcsMod {
    static final CompleteRec moduleRec = createModuleRec();

    private static CompleteRec createModuleRec() {
        return Rec.completeRecBuilder()
            .addField(Str.of("assert_bound"), KernelProcs.ASSERT_BOUND_PROC)
            .addField(Str.of("is_bound"), KernelProcs.IS_BOUND_PROC)
            .addField(Str.of("is_det"), KernelProcs.IS_DET_PROC)
            .addField(Str.of("respond"), (CompleteProc) LocalActor::onCallbackToRespondFromProc)
            .build();
    }
}
