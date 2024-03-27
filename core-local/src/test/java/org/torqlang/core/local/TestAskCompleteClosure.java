/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.junit.Test;
import org.torqlang.core.actor.ActorRef;
import org.torqlang.core.klvm.*;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.torqlang.core.local.ActorSystem.actorBuilder;
import static org.torqlang.core.local.ActorSystem.createAddress;

public class TestAskCompleteClosure {

    @Test
    public void test01() throws Exception {
        String source = """
            actor ConcurrentData() in
                func echo(m) in
                    m
                end
                ask 'perform' in
                    [
                        act echo(1) end,
                        act echo(2) end,
                        act echo(3) end
                    ]
                end
            end""";
        ActorBuilderGenerated g = actorBuilder()
            .setAddress(createAddress(getClass().getName() + "Actor"))
            .setSource(source)
            .generate();
        String expected = """
            local $actor_cfg_ctor in
                $create_actor_cfg_ctor(proc ($r) in // free vars: $act, $respond
                    local echo in
                        $create_proc(proc (m, $r) in
                            $bind(m, $r)
                        end, echo)
                        $create_proc(proc ($m) in // free vars: $act, $respond, echo
                            local $else in
                                $create_proc(proc () in // free vars: $m
                                    local $v0 in
                                        $create_rec('error'#{'name': 'org.torqlang.core.lang.NotHandledError', 'message': $m}, $v0)
                                        throw $v0
                                    end
                                end, $else)
                                case $m of 'perform' then
                                    local $v1 in
                                        local $v2, $v3, $v4 in
                                            $act
                                                echo(1, $v2)
                                            end
                                            $act
                                                echo(2, $v3)
                                            end
                                            $act
                                                echo(3, $v4)
                                            end
                                            $create_tuple([$v2, $v3, $v4], $v1)
                                        end
                                        $respond($v1)
                                    end
                                else
                                    $else()
                                end
                            end
                        end, $r)
                    end
                end, $actor_cfg_ctor)
                $create_rec('ConcurrentData'#{'cfg': $actor_cfg_ctor}, ConcurrentData)
            end""";
        assertEquals(expected, g.createActorRecStmt().toString());
        ActorRef actorRef = g.spawn();
        Object response = RequestClient.builder()
            .setAddress(createAddress("CompleteClosureClient"))
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
                ask 'perform' in
                    [
                        act next() end,
                        act next() end,
                        act next() end
                    ]
                end
            end""";
        ActorBuilderGenerated g = actorBuilder()
            .setAddress(createAddress(getClass().getName() + "Actor"))
            .setSource(source)
            .generate();
        String expected = """
            local $actor_cfg_ctor in
                $create_actor_cfg_ctor(proc ($r) in // free vars: $act, $import, $respond
                    local Cell, next_value, next in
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
                                    local $v2 in
                                        $create_rec('error'#{'name': 'org.torqlang.core.lang.NotHandledError', 'message': $m}, $v2)
                                        throw $v2
                                    end
                                end, $else)
                                case $m of 'perform' then
                                    local $v3 in
                                        local $v4, $v5, $v6 in
                                            $act
                                                next($v4)
                                            end
                                            $act
                                                next($v5)
                                            end
                                            $act
                                                next($v6)
                                            end
                                            $create_tuple([$v4, $v5, $v6], $v3)
                                        end
                                        $respond($v3)
                                    end
                                else
                                    $else()
                                end
                            end
                        end, $r)
                    end
                end, $actor_cfg_ctor)
                $create_rec('ConcurrentData'#{'cfg': $actor_cfg_ctor}, ConcurrentData)
            end""";
        assertEquals(expected, g.createActorRecStmt().toString());
        ActorRef actorRef = g.spawn();
        Object response = RequestClient.builder()
            .setAddress(createAddress("CompleteClosureClient"))
            .send(actorRef, Str.of("perform"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        assertTrue(response instanceof FailedValue);
        FailedValue failedValue = (FailedValue) response;
        assertTrue(failedValue.nativeCause() instanceof CannotCompleteError);
    }

}
