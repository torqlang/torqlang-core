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

public class TestEvalCases {

    @Test
    public void test01() throws Exception {
        String source = """
            x = case a
                of 'customer'#{'name': {'first': first, 'last': last}} then
                    last
                of 'supplier'#{'company': {'name': name, 'address': _}} then
                    name
                else
                    'not found'
            end""";
        Rec arg = Rec.completeRecBuilder()
            .setLabel(Str.of("customer"))
            .addField(Str.of("name"),
                Rec.completeRecBuilder()
                    .addField(Str.of("first"), Str.of("Abraham"))
                    .addField(Str.of("last"), Str.of("Lincoln"))
                    .build()
            )
            .build();
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("a"), new Var(arg))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local $else in
                $create_proc(proc ($r) in // free vars: a
                    local $else in
                        $create_proc(proc ($r) in
                            $bind('not found', $r)
                        end, $else)
                        case a of 'supplier'#{'company': $v0} then
                            case $v0 of {'name': name, 'address': $_0} then
                                $bind(name, $r)
                            else
                                $else($r)
                            end
                        else
                            $else($r)
                        end
                    end
                end, $else)
                case a of 'customer'#{'name': $v1} then
                    case $v1 of {'first': first, 'last': last} then
                        $bind(last, x)
                    else
                        $else(x)
                    end
                else
                    $else(x)
                end
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Str.of("Lincoln"), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void test02() throws Exception {
        String source = """
            x = case a
                of 'customer'#{'name': {'first': first, 'last': last}} then
                    last
                of 'supplier'#{'company': {'name': name, 'address': _}} then
                    name
            end""";
        Complete arg = Rec.completeRecBuilder()
            .setLabel(Str.of("customer"))
            .addField(Str.of("name"),
                Rec.completeRecBuilder()
                    .addField(Str.of("first"), Str.of("Abraham"))
                    .addField(Str.of("last"), Str.of("Lincoln"))
                    .build()
            )
            .build();
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("a"), new Var(arg))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local $else in
                $create_proc(proc ($r) in // free vars: a
                    case a of 'supplier'#{'company': $v0} then
                        case $v0 of {'name': name, 'address': $_0} then
                            $bind(name, $r)
                        end
                    end
                end, $else)
                case a of 'customer'#{'name': $v1} then
                    case $v1 of {'first': first, 'last': last} then
                        $bind(last, x)
                    else
                        $else(x)
                    end
                else
                    $else(x)
                end
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Str.of("Lincoln"), e.varAtName("x").valueOrVarSet());

        // TEST NO MATCH -- RESULT WILL BE UNBOUND

        arg = Str.of("this will not match");
        e = Evaluator.builder()
            .addVar(Ident.create("a"), new Var(arg))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(VarSet.EMPTY_VAR_SET, e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void test03() throws Exception {
        String source = """
            begin
                func check_customer(c) in
                    case c
                        of 'customer'#{'name': {'first': first, 'last': last}} then
                            return last
                        of 'supplier'#{'company': {'name': name, 'address': _}} then
                            return name
                    end
                    return 'not found'
                end
                x = check_customer(a)
            end""";
        Complete arg = Rec.completeRecBuilder()
            .setLabel(Str.of("customer"))
            .addField(Str.of("name"), Rec.completeRecBuilder()
                .addField(Str.of("first"), Str.of("Abraham"))
                .addField(Str.of("last"), Str.of("Lincoln"))
                .build()
            )
            .build();
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("a"), new Var(arg))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local check_customer in
                $create_proc(proc (c, $r) in
                    local $else in
                        $create_proc(proc () in // free vars: $r, c
                            case c of 'supplier'#{'company': $v0} then
                                case $v0 of {'name': name, 'address': $_0} then
                                    $bind(name, $r)
                                    $jump_throw(3)
                                end
                            end
                        end, $else)
                        case c of 'customer'#{'name': $v1} then
                            case $v1 of {'first': first, 'last': last} then
                                $bind(last, $r)
                                $jump_throw(3)
                            else
                                $else()
                            end
                        else
                            $else()
                        end
                    end
                    $bind('not found', $r)
                    $jump_throw(3)
                    $jump_catch(3)
                end, check_customer)
                check_customer(a, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Str.of("Lincoln"), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void test04() throws Exception {
        String source = """
            begin
                func check_customer(c) in
                    while true do
                        case c
                            of 'customer'#{'name': {'first': first, 'last': last}} then
                                return last
                            of 'supplier'#{'company': {'name': name, 'address': _}} then
                                return name
                        end
                        return 'not found'
                    end
                end
                x = check_customer(a)
            end""";
        Complete arg = Rec.completeRecBuilder()
            .setLabel(Str.of("customer"))
            .addField(Str.of("name"),
                Rec.completeRecBuilder()
                    .addField(Str.of("first"), Str.of("Abraham"))
                    .addField(Str.of("last"), Str.of("Lincoln"))
                    .build()
            )
            .build();
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("a"), new Var(arg))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local check_customer in
                $create_proc(proc (c, $r) in
                    local $guard, $while in
                        $create_proc(proc ($r) in
                            $bind(true, $r)
                        end, $guard)
                        $create_proc(proc () in // free vars: $guard, $r, $while, c
                            local $v0 in
                                $guard($v0)
                                if $v0 then
                                    local $else in
                                        $create_proc(proc () in // free vars: $r, c
                                            case c of 'supplier'#{'company': $v1} then
                                                case $v1 of {'name': name, 'address': $_0} then
                                                    $bind(name, $r)
                                                    $jump_throw(3)
                                                end
                                            end
                                        end, $else)
                                        case c of 'customer'#{'name': $v2} then
                                            case $v2 of {'first': first, 'last': last} then
                                                $bind(last, $r)
                                                $jump_throw(3)
                                            else
                                                $else()
                                            end
                                        else
                                            $else()
                                        end
                                    end
                                    $bind('not found', $r)
                                    $jump_throw(3)
                                    $while()
                                end
                            end
                        end, $while)
                        $while()
                    end
                    $jump_catch(3)
                end, check_customer)
                check_customer(a, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Str.of("Lincoln"), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void test05() throws Exception {
        String source = """
            x = case a
                of 'number'#{'holder': {'value': n}} when n <= 10 then
                    'n is less than or equal to 10'
                of 'number'#{'holder': {'value': n}} when n > 10 then
                    'n is greater than 10'
                else
                    'not found'
            end""";

        // n = 11

        Complete arg = Rec.completeRecBuilder()
            .setLabel(Str.of("number"))
            .addField(Str.of("holder"), Rec.completeRecBuilder()
                .addField(Str.of("value"), Int32.of(11))
                .build()
            )
            .build();
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("a"), new Var(arg))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local $else in
                $create_proc(proc ($r) in // free vars: a
                    local $else in
                        $create_proc(proc ($r) in
                            $bind('not found', $r)
                        end, $else)
                        case a of 'number'#{'holder': $v0} then
                            case $v0 of {'value': n} then
                                local $v1 in
                                    $gt(n, 10, $v1)
                                    if $v1 then
                                        $bind('n is greater than 10', $r)
                                    else
                                        $else($r)
                                    end
                                end
                            else
                                $else($r)
                            end
                        else
                            $else($r)
                        end
                    end
                end, $else)
                case a of 'number'#{'holder': $v2} then
                    case $v2 of {'value': n} then
                        local $v3 in
                            $le(n, 10, $v3)
                            if $v3 then
                                $bind('n is less than or equal to 10', x)
                            else
                                $else(x)
                            end
                        end
                    else
                        $else(x)
                    end
                else
                    $else(x)
                end
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Str.of("n is greater than 10"), e.varAtName("x").valueOrVarSet());

        // n = 5

        arg = Rec.completeRecBuilder()
            .setLabel(Str.of("number"))
            .addField(Str.of("holder"), Rec.completeRecBuilder()
                .addField(Str.of("value"), Int32.of(5))
                .build()
            )
            .build();
        e = Evaluator.builder()
            .addVar(Ident.create("a"), new Var(arg))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(Str.of("n is less than or equal to 10"), e.varAtName("x").valueOrVarSet());
    }

}
