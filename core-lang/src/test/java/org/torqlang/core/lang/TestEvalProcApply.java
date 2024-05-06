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

public class TestEvalProcApply {

    @Test
    public void testFuncExprApply() throws Exception {
        String source = """
            begin
                var add_2 = func (n) in
                    n + 2
                end
                x = add_2(3)
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        String expected = """
            begin
                var add_2 = func (n) in n + 2 end
                x = add_2(3)
            end""";
        assertEquals(expected, e.sntcOrExpr().toString());
        expected = """
            local add_2 in
                $create_proc(proc (n, $r) in
                    $add(n, 2, $r)
                end, add_2)
                add_2(3, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.of(5), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testFuncSntcApply() throws Exception {
        String source = """
            begin
                func add_2(x) in
                    x + 2
                end
                x = add_2(3)
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local add_2 in
                $create_proc(proc (x, $r) in
                    $add(x, 2, $r)
                end, add_2)
                add_2(3, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.of(5), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testFuncSntcApplyReturnLiteral() throws Exception {
        String source = """
            begin
                func do_it() in
                    if true then
                        1
                    else
                        2
                    end
                end
                do_it()
            end""";
        Ident x = Ident.create("x");
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(x)
            .setExprIdent(x)
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local do_it in
                $create_proc(proc ($r) in
                    if true then
                        $bind(1, $r)
                    else
                        $bind(2, $r)
                    end
                end, do_it)
                do_it(x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.I32_1, e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testProcExprApply() throws Exception {
        String source = """
            begin
                var add_2 = proc (n, r) in
                    r = n + 2
                end
                add_2(3, x)
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        String expected = """
            begin
                var add_2 = proc (n, r) in r = n + 2 end
                add_2(3, x)
            end""";
        assertEquals(expected, e.sntcOrExpr().toString());
        expected = """
            local add_2 in
                $create_proc(proc (n, r) in
                    $add(n, 2, r)
                end, add_2)
                add_2(3, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.of(5), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testProcSntcApply() throws Exception {
        String source = """
            begin
                proc add_2(n, r) in
                    r = n + 2
                end
                add_2(3, x)
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local add_2 in
                $create_proc(proc (n, r) in
                    $add(n, 2, r)
                end, add_2)
                add_2(3, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.of(5), e.varAtName("x").valueOrVarSet());
    }

}
