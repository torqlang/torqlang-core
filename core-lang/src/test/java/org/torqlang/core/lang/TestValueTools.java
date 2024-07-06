/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.jupiter.api.Test;
import org.torqlang.core.klvm.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestValueTools {

    @Test
    public void test01() {

        Object v;

        // Null

        v = ValueTools.toKernelValue(null);
        assertEquals(Null.SINGLETON, v);
        v = ValueTools.toNativeValue((Complete) v);
        assertNull(v);

        // Boolean

        v = ValueTools.toKernelValue(Boolean.TRUE);
        assertEquals(Bool.TRUE, v);
        v = ValueTools.toNativeValue((Complete) v);
        assertEquals(Boolean.TRUE, v);

        v = ValueTools.toKernelValue(Boolean.FALSE);
        assertEquals(Bool.FALSE, v);
        v = ValueTools.toNativeValue((Complete) v);
        assertEquals(Boolean.FALSE, v);

        // Character and String

        v = ValueTools.toKernelValue('a');
        assertEquals(Char.of('a'), v);
        v = ValueTools.toNativeValue((Complete) v);
        assertEquals('a', v);

        v = ValueTools.toKernelValue("abc");
        assertEquals(Str.of("abc"), v);
        v = ValueTools.toNativeValue((Complete) v);
        assertEquals("abc", v);

        // Numbers

        v = ValueTools.toKernelValue(3);
        assertEquals(Int32.of(3), v);
        v = ValueTools.toNativeValue((Complete) v);
        assertEquals(3, v);

        v = ValueTools.toKernelValue(3L);
        assertEquals(Int64.of(3), v);
        v = ValueTools.toNativeValue((Complete) v);
        assertEquals(3L, v);

        v = ValueTools.toKernelValue(3.0f);
        assertEquals(Flt32.of(3), v);
        v = ValueTools.toNativeValue((Complete) v);
        assertEquals(3.0f, v);

        v = ValueTools.toKernelValue(3.0);
        assertEquals(Flt64.of(3), v);
        v = ValueTools.toNativeValue((Complete) v);
        assertEquals(3.0, v);

        v = ValueTools.toKernelValue(new BigDecimal("3.0"));
        assertEquals(Dec128.of(3), v);
        v = ValueTools.toNativeValue((Complete) v);
        assertEquals(new BigDecimal("3.0"), v);

        // Records

        v = ValueTools.toKernelValue(List.of());
        assertEquals(Rec.completeTupleBuilder().build(), v);
        v = ValueTools.toNativeValue((Complete) v);
        assertEquals(List.of(), v);

        v = ValueTools.toKernelValue(List.of(1));
        assertEquals(Rec.completeTupleBuilder().addValue(Int32.of(1)).build(), v);
        v = ValueTools.toNativeValue((Complete) v);
        assertEquals(List.of(1), v);

        v = ValueTools.toKernelValue(Map.of(Rec.$LABEL, "x", Rec.$REC, List.of(1)));
        assertEquals(Rec.completeTupleBuilder().setLabel(Str.of("x")).addValue(Int32.of(1)).build(), v);
        v = ValueTools.toNativeValue((Complete) v);
        assertEquals(Map.of(Rec.$LABEL, "x", Rec.$REC, List.of(1)), v);

        v = ValueTools.toKernelValue(Map.of());
        assertEquals(Rec.completeRecBuilder().build(), v);
        v = ValueTools.toNativeValue((Complete) v);
        assertEquals(Map.of(), v);

        v = ValueTools.toKernelValue(Map.of("k", 1));
        assertEquals(Rec.completeRecBuilder().addField(Str.of("k"), Int32.of(1)).build(), v);
        v = ValueTools.toNativeValue((Complete) v);
        assertEquals(Map.of("k", 1), v);

        v = ValueTools.toKernelValue(Map.of(Rec.$LABEL, "x", Rec.$REC, Map.of("k", 1)));
        assertEquals(Rec.completeRecBuilder().setLabel(Str.of("x")).addField(Str.of("k"), Int32.of(1)).build(), v);
        v = ValueTools.toNativeValue((Complete) v);
        assertEquals(Map.of(Rec.$LABEL, "x", Rec.$REC, Map.of("k", 1)), v);
    }

}
