/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.klvm.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

final class LocalDateMod {
    public static final Ident LOCAL_DATE_IDENT = Ident.create("LocalDate");
    public static final CompleteObj LOCAL_DATE_CLS = LocalDateCls.SINGLETON;

    private static final ObjProcTable<LocalDateObj> objProcTable = ObjProcTable.<LocalDateObj>builder()
        .build();

    static void clsNew(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        final int expectedCount = 2;
        if (ys.size() != expectedCount) {
            throw new InvalidArgCountError(expectedCount, ys, "LocalDate.new");
        }
        Str dateStr = (Str) ys.get(0).resolveValue(env);
        LocalDateObj localDateObj = new LocalDateObj(LocalDate.parse(dateStr.value));
        ValueOrVar target = ys.get(1).resolveValueOrVar(env);
        target.bindToValue(localDateObj, null);
    }

    static class LocalDateCls implements CompleteObj {
        private static final LocalDateCls SINGLETON = new LocalDateCls();
        private static final CompleteProc LOCAL_DATE_CLS_NEW = LocalDateMod::clsNew;

        private LocalDateCls() {
        }

        @Override
        public final Value select(Feature feature) {
            if (feature.equals(CommonFeatures.NEW)) {
                return LOCAL_DATE_CLS_NEW;
            }
            throw new FeatureNotFoundError(this, feature);
        }

        @Override
        public final String toString() {
            return toKernelString();
        }
    }

    static class LocalDateObj implements CompleteObj {
        private final LocalDate state;

        public LocalDateObj(LocalDate state) {
            this.state = state;
        }

        @Override
        public final boolean entails(Value operand, Set<Memo> memos) {
            return this.equals(operand);
        }

        @Override
        public final boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || getClass() != other.getClass()) {
                return false;
            }
            LocalDateObj that = (LocalDateObj) other;
            return state.equals(that.state);
        }

        @Override
        public final String formatValue() {
            return state.toString();
        }

        @Override
        public final int hashCode() {
            return state.hashCode();
        }

        @Override
        public final boolean isValidKey() {
            return true;
        }

        @Override
        public final Value select(Feature feature) {
            throw new FeatureNotFoundError(this, feature);
        }

        public final LocalDate state() {
            return state;
        }

        @Override
        public final Object toNativeValue() {
            return state;
        }

        @Override
        public final String toString() {
            return toKernelString();
        }
    }

}
