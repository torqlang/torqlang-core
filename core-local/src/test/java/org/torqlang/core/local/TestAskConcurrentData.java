/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.junit.jupiter.api.Test;
import org.torqlang.core.klvm.CompleteRec;
import org.torqlang.core.klvm.CompleteTuple;
import org.torqlang.core.klvm.Rec;
import org.torqlang.core.klvm.Str;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
            .setAddress(Address.create(getClass().getName() + "Actor"))
            .setSource(source)
            .generate();
        String expected = """
            local $actor_cfgtr in
                $create_actor_cfgtr(proc ($r) in // free vars: $act, $respond
                    local $v0, $v6 in
                        $create_proc(proc ($m) in // free vars: $act, $respond
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
                                        local $v4, $v5 in
                                            $act
                                                $bind('Alice and Bob', $v4)
                                            end
                                            $act
                                                $bind('20 pounds of Sugar', $v5)
                                            end
                                            $create_rec({'customer': $v4, 'order': $v5}, $v3)
                                        end
                                        $respond($v3)
                                    end
                                else
                                    $else()
                                end
                            end
                        end, $v0)
                        $create_proc(proc ($m) in
                            local $v7 in
                                local $v8 in
                                    $create_rec({'notify': $m}, $v8)
                                    $create_rec('error'#{'name': 'org.torqlang.core.lang.TellNotHandledError', 'message': 'Actor could not match notify message with a \\'tell\\' handler.', 'details': $v8}, $v7)
                                end
                                throw $v7
                            end
                        end, $v6)
                        $create_tuple('handlers'#[$v0, $v6], $r)
                    end
                end, $actor_cfgtr)
                $create_rec('ConcurrentData'#{'cfg': $actor_cfgtr}, ConcurrentData)
            end""";
        assertEquals(expected, g.createActorRecStmt().toString());
        ActorRef actorRef = g.spawn().actorRef();
        Object response = RequestClient.builder()
            .setAddress(Address.create("ConcurrentDataClient"))
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
            .setAddress(Address.create(getClass().getName() + "Actor"))
            .setSource(source)
            .generate();
        String expected = """
            local $actor_cfgtr in
                $create_actor_cfgtr(proc ($r) in // free vars: $act, $import, $respond
                    local ArrayList, $v0, $v10 in
                        $import('system', ['ArrayList'])
                        $create_proc(proc ($m) in // free vars: $act, $respond, ArrayList
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
                                    local $v3, list in
                                        $select_apply(ArrayList, ['new'], list)
                                        local $v4 in
                                            local $v5, $v6 in
                                                $act
                                                    $bind('Alice and Bob', $v5)
                                                end
                                                $act
                                                    $bind('20 pounds of Sugar', $v6)
                                                end
                                                $create_rec({'customer': $v5, 'order': $v6}, $v4)
                                            end
                                            $select_apply(list, ['add'], $v4)
                                        end
                                        local $v7 in
                                            local $v8, $v9 in
                                                $act
                                                    $bind('Charles and Debbie', $v8)
                                                end
                                                $act
                                                    $bind('50 pounds of Flour', $v9)
                                                end
                                                $create_rec({'customer': $v8, 'order': $v9}, $v7)
                                            end
                                            $select_apply(list, ['add'], $v7)
                                        end
                                        $select_apply(list, ['to_tuple'], $v3)
                                        $respond($v3)
                                    end
                                else
                                    $else()
                                end
                            end
                        end, $v0)
                        $create_proc(proc ($m) in
                            local $v11 in
                                local $v12 in
                                    $create_rec({'notify': $m}, $v12)
                                    $create_rec('error'#{'name': 'org.torqlang.core.lang.TellNotHandledError', 'message': 'Actor could not match notify message with a \\'tell\\' handler.', 'details': $v12}, $v11)
                                end
                                throw $v11
                            end
                        end, $v10)
                        $create_tuple('handlers'#[$v0, $v10], $r)
                    end
                end, $actor_cfgtr)
                $create_rec('ConcurrentData'#{'cfg': $actor_cfgtr}, ConcurrentData)
            end""";
        assertEquals(expected, g.createActorRecStmt().toString());
        ActorRef actorRef = g.spawn().actorRef();
        Object response = RequestClient.builder()
            .setAddress(Address.create("ConcurrentDataClient"))
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
