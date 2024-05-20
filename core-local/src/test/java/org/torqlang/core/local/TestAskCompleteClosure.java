/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.junit.jupiter.api.Test;
import org.torqlang.core.klvm.*;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class TestAskCompleteClosure {

    @Test
    public void test01() throws Exception {
        String source = """
            actor ConcurrentData() in
                func echo(m) in
                    m
                end
                handle ask 'perform' in
                    [
                        act echo(1) end,
                        act echo(2) end,
                        act echo(3) end
                    ]
                end
            end""";
        ActorBuilderGenerated g = Actor.builder()
            .setAddress(Address.create(getClass().getName() + "Actor"))
            .setSource(source)
            .generate();
        String expected = """
            local $actor_cfgtr in
                $create_actor_cfgtr(proc ($r) in // free vars: $act, $respond
                    local echo, $v0, $v7 in
                        $create_proc(proc (m, $r) in
                            $bind(m, $r)
                        end, echo)
                        $create_proc(proc ($m) in // free vars: $act, $respond, echo
                            local $else in
                                $create_proc(proc () in // free vars: $m
                                    local $v1 in
                                        local $v2 in
                                            $create_rec({'request': $m}, $v2)
                                            $create_rec('error'#{'name': 'org.torqlang.core.lang.AskNotHandledError', 'message': 'Actor could not match request message with an \\'ask\\' handler.', 'details': $v2}, $v1)
                                        end
                                        throw $v1
                                    end
                                end, $else)
                                case $m of 'perform' then
                                    local $v3 in
                                        local $v4, $v5, $v6 in
                                            $act
                                                echo(1, $v4)
                                            end
                                            $act
                                                echo(2, $v5)
                                            end
                                            $act
                                                echo(3, $v6)
                                            end
                                            $create_tuple([$v4, $v5, $v6], $v3)
                                        end
                                        $respond($v3)
                                    end
                                else
                                    $else()
                                end
                            end
                        end, $v0)
                        $create_proc(proc ($m) in
                            local $v8 in
                                local $v9 in
                                    $create_rec({'notify': $m}, $v9)
                                    $create_rec('error'#{'name': 'org.torqlang.core.lang.TellNotHandledError', 'message': 'Actor could not match notify message with a \\'tell\\' handler.', 'details': $v9}, $v8)
                                end
                                throw $v8
                            end
                        end, $v7)
                        $create_tuple('handlers'#[$v0, $v7], $r)
                    end
                end, $actor_cfgtr)
                $create_rec('ConcurrentData'#{'cfg': $actor_cfgtr}, ConcurrentData)
            end""";
        assertEquals(expected, g.createActorRecStmt().toString());
        ActorRef actorRef = g.spawn().actorRef();
        Object response = RequestClient.builder()
            .setAddress(Address.create("CompleteClosureClient"))
            .send(actorRef, Str.of("perform"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        CompleteTuple expectedTuple = Rec.completeTupleBuilder()
            .addValue(Int32.I32_1)
            .addValue(Int32.I32_2)
            .addValue(Int32.I32_3)
            .build();
        assertEquals(expectedTuple, response);
    }

    /*
     * This test expects a failure because you cannot share a Cell across actor boundaries.
     */
    @Test
    public void test02() throws Exception {
        String source = """
            actor ConcurrentData() in
                import system.Cell
                var next_value = Cell.new(0)
                func next() in
                    var answer = @next_value
                    next_value := @next_value + 1
                    answer
                end
                handle ask 'perform' in
                    [
                        act next() end,
                        act next() end,
                        act next() end
                    ]
                end
            end""";
        ActorBuilderGenerated g = Actor.builder()
            .setAddress(Address.create(getClass().getName() + "Actor"))
            .setSource(source)
            .generate();
        String expected = """
            local $actor_cfgtr in
                $create_actor_cfgtr(proc ($r) in // free vars: $act, $import, $respond
                    local Cell, next_value, next, $v2, $v9 in
                        $import('system', ['Cell'])
                        $select_apply(Cell, ['new'], 0, next_value)
                        $create_proc(proc ($r) in // free vars: next_value
                            local answer in
                                $get(next_value, answer)
                                local $v0 in
                                    local $v1 in
                                        $get(next_value, $v1)
                                        $add($v1, 1, $v0)
                                    end
                                    $set(next_value, $v0)
                                end
                                $bind(answer, $r)
                            end
                        end, next)
                        $create_proc(proc ($m) in // free vars: $act, $respond, next
                            local $else in
                                $create_proc(proc () in // free vars: $m
                                    local $v3 in
                                        local $v4 in
                                            $create_rec({'request': $m}, $v4)
                                            $create_rec('error'#{'name': 'org.torqlang.core.lang.AskNotHandledError', 'message': 'Actor could not match request message with an \\'ask\\' handler.', 'details': $v4}, $v3)
                                        end
                                        throw $v3
                                    end
                                end, $else)
                                case $m of 'perform' then
                                    local $v5 in
                                        local $v6, $v7, $v8 in
                                            $act
                                                next($v6)
                                            end
                                            $act
                                                next($v7)
                                            end
                                            $act
                                                next($v8)
                                            end
                                            $create_tuple([$v6, $v7, $v8], $v5)
                                        end
                                        $respond($v5)
                                    end
                                else
                                    $else()
                                end
                            end
                        end, $v2)
                        $create_proc(proc ($m) in
                            local $v10 in
                                local $v11 in
                                    $create_rec({'notify': $m}, $v11)
                                    $create_rec('error'#{'name': 'org.torqlang.core.lang.TellNotHandledError', 'message': 'Actor could not match notify message with a \\'tell\\' handler.', 'details': $v11}, $v10)
                                end
                                throw $v10
                            end
                        end, $v9)
                        $create_tuple('handlers'#[$v2, $v9], $r)
                    end
                end, $actor_cfgtr)
                $create_rec('ConcurrentData'#{'cfg': $actor_cfgtr}, ConcurrentData)
            end""";
        assertEquals(expected, g.createActorRecStmt().toString());
        ActorRef actorRef = g.spawn().actorRef();
        Object response = RequestClient.builder()
            .setAddress(Address.create("CompleteClosureClient"))
            .send(actorRef, Str.of("perform"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        assertInstanceOf(FailedValue.class, response);
        FailedValue failedValue = (FailedValue) response;
        assertInstanceOf(CannotCompleteError.class, failedValue.nativeCause());
    }

}
