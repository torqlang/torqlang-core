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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestEvalApplyExprs {

    @Test
    public void testApplyFuncExprWithMathArg() throws Exception {
        String source = """
            begin
                var add_2 = func (n) in n + 2 end
                x = add_2(add_2(add_2(1 + 2)))
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local add_2 in
                $create_proc(proc (n, $r) in
                    $add(n, 2, $r)
                end, add_2)
                local $v0 in
                    local $v1 in
                        local $v2 in
                            $add(1, 2, $v2)
                            add_2($v2, $v1)
                        end
                        add_2($v1, $v0)
                    end
                    add_2($v0, x)
                end
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.of(9), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testApplyMathArg() throws Exception {
        String source = """
            begin
                var add_2 = func (n) in n + 2 end
                x = add_2(3 + 4)
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local add_2 in
                $create_proc(proc (n, $r) in
                    $add(n, 2, $r)
                end, add_2)
                local $v0 in
                    $add(3, 4, $v0)
                    add_2($v0, x)
                end
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.of(9), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testApplyNestedFuncExprs() throws Exception {
        String source = """
            begin
                var add_2 = func (n) in n + 2 end
                x = add_2(add_2(add_2(3)))
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local add_2 in
                $create_proc(proc (n, $r) in
                    $add(n, 2, $r)
                end, add_2)
                local $v0 in
                    local $v1 in
                        add_2(3, $v1)
                        add_2($v1, $v0)
                    end
                    add_2($v0, x)
                end
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.of(9), e.varAtName("x").valueOrVarSet());
    }

}
