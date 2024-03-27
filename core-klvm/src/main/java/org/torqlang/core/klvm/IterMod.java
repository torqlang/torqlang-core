/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.List;

public final class IterMod {

    public static final Ident ITER_IDENT = Ident.create("Iter");
    public static final CompleteObj ITER_CLS = IterCls.SINGLETON;

    static void clsNew(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        final int expectedArgCount = 2;
        if (ys.size() != expectedArgCount) {
            throw new InvalidArgCountError(expectedArgCount, ys, "Iter.new");
        }
        Value source = ys.get(0).resolveValue(env);
        if (!(source instanceof IterSource iterable)) {
            throw new IllegalArgumentException(ys.get(0) + " must be a type of " +
                IterSource.class.getSimpleName());
        }
        ValueOrVar iter = iterable.iter();
        ValueOrVar target = ys.get(1).resolveValueOrVar(env);
        target.bindToValueOrVar(iter, null);
    }

    static final class IterCls implements CompleteObj {
        private static final IterCls SINGLETON = new IterCls();
        private static final CompleteProc ITER_CLS_NEW = IterMod::clsNew;

        private IterCls() {
        }

        @Override
        public final Value select(Feature feature) {
            if (feature.equals(CommonFeatures.NEW)) {
                return ITER_CLS_NEW;
            }
            throw new FeatureNotFoundError(this, feature);
        }

        @Override
        public final String toString() {
            return toKernelString();
        }
    }

}
