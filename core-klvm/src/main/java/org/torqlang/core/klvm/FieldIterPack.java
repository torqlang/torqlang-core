/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.List;

public final class FieldIterPack {

    public static final Ident FIELD_ITER_IDENT = Ident.create("FieldIter");
    public static final CompleteObj FIELD_ITER_CLS = FieldIterCls.SINGLETON;

    static void clsNew(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        final int expectedArgCount = 2;
        if (ys.size() != expectedArgCount) {
            throw new InvalidArgCountError(expectedArgCount, ys, "FieldIter.new");
        }
        Value source = ys.get(0).resolveValue(env);
        if (!(source instanceof FieldIterSource iterable)) {
            throw new IllegalArgumentException(ys.get(0) + " must be a type of " +
                FieldIterSource.class.getSimpleName());
        }
        ValueOrVar iter = iterable.fieldIter();
        ValueOrVar target = ys.get(1).resolveValueOrVar(env);
        target.bindToValueOrVar(iter, null);
    }

    static final class FieldIterCls implements CompleteObj {
        private static final FieldIterCls SINGLETON = new FieldIterCls();
        private static final CompleteProc FIELD_ITER_CLS_NEW = FieldIterPack::clsNew;

        private FieldIterCls() {
        }

        @Override
        public final Value select(Feature feature) {
            if (feature.equals(CommonFeatures.NEW)) {
                return FIELD_ITER_CLS_NEW;
            }
            throw new FeatureNotFoundError(this, feature);
        }

        @Override
        public final String toString() {
            return toKernelString();
        }
    }

}
