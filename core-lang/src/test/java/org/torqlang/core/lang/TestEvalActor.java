/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;
import org.torqlang.core.klvm.ActorCfg;
import org.torqlang.core.klvm.Ident;
import org.torqlang.core.klvm.Str;
import org.torqlang.core.klvm.Var;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestEvalActor {

    @Test
    public void test() throws Exception {
        String source = """
            begin
                actor HelloFactorial() in
                    func fact(x) in
                        func fact_cps(n, k) in
                            if n < 2m then
                                k
                            else
                                fact_cps(n - 1m, n * k)
                            end
                        end
                        fact_cps(x, 1m)
                    end
                    handle ask {'hello': num} in
                        'Hello, ' + num + '! is ' + fact(num)
                    end
                end
                hello_factorial_cfg = HelloFactorial.cfg()
            end""";
        Ident configCtorIdent = Ident.create("hello_factorial_cfg");
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.$RESPOND, new Var(Str.of("RESPOND_PROC_GOES_HERE")))
            .addVar(configCtorIdent)
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local HelloFactorial, $actor_cfgtr in
                $create_actor_cfgtr(proc ($r) in // free vars: $respond
                    local fact, $v3, $v9 in
                        $create_proc(proc (x, $r) in
                            local fact_cps in
                                $create_proc(proc (n, k, $r) in // free vars: fact_cps
                                    local $v0 in
                                        $lt(n, 2m, $v0)
                                        if $v0 then
                                            $bind(k, $r)
                                        else
                                            local $v1, $v2 in
                                                $sub(n, 1m, $v1)
                                                $mult(n, k, $v2)
                                                fact_cps($v1, $v2, $r)
                                            end
                                        end
                                    end
                                end, fact_cps)
                                fact_cps(x, 1m, $r)
                            end
                        end, fact)
                        $create_proc(proc ($m) in // free vars: $respond, fact
                            local $else in
                                $create_proc(proc () in // free vars: $m
                                    local $v4 in
                                        $create_rec('error'#{'name': 'org.torqlang.core.lang.AskNotHandledError', 'message': $m}, $v4)
                                        throw $v4
                                    end
                                end, $else)
                                case $m of {'hello': num} then
                                    local $v5 in
                                        local $v6, $v8 in
                                            local $v7 in
                                                $add('Hello, ', num, $v7)
                                                $add($v7, '! is ', $v6)
                                            end
                                            fact(num, $v8)
                                            $add($v6, $v8, $v5)
                                        end
                                        $respond($v5)
                                    end
                                else
                                    $else()
                                end
                            end
                        end, $v3)
                        $create_proc(proc ($m) in
                            local $v10 in
                                $create_rec('error'#{'name': 'org.torqlang.core.lang.TellNotHandledError', 'message': $m}, $v10)
                                throw $v10
                            end
                        end, $v9)
                        $create_tuple('handlers'#[$v3, $v9], $r)
                    end
                end, $actor_cfgtr)
                $create_rec('HelloFactorial'#{'cfg': $actor_cfgtr}, HelloFactorial)
                $select_apply(HelloFactorial, ['cfg'], hello_factorial_cfg)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertTrue(e.varAtName(configCtorIdent.name).valueOrVarSet() instanceof ActorCfg);
    }

}
