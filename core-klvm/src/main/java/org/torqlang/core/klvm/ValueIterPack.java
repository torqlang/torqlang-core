/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.List;

public final class ValueIterPack {

    public static final Ident VALUE_ITER_IDENT = Ident.create("ValueIter");
    public static final CompleteObj VALUE_ITER_CLS = ValueIterCls.SINGLETON;

    static void clsNew(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        final int expectedArgCount = 2;
        if (ys.size() != expectedArgCount) {
            throw new InvalidArgCountError(expectedArgCount, ys, "ValueIter.new");
        }
        Value source = ys.get(0).resolveValue(env);
        if (!(source instanceof ValueIterSource iterable)) {
            throw new IllegalArgumentException(ys.get(0) + " must be a type of " +
                ValueIterSource.class.getSimpleName());
        }
        ValueOrVar iter = iterable.valueIter();
        ValueOrVar target = ys.get(1).resolveValueOrVar(env);
        target.bindToValueOrVar(iter, null);
    }

    static final class ValueIterCls implements CompleteObj {
        private static final ValueIterCls SINGLETON = new ValueIterCls();
        private static final CompleteProc VALUE_ITER_CLS_NEW = ValueIterPack::clsNew;

        private ValueIterCls() {
        }

        @Override
        public final Value select(Feature feature) {
            if (feature.equals(CommonFeatures.NEW)) {
                return VALUE_ITER_CLS_NEW;
            }
            throw new FeatureNotFoundError(this, feature);
        }

        @Override
        public final String toString() {
            return toKernelString();
        }
    }

}
