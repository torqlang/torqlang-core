/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

public final class WaitVarException extends WaitException {

    private final Var var;

    public WaitVarException(Var var) {
        this.var = var;
    }

    @Override
    public final Var barrier() {
        return var;
    }

    @Override
    public final String toString() {
        return "Wait exception at barrier: " + var;
    }

}
