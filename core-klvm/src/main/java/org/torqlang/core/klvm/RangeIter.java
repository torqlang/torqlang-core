/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.List;

public final class RangeIter implements Proc {

    private final static int RANGE_ITER_ARG_COUNT = 1;

    private final Int64 fromInt;
    private final Int64 toInt;
    private Int64 nextInt;

    public RangeIter(Int64 fromInt, Int64 toInt) {
        this.fromInt = fromInt;
        this.toInt = toInt;
        this.nextInt = fromInt;
    }

    @Override
    public final void apply(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        if (ys.size() != RANGE_ITER_ARG_COUNT) {
            throw new InvalidArgCountError(RANGE_ITER_ARG_COUNT, ys, this);
        }
        ValueOrVar y = ys.get(0).resolveValueOrVar(env);
        if (nextInt.compareValueTo(toInt) < 0) {
            y.bindToValue(nextInt, null);
            nextInt = (Int64) nextInt.addFrom(Int32.I32_1);
        } else {
            y.bindToValue(Eof.SINGLETON, null);
        }
    }

    @Override
    public final boolean isValidKey() {
        return true;
    }

    @Override
    public final String toString() {
        return toKernelString();
    }

}
