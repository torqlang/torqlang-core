/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestListTools {

    @Test
    public void test01() {
        List<String> a = List.of();
        List<String> b = List.of();
        assertEquals(List.of(), ListTools.concat(String.class, a, b));
    }

    @Test
    public void test02() {
        List<String> a = List.of("one");
        List<String> b = List.of();
        assertEquals(List.of("one"), ListTools.concat(String.class, a, b));
    }

    @Test
    public void test03() {
        List<String> a = List.of();
        List<String> b = List.of("two");
        assertEquals(List.of("two"), ListTools.concat(String.class, a, b));
    }

    @Test
    public void test04() {
        List<String> a = List.of("one");
        List<String> b = List.of("two");
        assertEquals(List.of("one", "two"), ListTools.concat(String.class, a, b));
    }

    @Test
    public void test05() {
        List<String> a = List.of("one", "two");
        List<String> b = List.of("three");
        assertEquals(List.of("one", "two", "three"), ListTools.concat(String.class, a, b));
    }

    @Test
    public void test06() {
        List<String> a = List.of("one");
        List<String> b = List.of("two", "three");
        assertEquals(List.of("one", "two", "three"), ListTools.concat(String.class, a, b));
    }

    @Test
    public void test07() {
        List<String> a = List.of("one", "two");
        List<String> b = List.of("three", "four");
        assertEquals(List.of("one", "two", "three", "four"), ListTools.concat(String.class, a, b));
    }

    @Test
    public void test08() {
        List<String> a = List.of();
        String b = "two";
        assertEquals(List.of("two"), ListTools.append(String.class, a, b));
    }

    @Test
    public void test09() {
        List<String> a = List.of("one");
        String b = "two";
        assertEquals(List.of("one", "two"), ListTools.append(String.class, a, b));
    }

}
