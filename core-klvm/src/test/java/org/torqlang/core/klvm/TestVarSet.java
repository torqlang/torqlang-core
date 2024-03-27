/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.junit.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;
import static org.torqlang.core.klvm.VarSet.EMPTY_VAR_SET;

public class TestVarSet {

    @Test
    public void testCreatePrivately() {
        Var v1 = new Var();
        VarSet vs1 = VarSet.createPrivatelyForKlvm(new Var[]{v1}, 1);
        assertEquals(1, vs1.size());
        assertTrue(vs1.contains(v1));
        Iterator<Var> iter = vs1.iterator();
        assertTrue(iter.hasNext());
        assertEquals(v1, iter.next());
        assertFalse(iter.hasNext());
        Exception exc = assertThrows(NoSuchElementException.class, iter::next);
        assertEquals("Next element is not present", exc.getMessage());
    }

    @Test
    public void testEmptyVarSet() {
        Var v1 = new Var();
        assertEquals(0, EMPTY_VAR_SET.size());
        assertFalse(EMPTY_VAR_SET.contains(v1));
        Iterator<Var> iter = EMPTY_VAR_SET.iterator();
        assertFalse(iter.hasNext());
        Exception exc = assertThrows(NoSuchElementException.class, iter::next);
        assertEquals("Next element is not present", exc.getMessage());
    }

    @Test
    public void testEmptyVarSetAdd() {
        Var v1 = new Var();
        VarSet vs1 = EMPTY_VAR_SET.add(v1);
        assertEquals(1, vs1.size());
        assertTrue(vs1.contains(v1));
        Iterator<Var> iter = vs1.iterator();
        assertTrue(iter.hasNext());
        assertEquals(v1, iter.next());
        assertFalse(iter.hasNext());
        Exception exc = assertThrows(NoSuchElementException.class, iter::next);
        assertEquals("Next element is not present", exc.getMessage());
    }

    @Test
    public void testToString() {

        assertEquals("<<$var_set>>", EMPTY_VAR_SET.toString());

        Var v1 = new Var();
        VarSet vs1 = EMPTY_VAR_SET.add(v1);
        String toString = vs1.toString();
        String[] lines = toString.split(",");
        assertEquals(1, lines.length);
        assertTrue(lines[0].startsWith("<<$var_set <<$var "));
        assertTrue(lines[0].endsWith(">>"));

        Var v2 = new Var();
        VarSet vs2 = vs1.add(v2);
        toString = vs2.toString();
        lines = toString.split(",");
        assertEquals(2, lines.length);
        assertTrue(lines[0].startsWith("<<$var_set <<$var "));
        assertTrue(lines[1].endsWith(">>"));
    }

    @Test
    public void testToKernelString() {

        assertEquals("<<$var_set>>", EMPTY_VAR_SET.toKernelString());

        Var v1 = new Var();
        VarSet vs1 = EMPTY_VAR_SET.add(v1);
        String toKernelString = vs1.toKernelString();
        String[] lines = toKernelString.split(",");
        assertEquals(1, lines.length);
        assertTrue(lines[0].startsWith("<<$var_set <<$var "));
        assertTrue(lines[0].endsWith(">>"));

        Var v2 = new Var();
        VarSet vs2 = vs1.add(v2);
        toKernelString = vs2.toKernelString();
        lines = toKernelString.split(",");
        assertEquals(2, lines.length);
        assertTrue(lines[0].startsWith("<<$var_set <<$var "));
        assertTrue(lines[1].endsWith(">>"));
    }

    @Test
    public void testUnion() {

        Var v1 = new Var();
        VarSet vs1 = EMPTY_VAR_SET.add(v1);
        assertEquals(1, vs1.size());
        assertTrue(vs1.contains(v1));

        Var v2 = new Var();
        VarSet vs2 = EMPTY_VAR_SET.add(v2);
        assertEquals(1, vs2.size());
        assertFalse(vs2.contains(v1));
        assertTrue(vs2.contains(v2));

        VarSet vs3 = VarSet.union(vs1, vs2);
        assertEquals(2, vs3.size());
        assertTrue(vs3.contains(v1));
        assertTrue(vs3.contains(v2));

        VarSet vs4 = VarSet.union(vs1, vs1);
        assertEquals(vs1, vs4);

        VarSet vs5 = vs1.add(v1);
        assertEquals(vs1, vs5);

        VarSet vs6 = vs3.add(v1);
        assertEquals(vs3, vs6);
        VarSet vs7 = vs3.add(v2);
        assertEquals(vs3, vs7);
    }

}
