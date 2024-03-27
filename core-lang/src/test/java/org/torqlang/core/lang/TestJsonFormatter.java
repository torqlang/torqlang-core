/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TestJsonFormatter {

    @Test
    public void test() {

        String s;

        s = JsonFormatter.SINGLETON.format(null);
        assertEquals("null", s);

        s = JsonFormatter.SINGLETON.format(JsonNull.SINGLETON);
        assertEquals("null", s);

        s = JsonFormatter.SINGLETON.format(0);
        assertEquals("0", s);

        s = JsonFormatter.SINGLETON.format(0.005);
        assertEquals("0.005", s);

        s = JsonFormatter.SINGLETON.format(LocalDate.now());
        assertEquals("\"" + LocalDate.now() + "\"", s);

        s = JsonFormatter.SINGLETON.format(new BigDecimal("1.11"));
        assertEquals("\"1.11\"", s);

        s = JsonFormatter.SINGLETON.format(false);
        assertEquals("false", s);

        s = JsonFormatter.SINGLETON.format(true);
        assertEquals("true", s);

        s = JsonFormatter.SINGLETON.format("my-string");
        assertEquals("\"my-string\"", s);

        s = JsonFormatter.SINGLETON.format(List.of());
        assertEquals("[]", s);

        s = JsonFormatter.SINGLETON.format(List.of(1));
        assertEquals("[1]", s);

        s = JsonFormatter.SINGLETON.format(List.of(1, 2));
        assertEquals("[1,2]", s);

        s = JsonFormatter.SINGLETON.format(Map.of());
        assertEquals("{}", s);

        s = JsonFormatter.SINGLETON.format(Map.of("one", 1));
        assertEquals("{\"one\":1}", s);

        s = JsonFormatter.SINGLETON.format(Map.of("one", 1, "two", 2));
        assertTrue("{\"one\":1,\"two\":2}".equals(s) || "{\"two\":2,\"one\":1}".equals(s));
    }

    @Test
    public void testErrors() {
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
            () -> JsonFormatter.SINGLETON.format(new Object()));
        assertTrue(exc.getMessage().startsWith("Invalid JSON request: java.lang.Object"));
    }

}
