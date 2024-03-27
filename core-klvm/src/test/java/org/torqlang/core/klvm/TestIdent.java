/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class TestIdent {

    @Test
    public void testCaptureFree() {
        Ident i;
        Set<Ident> free;
        Set<Ident> bound;
        Exception exc;

        i = Ident.create("test_ident");

        exc = assertThrows(IllegalStateException.class, () -> i.captureLexicallyFree(new HashSet<>(), new HashSet<>()));
        assertEquals(KlvmMessageText.IDENT_ALONE_CANNOT_DETERMINE_WHETHER_IT_IS_BOUND_OR_FREE, exc.getMessage());

        bound = new HashSet<>();
        free = new HashSet<>();
        Ident.captureLexicallyFree(i, bound, free);
        assertEquals(1, free.size());
        assertTrue(free.contains(i));

        bound = new HashSet<>();
        bound.add(i);
        free = new HashSet<>();
        Ident.captureLexicallyFree(i, bound, free);
        assertEquals(0, free.size());
        assertFalse(free.contains(i));
    }

    @Test
    public void testCreate() {
        Ident i;
        Exception exc;

        i = Ident.create("a");
        assertEquals("a", i.name);

        i = Ident.create("`this is an ident`");
        assertEquals("`this is an ident`", i.name);

        exc = assertThrows(IllegalArgumentException.class, () -> Ident.create("$not_allowed"));
        assertEquals(KlvmMessageText.USER_IDENTIFIERS_CANNOT_BEGIN_WITH_A_DOLLAR_SIGN, exc.getMessage());

        i = Ident.createPrivately("$system_identifier");
        assertEquals("$system_identifier", i.name);

        i = Ident.createSystemAnonymousIdent(1);
        assertEquals("$_1", i.name);

        i = Ident.createSystemArgIdent(1);
        assertEquals("$a1", i.name);

        i = Ident.createSystemVarIdent(1);
        assertEquals("$v1", i.name);
    }

    @Test
    public void testEquals() {
        Ident a = Ident.create("a");
        Ident b = Ident.create("b");
        Ident b_2 = Ident.create("b");

        assertNotEquals(a, b);
        assertEquals(b, b_2);
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(a, Int32.I32_0);
    }

    @Test
    public void testIsMethods() {

        Ident a = Ident.create("a");
        assertFalse(a.isAnonymous());
        assertFalse(a.isSystem());

        Ident anon = Ident.create("_");
        assertTrue(anon.isAnonymous());
        assertFalse(anon.isSystem());

        Ident systemAnon1 = Ident.createSystemAnonymousIdent(1);
        assertFalse(systemAnon1.isAnonymous());
        assertTrue(systemAnon1.isSystem());

        Ident systemArg1 = Ident.createSystemArgIdent(1);
        assertFalse(systemArg1.isAnonymous());
        assertTrue(systemArg1.isSystem());

        Ident systemVar1 = Ident.createSystemVarIdent(1);
        assertFalse(systemVar1.isAnonymous());
        assertTrue(systemVar1.isSystem());
    }

    @Test
    public void testResolve() throws Exception {

        Exception exc;

        Ident i1 = Ident.create("a");
        ValueOrVarSet value = Str.of("test value");
        var var = new Var(value);
        Ident i2 = Ident.create("b");
        Env env = Env.create(new EnvEntry(i1, var));

        assertEquals(value, i1.resolveValue(env));
        exc = assertThrows(IdentNotFoundError.class, () -> i2.resolveValue(env));
        assertEquals("Ident not found: b", exc.getMessage());

        assertEquals(value, i1.resolveValueOrVar(env));
        exc = assertThrows(IdentNotFoundError.class, () -> i2.resolveValueOrVar(env));
        assertEquals("Ident not found: b", exc.getMessage());

        assertEquals(var, i1.toVar(env));
        exc = assertThrows(IdentNotFoundError.class, () -> i2.toVar(env));
        assertEquals("Ident not found: b", exc.getMessage());
    }

    @Test
    public void testToValues() {
        Ident i = Ident.create("a");
        Ident s = Ident.createSystemArgIdent(1);
        Ident q1 = Ident.create("this is an ident");
        Ident q2 = Ident.create("this_is_an_ident");
        Ident a = Ident.create("this_&_this_is_an_ident");
        Ident u = Ident.create("_");

        assertEquals("a", i.toString());
        assertEquals("a", i.formatValue());
        assertEquals("a", i.toKernelString());

        assertEquals("$a1", s.toString());
        assertEquals("$a1", s.formatValue());
        assertEquals("$a1", s.toKernelString());

        assertEquals("this is an ident", q1.toString());
        assertEquals("this is an ident", q1.formatValue());
        // This value is quoted with backticks because of embedded spaces
        assertEquals("`this is an ident`", q1.toKernelString());

        assertEquals("this_is_an_ident", q2.toString());
        assertEquals("this_is_an_ident", q2.formatValue());
        assertEquals("this_is_an_ident", q2.toKernelString());

        assertEquals("this_&_this_is_an_ident", a.toString());
        assertEquals("this_&_this_is_an_ident", a.formatValue());
        // This value is quoted with backticks because of an embedded ampersand
        assertEquals("`this_&_this_is_an_ident`", a.toKernelString());

        assertEquals("_", u.toString());
        assertEquals("_", u.formatValue());
        assertEquals("_", u.toKernelString());
    }

}
