/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Object procedure tables are immutable and global to all instances of its class. A class will create just one table
 * for all of its instances. When a caller selects an object feature, the object uses the procedure table to create a
 * binding.
 *
 * Noteworthy:
 *   1. There is no procedure table overhead when you instantiate an object since the table is created once for the
 *      class. Therefore, fine-grained types like Str can be implemented as objects without a performance impact.
 *   2. Selecting a feature creates and returns a bound procedure -- an ObjProcBinding.
 *   3. A bound procedure can be held and passed around like any other procedure. In essence, the fact that a procedure
 *      is bound to an object is irrelevant.
 *   4. Repeatedly selecting the same feature on an object can be optimized by reusing the bound procedure. This is
 *      possible because procedure tables are immutable and bound procedures are immutable. In essence, repeatedly
 *      selecting a feature always returns equivalent results.
 */
public final class ObjProcTable<T extends Obj> {

    private final Feature[] features;
    private final ObjProc<T>[] objProcs;

    private ObjProcTable(Feature[] features, ObjProc<T>[] objProcs) {
        this.features = features;
        this.objProcs = objProcs;
    }

    public static <T extends Obj> Builder<T> builder() {
        return new Builder<>();
    }

    public final ObjProcBinding<T> selectAndBind(T target, Feature selector) {
        int index = Arrays.binarySearch(features, selector);
        if (index < 0) {
            throw new FeatureNotFoundError(target, selector);
        }
        return new ObjProcBinding<>(target, objProcs[index]);
    }

    public static class Builder<T extends Obj> {

        private final List<Entry<T>> entries = new ArrayList<>();

        private Builder() {
        }

        public Builder<T> addEntry(Feature feature, ObjProc<T> objProc) {
            entries.add(new Entry<>(feature, objProc));
            return this;
        }

        public ObjProcTable<T> build() {
            entries.sort((a, b) -> FeatureComparator.SINGLETON.compare(a.feature, b.feature));
            Feature[] features = new Feature[entries.size()];
            @SuppressWarnings("unchecked")
            ObjProc<T>[] procs = (ObjProc<T>[]) Array.newInstance(ObjProc.class, entries.size());
            for (int i = 0; i < entries.size(); i++) {
                Entry<T> e = entries.get(i);
                features[i] = e.feature;
                procs[i] = e.objProc;
            }
            return new ObjProcTable<>(features, procs);
        }

        private record Entry<T>(Feature feature, ObjProc<T> objProc) {
        }

    }

}
