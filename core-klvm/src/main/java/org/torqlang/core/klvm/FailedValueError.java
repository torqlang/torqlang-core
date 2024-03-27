/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

public final class FailedValueError extends MachineError {

    public final FailedValue touchedFailedValue;

    public FailedValueError(FailedValue touchedFailedValue) {
        super("Failed value error");
        this.touchedFailedValue = touchedFailedValue;
    }

    @Override
    final ComputeHalt asComputeHalt(Stack current) {
        return new ComputeHalt(touchedFailedValue, current);
    }

}
