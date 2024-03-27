/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.List;

public final class RangeIterMod {

    public static final Ident RANGE_ITER_IDENT = Ident.create("RangeIter");
    public static final CompleteObj RANGE_ITER_CLS = RangeIterCls.SINGLETON;

    static void clsNew(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        final int expectedArgCount = 3;
        if (ys.size() != expectedArgCount) {
            throw new InvalidArgCountError(expectedArgCount, ys, "RangeIter.new");
        }
        // NOTE: RangeIter is not suspendable. Therefore, its arguments must be bound before we construct it.
        Int64 fromInt = (Int64) ys.get(0).resolveValue(env);
        Int64 toInt = (Int64) ys.get(1).resolveValue(env);
        RangeIter rangeIter = new RangeIter(fromInt, toInt);
        ValueOrVar target = ys.get(2).resolveValueOrVar(env);
        target.bindToValue(rangeIter, null);
    }

    static final class RangeIterCls implements CompleteObj {
        private static final RangeIterCls SINGLETON = new RangeIterCls();
        private static final CompleteProc RANGE_ITER_CLS_NEW = RangeIterMod::clsNew;

        private RangeIterCls() {
        }

        @Override
        public final Value select(Feature feature) {
            if (feature.equals(CommonFeatures.NEW)) {
                return RANGE_ITER_CLS_NEW;
            }
            throw new FeatureNotFoundError(this, feature);
        }

        @Override
        public final String toString() {
            return toKernelString();
        }
    }

}
