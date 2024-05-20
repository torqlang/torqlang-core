/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestLocalAddress {

    @Test
    public void test01() {
        LocalAddress a;

        a = LocalAddress.create("a");
        assertEquals("a", a.path());

        a = LocalAddress.create("/a");
        assertEquals("a", a.path());

        a = LocalAddress.create("a/");
        assertEquals("a", a.path());

        a = LocalAddress.create("/a/");
        assertEquals("a", a.path());

        a = LocalAddress.create("a/b");
        assertEquals("a/b", a.path());

        a = LocalAddress.create("/a/b");
        assertEquals("a/b", a.path());

        a = LocalAddress.create("a/b/");
        assertEquals("a/b", a.path());

        a = LocalAddress.create("/a/b/");
        assertEquals("a/b", a.path());

        a = LocalAddress.create("a/b/c");
        assertEquals("a/b/c", a.path());

        a = LocalAddress.create("/a/b/c");
        assertEquals("a/b/c", a.path());

        a = LocalAddress.create("a/b/c/");
        assertEquals("a/b/c", a.path());

        a = LocalAddress.create("/a/b/c/");
        assertEquals("a/b/c", a.path());

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class, () -> LocalAddress.create("/"));
        assertEquals("Invalid path: /", exc.getMessage());
    }

}
