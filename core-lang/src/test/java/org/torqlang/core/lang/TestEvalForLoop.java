/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.jupiter.api.Test;
import org.torqlang.core.klvm.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestEvalForLoop {

    @Test
    public void testWithRange() throws Exception {

        // k = 5

        String source = """
            begin
                var counter = Cell.new(0)
                for i in RangeIter.new(0, a) do
                    counter := @counter + 1
                end
                x = @counter
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(CellPack.CELL_IDENT, new Var(CellPack.CELL_CLS))
            .addVar(RangeIterPack.RANGE_ITER_IDENT, new Var(RangeIterPack.RANGE_ITER_CLS))
            .addVar(Ident.create("a"), new Var(Int32.of(5)))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local counter in
                $select_apply(Cell, ['new'], 0, counter)
                local $iter, $for in
                    $select_apply(RangeIter, ['new'], 0, a, $iter)
                    $create_proc(proc () in // free vars: $for, $iter, counter
                        local i, $v0 in
                            $iter(i)
                            $ne(i, eof, $v0)
                            if $v0 then
                                local $v1 in
                                    local $v2 in
                                        $get(counter, $v2)
                                        $add($v2, 1, $v1)
                                    end
                                    $set(counter, $v1)
                                end
                                $for()
                            end
                        end
                    end, $for)
                    $for()
                end
                $get(counter, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.I32_5, e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testWithRangeButBreakAt2() throws Exception {

        // k = 5 but break at 2

        String source = """
            begin
                var counter = Cell.new(0)
                for i in RangeIter.new(0, a) do
                    counter := @counter + 1
                    if i == 2 then
                        break
                    end
                end
                x = @counter
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(CellPack.CELL_IDENT, new Var(CellPack.CELL_CLS))
            .addVar(RangeIterPack.RANGE_ITER_IDENT, new Var(RangeIterPack.RANGE_ITER_CLS))
            .addVar(Ident.create("a"), new Var(Int32.of(5)))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local counter in
                $select_apply(Cell, ['new'], 0, counter)
                local $iter, $for in
                    $select_apply(RangeIter, ['new'], 0, a, $iter)
                    $create_proc(proc () in // free vars: $for, $iter, counter
                        local i, $v0 in
                            $iter(i)
                            $ne(i, eof, $v0)
                            if $v0 then
                                local $v3 in
                                    local $v1 in
                                        local $v2 in
                                            $get(counter, $v2)
                                            $add($v2, 1, $v1)
                                        end
                                        $set(counter, $v1)
                                    end
                                    $eq(i, 2, $v3)
                                    if $v3 then
                                        $jump_throw(1)
                                    end
                                    $for()
                                end
                            end
                        end
                    end, $for)
                    $for()
                    $jump_catch(1)
                end
                $get(counter, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.I32_3, e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testWithRangeWithBreakAndContinue() throws Exception {

        // k = 5 but break at 2

        /*
         * If `continue` failed, the answer would be 12, not 13
         * If `break` failed, the answer would be 15, not 13.
         */
        String source = """
            begin
                var c = Cell.new(0)
                for i in RangeIter.new(0, k) do
                    c := @c + 1
                    if i == 1 then
                        c := @c + 10
                        continue
                    end
                    if i == 1 then
                        break
                    end
                    if i == 2 then
                        break
                    end
                end
                z = @c
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(CellPack.CELL_IDENT, new Var(CellPack.CELL_CLS))
            .addVar(RangeIterPack.RANGE_ITER_IDENT, new Var(RangeIterPack.RANGE_ITER_CLS))
            .addVar(Ident.create("k"), new Var(Int32.of(5)))
            .addVar(Ident.create("z"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local c in
                $select_apply(Cell, ['new'], 0, c)
                local $iter, $for in
                    $select_apply(RangeIter, ['new'], 0, k, $iter)
                    $create_proc(proc () in // free vars: $for, $iter, c
                        local i, $v0 in
                            $iter(i)
                            $ne(i, eof, $v0)
                            if $v0 then
                                local $v3, $v6, $v7 in
                                    local $v1 in
                                        local $v2 in
                                            $get(c, $v2)
                                            $add($v2, 1, $v1)
                                        end
                                        $set(c, $v1)
                                    end
                                    $eq(i, 1, $v3)
                                    if $v3 then
                                        local $v4 in
                                            local $v5 in
                                                $get(c, $v5)
                                                $add($v5, 10, $v4)
                                            end
                                            $set(c, $v4)
                                        end
                                        $jump_throw(2)
                                    end
                                    $eq(i, 1, $v6)
                                    if $v6 then
                                        $jump_throw(1)
                                    end
                                    $eq(i, 2, $v7)
                                    if $v7 then
                                        $jump_throw(1)
                                    end
                                    $jump_catch(2)
                                    $for()
                                end
                            end
                        end
                    end, $for)
                    $for()
                    $jump_catch(1)
                end
                $get(c, z)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.of(13), e.varAtName("z").valueOrVarSet());
    }

}
