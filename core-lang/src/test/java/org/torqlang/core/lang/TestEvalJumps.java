/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;
import org.torqlang.core.klvm.Bool;
import org.torqlang.core.klvm.Ident;
import org.torqlang.core.klvm.Int32;
import org.torqlang.core.klvm.Var;

import static org.junit.Assert.assertEquals;

public class TestEvalJumps {

    @Test
    public void testFuncReturnFromIfElse() throws Exception {

        // a = 3

        String source = """
            begin
                func is_odd(x) in
                    if x % 2 == 0 then
                        return false
                    else
                        return true
                    end
                end
                x = is_odd(a)
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("a"), new Var(Int32.I32_3))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local is_odd in
                $create_proc(proc (x, $r) in
                    local $v0 in
                        local $v1 in
                            $mod(x, 2, $v1)
                            $eq($v1, 0, $v0)
                        end
                        if $v0 then
                            $bind(false, $r)
                            $jump_throw(3)
                        else
                            $bind(true, $r)
                            $jump_throw(3)
                        end
                        $jump_catch(3)
                    end
                end, is_odd)
                is_odd(a, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Bool.TRUE, e.varAtName("x").valueOrVarSet());

        // a = 4

        e = Evaluator.builder()
            .addVar(Ident.create("a"), new Var(Int32.I32_4))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(Bool.FALSE, e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testFuncReturnFromIfWithNoElse() throws Exception {

        // a = 3

        String source = """
            begin
                func add_1_if_odd(x) in
                    if x % 2 == 0 then
                        return x
                    end
                    x + 1
                end
                x = add_1_if_odd(a)
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("a"), new Var(Int32.I32_3))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local add_1_if_odd in
                $create_proc(proc (x, $r) in
                    local $v0 in
                        local $v1 in
                            $mod(x, 2, $v1)
                            $eq($v1, 0, $v0)
                        end
                        if $v0 then
                            $bind(x, $r)
                            $jump_throw(3)
                        end
                        $add(x, 1, $r)
                        $jump_catch(3)
                    end
                end, add_1_if_odd)
                add_1_if_odd(a, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.I32_4, e.varAtName("x").valueOrVarSet());

        // a = 6

        e = Evaluator.builder()
            .addVar(Ident.create("a"), new Var(Int32.I32_6))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(Int32.I32_6, e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testFuncReturnInt32() throws Exception {
        String source = """
            begin
                func echo(x) in
                    return x
                end
                x = echo(a)
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("a"), new Var(Int32.I32_3))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local echo in
                $create_proc(proc (x, $r) in
                    $bind(x, $r)
                    $jump_throw(3)
                    $jump_catch(3)
                end, echo)
                echo(a, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.I32_3, e.varAtName("x").valueOrVarSet());
    }

}
