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
import static org.torqlang.core.local.ActorSystem.actorBuilder;
import static org.torqlang.core.local.ActorSystem.createAddress;

public class TestAskConcurrentData {

    @Test
    public void test01() throws Exception {
        String source = """
            actor ConcurrentData() in
                ask 'perform' in
                    {'customer': act 'Alice and Bob' end, 'order': act '20 pounds of Sugar' end}
                end
            end""";
        ActorBuilderGenerated g = actorBuilder()
            .setAddress(createAddress(getClass().getName() + "Actor"))
            .setSource(source)
            .generate();
        String expected = """
            local $actor_cfg_ctor in
                $create_actor_cfg_ctor(proc ($r) in // free vars: $act, $respond
                    $create_proc(proc ($m) in // free vars: $act, $respond
                        local $else in
                            $create_proc(proc () in // free vars: $m
                                local $v0 in
                                    $create_rec('error'#{'name': 'org.torqlang.core.lang.NotHandledError', 'message': $m}, $v0)
                                    throw $v0
                                end
                            end, $else)
                            case $m of 'perform' then
                                local $v1 in
                                    local $v2, $v3 in
                                        $act
                                            $bind('Alice and Bob', $v2)
                                        end
                                        $act
                                            $bind('20 pounds of Sugar', $v3)
                                        end
                                        $create_rec({'customer': $v2, 'order': $v3}, $v1)
                                    end
                                    $respond($v1)
                                end
                            else
                                $else()
                            end
                        end
                    end, $r)
                end, $actor_cfg_ctor)
                $create_rec('ConcurrentData'#{'cfg': $actor_cfg_ctor}, ConcurrentData)
            end""";
        assertEquals(expected, g.createActorRecStmt().toString());
        ActorRef actorRef = g.spawn();
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
                ask 'perform' in
                    var list = ArrayList.new()
                    list.add({'customer': act 'Alice and Bob' end, 'order': act '20 pounds of Sugar' end})
                    list.add({'customer': act 'Charles and Debbie' end, 'order': act '50 pounds of Flour' end})
                    list.to_tuple()
                end
            end""";
        ActorBuilderGenerated g = actorBuilder()
            .setAddress(createAddress(getClass().getName() + "Actor"))
            .setSource(source)
            .generate();
        String expected = """
            local $actor_cfg_ctor in
                $create_actor_cfg_ctor(proc ($r) in // free vars: $act, $import, $respond
                    local ArrayList in
                        $import('system', ['ArrayList'])
                        $create_proc(proc ($m) in // free vars: $act, $respond, ArrayList
                            local $else in
                                $create_proc(proc () in // free vars: $m
                                    local $v0 in
                                        $create_rec('error'#{'name': 'org.torqlang.core.lang.NotHandledError', 'message': $m}, $v0)
                                        throw $v0
                                    end
                                end, $else)
                                case $m of 'perform' then
                                    local $v1, list in
                                        $select_apply(ArrayList, ['new'], list)
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
                                            $select_apply(list, ['add'], $v2)
                                        end
                                        local $v5 in
                                            local $v6, $v7 in
                                                $act
                                                    $bind('Charles and Debbie', $v6)
                                                end
                                                $act
                                                    $bind('50 pounds of Flour', $v7)
                                                end
                                                $create_rec({'customer': $v6, 'order': $v7}, $v5)
                                            end
                                            $select_apply(list, ['add'], $v5)
                                        end
                                        $select_apply(list, ['to_tuple'], $v1)
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
