/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.jupiter.api.Test;
import org.torqlang.core.klvm.CellPack;
import org.torqlang.core.klvm.Ident;
import org.torqlang.core.klvm.Int32;
import org.torqlang.core.klvm.Var;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestEvalAccessNums {

    @Test
    public void testInt32() throws Exception {
        String source = """
            begin
                var a = Cell.new(5)
                x = @a
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(CellPack.CELL_IDENT, new Var(CellPack.CELL_CLS))
            .addVar(Ident.create("a"))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local a in
                $select_apply(Cell, ['new'], 5, a)
                $get(a, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.I32_5, e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testInt32WithAdd() throws Exception {
        String source = """
            begin
                var a = Cell.new(5)
                x = @a + 3
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(CellPack.CELL_IDENT, new Var(CellPack.CELL_CLS))
            .addVar(Ident.create("a"))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local a in
                $select_apply(Cell, ['new'], 5, a)
                local $v0 in
                    $get(a, $v0)
                    $add($v0, 3, x)
                end
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.I32_8, e.varAtName("x").valueOrVarSet());
    }

}
