/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestEnvEntry {

    @Test
    public void testCreate() {

        Ident a = Ident.create("a");
        Str s = Str.of("test value");

        EnvEntry ee;
        Exception exc;

        ee = new EnvEntry(a, new Var(s));
        assertEquals(a, ee.ident);
        assertEquals(s, ee.var.valueOrVarSet());

        exc = assertThrows(NullPointerException.class, () -> new EnvEntry(null, new Var(s)));
        assertEquals("Ident is null", exc.getMessage());

        exc = assertThrows(NullPointerException.class, () -> new EnvEntry(a, null));
        assertEquals("Ident <a> mapped to null Var", exc.getMessage());
    }

}
