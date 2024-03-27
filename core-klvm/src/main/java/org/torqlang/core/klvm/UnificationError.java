/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

public final class UnificationError extends MachineError {
    public final Value a;
    public final Value b;

    public UnificationError(Value a, Value b) {
        super("Unification error");
        this.a = a;
        this.b = b;
    }
}
