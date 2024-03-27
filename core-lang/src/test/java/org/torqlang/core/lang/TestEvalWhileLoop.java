/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;
import org.torqlang.core.klvm.*;

import static org.junit.Assert.assertEquals;

public class TestEvalWhileLoop {

    @Test
    public void testConditionWithCounter() throws Exception {

        // a = 5

        String source = """
            begin
                var c = Cell.new(0)
                while @c < a do
                    c := @c + 1
                end
                x = @c
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(CellMod.CELL_IDENT, new Var(CellMod.CELL_CLS))
            .addVar(Ident.create("a"), new Var(Int32.of(5)))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local c in
                $select_apply(Cell, ['new'], 0, c)
                local $guard, $while in
                    $create_proc(proc ($r) in // free vars: a, c
                        local $v0 in
                            $get(c, $v0)
                            $lt($v0, a, $r)
                        end
                    end, $guard)
                    $create_proc(proc () in // free vars: $guard, $while, c
                        local $v1 in
                            $guard($v1)
                            if $v1 then
                                local $v2 in
                                    local $v3 in
                                        $get(c, $v3)
                                        $add($v3, 1, $v2)
                                    end
                                    $set(c, $v2)
                                end
                                $while()
                            end
                        end
                    end, $while)
                    $while()
                end
                $get(c, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.I32_5, e.varAtName("x").valueOrVarSet());

        // a = 3

        e = Evaluator.builder()
            .addVar(CellMod.CELL_IDENT, new Var(CellMod.CELL_CLS))
            .addVar(Ident.create("a"), new Var(Int32.of(3)))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(Int32.I32_3, e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testWithBreak() throws Exception {

        // a = 5

        String source = """
            begin
                var c = Cell.new(0)
                while true do
                    if @c >= a then
                        break
                    end
                    c := @c + 1
                end
                x = @c
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(CellMod.CELL_IDENT, new Var(CellMod.CELL_CLS))
            .addVar(Ident.create("a"), new Var(Int32.of(5)))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local c in
                $select_apply(Cell, ['new'], 0, c)
                local $guard, $while in
                    $create_proc(proc ($r) in
                        $bind(true, $r)
                    end, $guard)
                    $create_proc(proc () in // free vars: $guard, $while, a, c
                        local $v0 in
                            $guard($v0)
                            if $v0 then
                                local $v1 in
                                    local $v2 in
                                        $get(c, $v2)
                                        $ge($v2, a, $v1)
                                    end
                                    if $v1 then
                                        $jump_throw(1)
                                    end
                                    local $v3 in
                                        local $v4 in
                                            $get(c, $v4)
                                            $add($v4, 1, $v3)
                                        end
                                        $set(c, $v3)
                                    end
                                    $while()
                                end
                            end
                        end
                    end, $while)
                    $while()
                    $jump_catch(1)
                end
                $get(c, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.I32_5, e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testWithBreakAndContinue() throws Exception {

        // break at 2

        /*
         * If `continue` failed, the answer would be 12, not 13
         * If `break` failed, the loop will not terminate.
         */

        String source = """
            begin
                var i = Cell.new(-1)
                var c = Cell.new(0)
                while true do
                    i := @i + 1
                    c := @c + 1
                    if @i == 1 then
                        c := @c + 10
                        continue
                    end
                    if @i == 1 then
                        break
                    end
                    if @i == 2 then
                        break
                    end
                end
                x = @c
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(CellMod.CELL_IDENT, new Var(CellMod.CELL_CLS))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local i, c in
                local $v0 in
                    $negate(1, $v0)
                    $select_apply(Cell, ['new'], $v0, i)
                end
                $select_apply(Cell, ['new'], 0, c)
                local $guard, $while in
                    $create_proc(proc ($r) in
                        $bind(true, $r)
                    end, $guard)
                    $create_proc(proc () in // free vars: $guard, $while, c, i
                        local $v1 in
                            $guard($v1)
                            if $v1 then
                                local $v6, $v10, $v12 in
                                    local $v2 in
                                        local $v3 in
                                            $get(i, $v3)
                                            $add($v3, 1, $v2)
                                        end
                                        $set(i, $v2)
                                    end
                                    local $v4 in
                                        local $v5 in
                                            $get(c, $v5)
                                            $add($v5, 1, $v4)
                                        end
                                        $set(c, $v4)
                                    end
                                    local $v7 in
                                        $get(i, $v7)
                                        $eq($v7, 1, $v6)
                                    end
                                    if $v6 then
                                        local $v8 in
                                            local $v9 in
                                                $get(c, $v9)
                                                $add($v9, 10, $v8)
                                            end
                                            $set(c, $v8)
                                        end
                                        $jump_throw(2)
                                    end
                                    local $v11 in
                                        $get(i, $v11)
                                        $eq($v11, 1, $v10)
                                    end
                                    if $v10 then
                                        $jump_throw(1)
                                    end
                                    local $v13 in
                                        $get(i, $v13)
                                        $eq($v13, 2, $v12)
                                    end
                                    if $v12 then
                                        $jump_throw(1)
                                    end
                                    $jump_catch(2)
                                    $while()
                                end
                            end
                        end
                    end, $while)
                    $while()
                    $jump_catch(1)
                end
                $get(c, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.of(13), e.varAtName("x").valueOrVarSet());
    }

}
