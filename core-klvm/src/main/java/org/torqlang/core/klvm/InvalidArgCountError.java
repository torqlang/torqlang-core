/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.List;

public final class InvalidArgCountError extends MachineError {

    public final int minCount;
    public final int maxCount;
    public final List<CompleteOrIdent> actualArgs;
    public final Object receiver;

    public InvalidArgCountError(int expectedCount, List<CompleteOrIdent> actualArgs, Object receiver) {
        this(expectedCount, expectedCount, actualArgs, receiver);
    }

    public InvalidArgCountError(int minCount, int maxCount, List<CompleteOrIdent> actualArgs, Object receiver) {
        super("Invalid arg count");
        this.minCount = minCount;
        this.maxCount = maxCount;
        this.actualArgs = actualArgs;
        this.receiver = receiver;
    }
}
