/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.jupiter.api.Test;
import org.torqlang.core.klvm.Ident;
import org.torqlang.core.klvm.Int32;
import org.torqlang.core.klvm.Str;
import org.torqlang.core.klvm.Var;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestEvalAlgebraicNums {

    @Test
    public void testAddAndMultiply() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource("x = 2 + 3 * 5")
            .perform();
        assertEquals("x = 2 + 3 * 5", e.sntcOrExpr().toString());
        String expected = """
            local $v0 in
                $mult(3, 5, $v0)
                $add(2, $v0, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.of(17), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testAddAndMultiplyWithInitVars() throws Exception {
        String source = """
            begin
                var a = 2, b = 3, c = 5
                x = a + b * c
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local a = 2, b = 3, c = 5 in
                local $v0 in
                    $mult(b, c, $v0)
                    $add(a, $v0, x)
                end
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.of(17), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testAddAndMultiplyWithVars() throws Exception {
        String source = """
            begin
                var a, b, c
                a = 2
                b = 3
                c = 5
                x = a + b * c
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local a, b, c in
                $bind(2, a)
                $bind(3, b)
                $bind(5, c)
                local $v0 in
                    $mult(b, c, $v0)
                    $add(a, $v0, x)
                end
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.of(17), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testConcatAsStrings() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("a"), new Var(Int32.I32_1))
            .addVar(Ident.create("x"))
            .setSource("x = 'Hello, ' + a + '! is ' + a")
            .perform();
        assertEquals("x = 'Hello, ' + a + '! is ' + a", e.sntcOrExpr().toString());
        String expected = """
            local $v0 in
                local $v1 in
                    $add('Hello, ', a, $v1)
                    $add($v1, '! is ', $v0)
                end
                $add($v0, a, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Str.of("Hello, 1! is 1"), e.varAtName("x").valueOrVarSet());
    }

}
