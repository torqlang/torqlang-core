/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;
import org.torqlang.core.klvm.*;

import static org.junit.Assert.*;

public class TestEvalTryCatchFinally {

    @Test
    public void testCaughtDivideByZero() throws Exception {
        String source = """
            begin
                try
                    x = 1 / 0
                catch 'error'#{'name': 'java.lang.ArithmeticException', 'message': m, ...} then
                    a = 'Caught an attempt to divide by zero'
                finally
                    b = 'Finally done'
                end
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("a"))
            .addVar(Ident.create("b"))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local $finally in
                $create_proc(proc () in // free vars: b
                    $bind('Finally done', b)
                end, $finally)
                try
                    $div(1, 0, x)
                catch $v0 in
                    local $else in
                        $create_proc(proc () in // free vars: $finally, $v0
                            $finally()
                            throw $v0
                        end, $else)
                        case $v0 of 'error'#{'name': 'java.lang.ArithmeticException', 'message': m, ...} then
                            $bind('Caught an attempt to divide by zero', a)
                        else
                            $else()
                        end
                    end
                end
                $finally()
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Str.of("Caught an attempt to divide by zero"), e.varAtName("a").valueOrVarSet());
        assertEquals(Str.of("Finally done"), e.varAtName("b").valueOrVarSet());
        assertEquals(VarSet.EMPTY_VAR_SET, e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testUncaughtDivideByZero() throws Exception {
        String source = """
            begin
                try
                    x = 1 / 0
                catch 'do-not-catch'#{'name': 'java.lang.ArithmeticException', 'message': m, ...} then
                    a = 'Caught an attempt to divide by zero'
                finally
                    b = 'Finally done'
                end
            end""";
        EvaluatorGenerated e = Evaluator.builder()
            .addVar(Ident.create("a"))
            .addVar(Ident.create("b"))
            .addVar(Ident.create("x"))
            .setSource(source)
            .generate();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local $finally in
                $create_proc(proc () in // free vars: b
                    $bind('Finally done', b)
                end, $finally)
                try
                    $div(1, 0, x)
                catch $v0 in
                    local $else in
                        $create_proc(proc () in // free vars: $finally, $v0
                            $finally()
                            throw $v0
                        end, $else)
                        case $v0 of 'do-not-catch'#{'name': 'java.lang.ArithmeticException', 'message': m, ...} then
                            $bind('Caught an attempt to divide by zero', a)
                        else
                            $else()
                        end
                    end
                end
                $finally()
            end""";
        assertEquals(expected, e.kernel().toString());
        MachineHaltError exc = assertThrows(MachineHaltError.class, e::perform);
        assertTrue(exc.computeHalt().uncaughtThrow instanceof Rec);
        Rec errorRec = (Rec) exc.computeHalt().uncaughtThrow;
        assertEquals(Str.of("error"), errorRec.label());
        assertEquals(Str.of("java.lang.ArithmeticException"), errorRec.findValue(Str.of("name")));
        assertEquals(Str.of("/ by zero"), errorRec.findValue(Str.of("message")));
    }

}
