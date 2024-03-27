/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

public final class NotBoundError extends MachineError {
    public final Var arg;
    public final Stmt stmt;

    public NotBoundError(Var arg, Stmt stmt) {
        super("Not bound error");
        this.arg = arg;
        this.stmt = stmt;
    }
}
