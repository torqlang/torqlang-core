/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

public final class ComputePreempt implements ComputeAdvice {

    public static final ComputePreempt SINGLETON = new ComputePreempt();

    private ComputePreempt() {
    }

    @Override
    public final boolean isPreempt() {
        return true;
    }

}
