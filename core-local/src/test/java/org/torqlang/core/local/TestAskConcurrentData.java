/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.junit.Test;
import org.torqlang.core.actor.ActorRef;
import org.torqlang.core.klvm.CompleteRec;
import org.torqlang.core.klvm.CompleteTuple;
import org.torqlang.core.klvm.Rec;
import org.torqlang.core.klvm.Str;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.torqlang.core.local.ActorSystem.createAddress;

public class TestAskConcurrentData {

    @Test
    public void test01() throws Exception {
        String source = """
            actor ConcurrentData() in
                handle ask 'perform' in
                    {'customer': act 'Alice and Bob' end, 'order': act '20 pounds of Sugar' end}
                end
            end""";
        ActorBuilderGenerated g = Actor.builder()
            .setAddress(createAddress(getClass().getName() + "Actor"))
            .setSource(source)
            .generate();
        String expected = """
            local $actor_cfgtr in
                $create_actor_cfgtr(proc ($r) in // free vars: $act, $respond
                    local $v0, $v5 in
                        $create_proc(proc ($m) in // free vars: $act, $respond
                            local $else in
                                $create_proc(proc () in // free vars: $m
                                    local $v1 in
                                        $create_rec('error'#{'name': 'org.torqlang.core.lang.AskNotHandledError', 'message': $m}, $v1)
                                        throw $v1
                                    end
                                end, $else)
                                case $m of 'perform' then
                                    local $v2 in
                                        local $v3, $v4 in
                                            $act
                                                $bind('Alice and Bob', $v3)
                                            end
                                            $act
                                                $bind('20 pounds of Sugar', $v4)
                                            end
                                            $create_rec({'customer': $v3, 'order': $v4}, $v2)
                                        end
                                        $respond($v2)
                                    end
                                else
                                    $else()
                                end
                            end
                        end, $v0)
                        $create_proc(proc ($m) in
                            local $v6 in
                                $create_rec('error'#{'name': 'org.torqlang.core.lang.TellNotHandledError', 'message': $m}, $v6)
                                throw $v6
                            end
                        end, $v5)
                        $create_tuple('handlers'#[$v0, $v5], $r)
                    end
                end, $actor_cfgtr)
                $create_rec('ConcurrentData'#{'cfg': $actor_cfgtr}, ConcurrentData)
            end""";
        assertEquals(expected, g.createActorRecStmt().toString());
        ActorRef actorRef = g.spawn().actorRef();
        Object response = RequestClient.builder()
            .setAddress(createAddress("ConcurrentDataClient"))
            .send(actorRef, Str.of("perform"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        CompleteRec expectedRec = Rec.completeRecBuilder()
            .addField(Str.of("customer"), Str.of("Alice and Bob"))
            .addField(Str.of("order"), Str.of("20 pounds of Sugar"))
            .build();
        assertEquals(expectedRec, response);
    }

    @Test
    public void test02() throws Exception {
        String source = """
            actor ConcurrentData() in
                import system.ArrayList
                handle ask 'perform' in
                    var list = ArrayList.new()
                    list.add({'customer': act 'Alice and Bob' end, 'order': act '20 pounds of Sugar' end})
                    list.add({'customer': act 'Charles and Debbie' end, 'order': act '50 pounds of Flour' end})
                    list.to_tuple()
                end
            end""";
        ActorBuilderGenerated g = Actor.builder()
            .setAddress(createAddress(getClass().getName() + "Actor"))
            .setSource(source)
            .generate();
        String expected = """
            local $actor_cfgtr in
                $create_actor_cfgtr(proc ($r) in // free vars: $act, $import, $respond
                    local ArrayList, $v0, $v9 in
                        $import('system', ['ArrayList'])
                        $create_proc(proc ($m) in // free vars: $act, $respond, ArrayList
                            local $else in
                                $create_proc(proc () in // free vars: $m
                                    local $v1 in
                                        $create_rec('error'#{'name': 'org.torqlang.core.lang.AskNotHandledError', 'message': $m}, $v1)
                                        throw $v1
                                    end
                                end, $else)
                                case $m of 'perform' then
                                    local $v2, list in
                                        $select_apply(ArrayList, ['new'], list)
                                        local $v3 in
                                            local $v4, $v5 in
                                                $act
                                                    $bind('Alice and Bob', $v4)
                                                end
                                                $act
                                                    $bind('20 pounds of Sugar', $v5)
                                                end
                                                $create_rec({'customer': $v4, 'order': $v5}, $v3)
                                            end
                                            $select_apply(list, ['add'], $v3)
                                        end
                                        local $v6 in
                                            local $v7, $v8 in
                                                $act
                                                    $bind('Charles and Debbie', $v7)
                                                end
                                                $act
                                                    $bind('50 pounds of Flour', $v8)
                                                end
                                                $create_rec({'customer': $v7, 'order': $v8}, $v6)
                                            end
                                            $select_apply(list, ['add'], $v6)
                                        end
                                        $select_apply(list, ['to_tuple'], $v2)
                                        $respond($v2)
                                    end
                                else
                                    $else()
                                end
                            end
                        end, $v0)
                        $create_proc(proc ($m) in
                            local $v10 in
                                $create_rec('error'#{'name': 'org.torqlang.core.lang.TellNotHandledError', 'message': $m}, $v10)
                                throw $v10
                            end
                        end, $v9)
                        $create_tuple('handlers'#[$v0, $v9], $r)
                    end
                end, $actor_cfgtr)
                $create_rec('ConcurrentData'#{'cfg': $actor_cfgtr}, ConcurrentData)
            end""";
        assertEquals(expected, g.createActorRecStmt().toString());
        ActorRef actorRef = g.spawn().actorRef();
        Object response = RequestClient.builder()
            .setAddress(createAddress("ConcurrentDataClient"))
            .send(actorRef, Str.of("perform"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        CompleteTuple expectedTuple = Rec.completeTupleBuilder()
            .addValue(
                Rec.completeRecBuilder()
                    .addField(Str.of("customer"), Str.of("Alice and Bob"))
                    .addField(Str.of("order"), Str.of("20 pounds of Sugar"))
                    .build()
            )
            .addValue(
                Rec.completeRecBuilder()
                    .addField(Str.of("customer"), Str.of("Charles and Debbie"))
                    .addField(Str.of("order"), Str.of("50 pounds of Flour"))
                    .build()
            )
            .build();
        assertEquals(expectedTuple, response);
    }

}
