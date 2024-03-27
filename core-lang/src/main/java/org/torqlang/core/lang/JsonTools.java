/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.klvm.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonTools {

    public static final String TORQLANG_COLON_LABEL_COLON = "torqlang:label:";

    public static Object toJsonValue(Complete value) {
        return value.toNativeValue();
    }

    public static Complete toKernelValue(Object value) {
        return toKernelValue(null, value);
    }

    private static Complete toKernelValue(String label, Object value) {
        if (value instanceof String string) {
            return Str.of(string);
        }
        if (value instanceof Integer integer) {
            return Int32.of(integer);
        }
        if (value instanceof Long longValue) {
            return Int64.of(longValue);
        }
        if (value instanceof Boolean booleanValue) {
            return Bool.of(booleanValue);
        }
        if (value instanceof Character characterValue) {
            return Char.of(characterValue);
        }
        if (value instanceof Float floatValue) {
            return Flt32.of(floatValue);
        }
        if (value instanceof Double doubleValue) {
            return Flt64.of(doubleValue);
        }
        if (value instanceof Map<?, ?> m) {
            if (m.size() == 1) {
                Map.Entry<?, ?> e = m.entrySet().iterator().next();
                if (e.getKey() instanceof String s) {
                    if (s.startsWith(TORQLANG_COLON_LABEL_COLON)) {
                        if (label != null) {
                            throw new IllegalArgumentException("A label cannot be followed by a label");
                        }
                        if (!(e.getValue() instanceof Map) && !(e.getValue() instanceof List)) {
                            throw new IllegalArgumentException("A label must precede a Map or List");
                        }
                        String thisLabel = s.substring(TORQLANG_COLON_LABEL_COLON.length());
                        return toKernelValue(thisLabel, e.getValue());
                    }
                }
            }
            List<CompleteField> fs = new ArrayList<>();
            for (Map.Entry<?, ?> e : m.entrySet()) {
                if (!(e.getKey() instanceof String k)) {
                    throw new IllegalArgumentException("JSON key must be a String: " + e.getKey());
                }
                Complete v = toKernelValue(e.getValue());
                fs.add(new CompleteField(Str.of(k), v));
            }
            return CompleteRec.create(label != null ? Str.of(label) : null, fs);
        }
        if (value instanceof List<?> l) {
            List<Complete> es = new ArrayList<>();
            for (Object e : l) {
                es.add(toKernelValue(e));
            }
            return CompleteTuple.create(label != null ? Str.of(label) : null, es);
        }
        throw new IllegalArgumentException("Cannot convert to kernel value: " + value);
    }

}
