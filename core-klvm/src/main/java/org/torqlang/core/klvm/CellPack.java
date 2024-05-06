/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.List;

public final class CellPack {

    public static final Ident CELL_IDENT = Ident.create("Cell");
    public static final CompleteObj CELL_CLS = CellCls.SINGLETON;

    private static final ObjProcTable<CellObj> objProcTable = ObjProcTable.<CellObj>builder()
        .build();

    static void clsNew(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        final int expectedArgCount = 2;
        if (ys.size() != expectedArgCount) {
            throw new InvalidArgCountError(expectedArgCount, ys, "Cell.new");
        }
        ValueOrVar cellContent = ys.get(0).resolveValueOrVar(env);
        CellObj cellObj = new CellObj(cellContent);
        ValueOrVar target = ys.get(1).resolveValueOrVar(env);
        target.bindToValue(cellObj, null);
    }

    static class CellCls implements CompleteObj {
        private static final CellCls SINGLETON = new CellCls();
        private static final CompleteProc CELL_CLS_NEW = CellPack::clsNew;

        private CellCls() {
        }

        @Override
        public final Value select(Feature feature) {
            if (feature.equals(CommonFeatures.NEW)) {
                return CELL_CLS_NEW;
            }
            throw new FeatureNotFoundError(this, feature);
        }

        @Override
        public final String toString() {
            return toKernelString();
        }
    }

    static class CellObj implements Obj {

        private ValueOrVar valueOrVar;

        private CellObj() {
        }

        CellObj(ValueOrVar valueOrVar) {
            this.valueOrVar = valueOrVar;
        }

        final ValueOrVar get() {
            return valueOrVar;
        }

        @Override
        public final boolean isValidKey() {
            return true;
        }

        @Override
        public final ValueOrVar select(Feature feature) {
            return objProcTable.selectAndBind(this, feature);
        }

        final ValueOrVar set(ValueOrVar valueOrVar) {
            ValueOrVar previous = this.valueOrVar;
            this.valueOrVar = valueOrVar;
            return previous;
        }

        @Override
        public final String toString() {
            return toKernelString();
        }
    }

}
