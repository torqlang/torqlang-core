/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

public final class ComputeHalt implements ComputeAdvice {

    public final FailedValue touchedFailedValue;
    public final Complete uncaughtThrow;
    public final Throwable nativeCause;
    public final Stack current;

    public ComputeHalt(Complete uncaughtThrow, Stack current, Throwable nativeCause) {
        this.uncaughtThrow = uncaughtThrow;
        this.nativeCause = nativeCause;
        this.touchedFailedValue = null;
        this.current = current;
    }

    public ComputeHalt(FailedValue touchedFailedValue, Stack current) {
        this.uncaughtThrow = null;
        this.nativeCause = null;
        this.touchedFailedValue = touchedFailedValue;
        this.current = current;
    }

    public ComputeHalt(Stack current, Throwable nativeCause) {
        this.uncaughtThrow = null;
        this.nativeCause = nativeCause;
        this.touchedFailedValue = null;
        this.current = current;
    }

    @Override
    public final boolean isHalt() {
        return true;
    }

}
