/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/*
 * Complete records (and tuples) are valid keys for Java hash maps.
 */
public interface CompleteRec extends Complete, Rec {

    static CompleteRec create(List<CompleteField> completeFields) {
        return BasicCompleteRec.createPrivatelyForKlvm(null, completeFields);
    }

    static CompleteRec create(Literal label, List<CompleteField> completeFields) {
        return BasicCompleteRec.createPrivatelyForKlvm(label, completeFields);
    }

    static CompleteRec singleton(Feature feature, Complete value) {
        return BasicCompleteRec.createPrivatelyForKlvm(null, List.of(new CompleteField(feature, value)));
    }

    /*
     * Return true if the other object is complete and this object entails the other object. Native objects, such as
     * Java hash maps, must utilize checkComplete() and isValidKey() before using Torqlang values as keys.
     */
    default boolean equalsComplete(Object other) {
        if (!(other instanceof Complete complete)) {
            return false;
        }
        try {
            return entails(complete, null);
        } catch (WaitException exc) {
            throw new IllegalStateException("Unexpected WaitException processing Complete value");
        }
    }

    @Override
    CompleteField fieldAt(int index);

    Complete findValue(Feature feature);

    default int hashCodeComplete(IdentityHashMap<CompleteRec, Object> memos) {
        if (memos != null) {
            if (memos.containsKey(this)) {
                throw new IllegalArgumentException("Circular reference error");
            }
        } else {
            memos = new IdentityHashMap<>();
        }
        memos.put(this, Value.PRESENT);
        int hash = 17;
        for (int i = 0; i < fieldCount(); i++) {
            CompleteField f = fieldAt(i);
            if (f.value instanceof CompleteRec completeRec) {
                if (memos.containsKey(this)) {
                    // Circular references get a constant hash
                    hash = 31 * hash + 11;
                } else {
                    hash = 31 * hash + completeRec.hashCodeComplete(memos);
                }
            } else {
                hash = 31 * hash + f.value.hashCode();
            }
            hash = 31 * hash + f.feature.hashCode();
        }
        return hash;
    }

    Complete select(Feature feature);

    /**
     * Convert to a native object without a label and without circular references. Throw an exception if a value
     * cannot be converted to a native value or if a circular reference is found.
     */
    @Override
    default Object toNativeValue() {
        return toNativeValue(null);
    }

    default Object toNativeValue(IdentityHashMap<CompleteRec, Object> memos) {
        if (memos != null) {
            if (memos.containsKey(this)) {
                throw new IllegalArgumentException("Circular reference error");
            }
        } else {
            memos = new IdentityHashMap<>();
        }
        memos.put(this, Value.PRESENT);
        Map<Object, Object> map = new HashMap<>(fieldCount());
        for (int i = 0; i < fieldCount(); i++) {
            CompleteField f = fieldAt(i);
            Object k = f.feature.toNativeValue();
            Object v;
            if (f.value instanceof CompleteRec completeRec) {
                v = completeRec.toNativeValue(memos);
            } else {
                v = f.value.toNativeValue();
            }
            map.put(k, v);
        }
        if (label().equals(DEFAULT_LABEL)) {
            return map;
        }
        return Map.of(Rec.$LABEL, label().toNativeValue(), Rec.$REC, map);
    }

    Complete valueAt(int index);
}
