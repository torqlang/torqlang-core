/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.util.ErrorWithSourceSpan;
import org.torqlang.core.util.SourceSpan;

import java.util.Objects;

public final class MachineHaltError extends ErrorWithSourceSpan {

    private final ComputeHalt computeHalt;

    public MachineHaltError(ComputeHalt computeHalt) {
        super(Objects.toString(computeHalt.uncaughtThrow), computeHalt.nativeCause);
        this.computeHalt = computeHalt;
    }

    public final ComputeHalt computeHalt() {
        return computeHalt;
    }

    public final Stack current() {
        return computeHalt.current;
    }

    public final Throwable nativeCause() {
        return computeHalt.nativeCause;
    }

    @Override
    public final SourceSpan sourceSpan() {
        return current().stmt;
    }

    public final FailedValue touchedFailedValue() {
        return computeHalt.touchedFailedValue;
    }

}
