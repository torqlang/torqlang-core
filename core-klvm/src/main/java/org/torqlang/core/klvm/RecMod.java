/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.List;

public final class RecMod {

    public static final Ident REC_IDENT = Ident.create("Rec");
    public static final CompleteObj REC_CLS = RecCls.SINGLETON;

    private static final ObjProcTable<RecCls> clsProcTable = ObjProcTable.<RecCls>builder()
        .addEntry(CommonFeatures.ASSIGN, RecMod::clsAssign)
        .build();

    /*
     * Rec.assign(from, to, result)
     *
     * Assign fields from the left argument to the right argument, which effectively adds or replaces fields on the
     * right side.
     *
     * var from = { b: 4, c: 5 }
     * var to = { a: 1, b: 2 }
     *
     * Rec.assign(from, to, result) // result = { a: 1, b: 4, c: 5 }
     */
    static void clsAssign(RecCls cls, List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        final int expectedArgCount = 3;
        if (ys.size() != expectedArgCount) {
            throw new InvalidArgCountError(expectedArgCount, ys, "Rec.assign");
        }
        PartialRecBuilder builder = Rec.partialRecBuilder();
        Rec rec0 = (Rec) ys.get(0).resolveValue(env);
        rec0.checkDetermined();
        for (int i = 0; i < rec0.fieldCount(); i++) {
            builder.addField(rec0.featureAt(i), rec0.valueAt(i));
        }
        Rec rec1 = (Rec) ys.get(1).resolveValue(env);
        rec1.checkDetermined();
        for (int i = 0; i < rec1.fieldCount(); i++) {
            Feature rec1Feat = rec1.featureAt(i);
            if (rec0.findValue(rec1Feat) == null) {
                builder.addField(rec1Feat, rec1.valueAt(i));
            }
        }
        Rec assigned = builder.build();
        ValueOrVar target = ys.get(2).resolveValueOrVar(env);
        target.bindToValue(assigned, null);
    }

    static final class RecCls implements CompleteObj {

        private static final RecCls SINGLETON = new RecCls();

        private RecCls() {
        }

        @Override
        public final Value select(Feature feature) {
            return clsProcTable.selectAndBind(this, feature);
        }

        @Override
        public final String toString() {
            return toKernelString();
        }
    }

}
