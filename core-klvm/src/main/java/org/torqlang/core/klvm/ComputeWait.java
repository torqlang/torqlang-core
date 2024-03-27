/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

public final class ComputeWait implements ComputeAdvice {

    public final Object barrier;

    public ComputeWait(Object barrier) {
        this.barrier = barrier;
    }

    @Override
    public final boolean isWait() {
        return true;
    }

}
