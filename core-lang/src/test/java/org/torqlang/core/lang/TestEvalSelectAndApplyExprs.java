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

public class TestEvalSelectAndApplyExprs {

    @Test
    public void testSingleSelectAndApply() throws Exception {
        String source = """
            begin
                var add_2 = func (n) in n + 2 end
                var bundle = {'add_2': add_2}
                x = bundle.add_2(3 + 4)
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local add_2, bundle in
                $create_proc(proc (n, $r) in
                    $add(n, 2, $r)
                end, add_2)
                $create_rec({'add_2': add_2}, bundle)
                local $v0 in
                    $add(3, 4, $v0)
                    $select_apply(bundle, ['add_2'], $v0, x)
                end
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.of(9), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testDoubleSelectAndApply() throws Exception {
        String source = """
            begin
                var add_2 = func (n) in n + 2 end
                var root = {'bundle': {'add_2': add_2}}
                x = root.bundle.add_2(3 + 4)
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local add_2, root in
                $create_proc(proc (n, $r) in
                    $add(n, 2, $r)
                end, add_2)
                local $v0 in
                    $create_rec({'add_2': add_2}, $v0)
                    $create_rec({'bundle': $v0}, root)
                end
                local $v1 in
                    $add(3, 4, $v1)
                    $select_apply(root, ['bundle', 'add_2'], $v1, x)
                end
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.of(9), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testDoubleSelectWithExprAndApply() throws Exception {
        String source = """
            begin
                var add_2 = func (n) in n + 2 end
                var root = {'bundle': {5: add_2}}
                x = root.bundle[3 + 2](3 + 4)
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local add_2, root in
                $create_proc(proc (n, $r) in
                    $add(n, 2, $r)
                end, add_2)
                local $v0 in
                    $create_rec({5: add_2}, $v0)
                    $create_rec({'bundle': $v0}, root)
                end
                local $v1, $v2 in
                    $add(3, 2, $v1)
                    $add(3, 4, $v2)
                    $select_apply(root, ['bundle', $v1], $v2, x)
                end
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.of(9), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testApplyWithSelectAndApplyArg() throws Exception {
        String source = """
            begin
                var add_2 = func (n) in n + 2 end
                var add_3 = func (n) in n + 3 end
                var bundle = {'add_3': add_3}
                x = add_2(bundle.add_3(5))
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local add_2, add_3, bundle in
                $create_proc(proc (n, $r) in
                    $add(n, 2, $r)
                end, add_2)
                $create_proc(proc (n, $r) in
                    $add(n, 3, $r)
                end, add_3)
                $create_rec({'add_3': add_3}, bundle)
                local $v0 in
                    $select_apply(bundle, ['add_3'], 5, $v0)
                    add_2($v0, x)
                end
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.of(10), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testApplyWithSelectAndApplyArg2() throws Exception {
        String source = """
            begin
                var r = SomeObj.method(some_proc(SomeActor.cfg(1, 'two')))
            end""";
        EvaluatorGenerated g = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource(source)
            .generate();
        assertEquals(source, g.sntcOrExpr().toString());
        String expected = """
            local r in
                local $v0 in
                    local $v1 in
                        $select_apply(SomeActor, ['cfg'], 1, 'two', $v1)
                        some_proc($v1, $v0)
                    end
                    $select_apply(SomeObj, ['method'], $v0, r)
                end
            end""";
        assertEquals(expected, g.kernel().toString());
    }

}
