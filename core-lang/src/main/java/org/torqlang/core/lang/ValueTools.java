/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.klvm.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ValueTools {

    public static Object toNativeValue(Complete value) {
        return value.toNativeValue();
    }

    public static Complete toKernelValue(Object value) {
        return toKernelValue(null, value);
    }

    private static Complete toKernelValue(Object label, Object value) {
        if (value == null || value == JsonNull.SINGLETON) {
            return Nothing.SINGLETON;
        }
        if (value instanceof Complete) {
            return (Complete) value;
        }
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
        if (value instanceof BigDecimal bigDecimal) {
            return Dec128.of(bigDecimal);
        }
        if (value instanceof Map<?, ?> m) {
            if (m.size() == 2) {
                Object parsedLabel = m.get(Rec.$LABEL);
                if (parsedLabel != null) {
                    if (label != null) {
                        throw new IllegalArgumentException("Label cannot follow a label");
                    }
                    Object parsedValue = m.get(Rec.$REC);
                    if (parsedValue != null) {
                        if (!(parsedValue instanceof Map) && !(parsedValue instanceof List)) {
                            throw new IllegalArgumentException("Label must precede a Map or List");
                        }
                        return toKernelValue(parsedLabel, parsedValue);
                    }
                }
            }
            List<CompleteField> fs = new ArrayList<>();
            for (Map.Entry<?, ?> e : m.entrySet()) {
                Complete k = toKernelValue(e.getKey());
                if (!(k instanceof Feature f)) {
                    throw new IllegalArgumentException("Map key must be a Feature: " + e.getKey());
                }
                Complete v = toKernelValue(e.getValue());
                fs.add(new CompleteField(f, v));
            }
            Literal kernelLabel = null;
            if (label != null) {
                kernelLabel = (Literal) toKernelValue(label);
            }
            return CompleteRec.create(kernelLabel, fs);
        }
        if (value instanceof List<?> l) {
            List<Complete> es = new ArrayList<>();
            for (Object e : l) {
                es.add(toKernelValue(e));
            }
            Literal kernelLabel = null;
            if (label != null) {
                kernelLabel = (Literal) toKernelValue(label);
            }
            return CompleteTuple.create(kernelLabel, es);
        }
        throw new IllegalArgumentException("Cannot convert to kernel value: " + value);
    }

}
