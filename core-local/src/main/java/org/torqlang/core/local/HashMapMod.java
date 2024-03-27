/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.klvm.*;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

final class HashMapMod {

    public static final Ident HASH_MAP_IDENT = Ident.create("HashMap");
    public static final CompleteObj HASH_MAP_CLS = HashMapCls.SINGLETON;

    private static final ObjProcTable<HashMapObj> objProcTable = ObjProcTable.<HashMapObj>builder()
        .addEntry(CommonFeatures.GET, HashMapMod::objGet)
        .addEntry(CommonFeatures.PUT, HashMapMod::objPut)
        .build();

    static void clsNew(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        final int expectedCount = 1;
        if (ys.size() != expectedCount) {
            throw new InvalidArgCountError(expectedCount, ys, "HashMap.new");
        }
        ValueOrVar y0 = ys.get(0).resolveValueOrVar(env);
        HashMapObj obj = new HashMapObj();
        y0.bindToValue(obj, null);
    }

    static void objGet(HashMapObj obj, List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        final int expectedCount = 2;
        if (ys.size() != expectedCount) {
            throw new InvalidArgCountError(expectedCount, ys, "HashMap.get");
        }
        Value candidateKey = ys.get(0).resolveValue(env);
        Complete key = candidateKey.checkComplete();
        if (!key.isValidKey()) {
            throw new NotValidKeyError(key);
        }
        ValueOrVar elem = obj.state.get(key);
        // An element not found results in the Nothing value
        if (elem == null) {
            elem = Nothing.SINGLETON;
        }
        ValueOrVar target = ys.get(1).resolveValueOrVar(env);
        target.bindToValueOrVar(elem, null);
    }

    static void objPut(HashMapObj obj, List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        final int expectedCount = 2;
        if (ys.size() != expectedCount) {
            throw new InvalidArgCountError(expectedCount, ys, "HashMap.put");
        }
        Value candidateKey = ys.get(0).resolveValue(env);
        Complete key = candidateKey.checkComplete();
        if (!key.isValidKey()) {
            throw new NotValidKeyError(key);
        }
        ValueOrVar elem = ys.get(1).resolveValueOrVar(env);
        obj.state.put(key, elem);
    }

    static class HashMapCls implements CompleteObj {
        private static final HashMapCls SINGLETON = new HashMapCls();
        private static final CompleteProc HASH_MAP_CLS_NEW = HashMapMod::clsNew;

        private HashMapCls() {
        }

        @Override
        public final Value select(Feature feature) {
            if (feature.equals(CommonFeatures.NEW)) {
                return HASH_MAP_CLS_NEW;
            }
            throw new FeatureNotFoundError(this, feature);
        }

        @Override
        public final String toString() {
            return toKernelString();
        }
    }

    static class HashMapObj implements Obj, ValueIterSource {
        private final HashMap<Complete, ValueOrVar> state;

        HashMapObj() {
            state = new HashMap<>();
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
            HashMapObj that = (HashMapObj) other;
            return state.equals(that.state);
        }

        @Override
        public final int hashCode() {
            return state.hashCode();
        }

        @Override
        public final boolean isValidKey() {
            return false;
        }

        @Override
        public final Value select(Feature feature) {
            return objProcTable.selectAndBind(this, feature);
        }

        public final HashMap<Complete, ValueOrVar> state() {
            return state;
        }

        @Override
        public final ValueOrVar valueIter() {
            return new ObjValueIter(state);
        }

        static class ObjValueIter extends AbstractIter implements ValueIter {
            public ObjValueIter(HashMap<Complete, ValueOrVar> hashMap) {
                super(hashMap.values());
            }
        }
    }

}
