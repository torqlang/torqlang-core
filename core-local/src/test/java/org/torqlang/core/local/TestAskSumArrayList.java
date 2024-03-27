/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.junit.Test;
import org.torqlang.core.actor.ActorRef;
import org.torqlang.core.klvm.Int32;
import org.torqlang.core.klvm.Str;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.torqlang.core.local.ActorSystem.actorBuilder;
import static org.torqlang.core.local.ActorSystem.createAddress;

public class TestAskSumArrayList {

    @Test
    public void test() throws Exception {
        String source = """
            actor SumArrayList() in
                import system[ArrayList, Cell, Iter]
                var one_thru_five = ArrayList.new([1, 2, 3, 4, 5])
                ask 'perform' in
                    var sum = Cell.new(0)
                    for i in Iter.new(one_thru_five) do
                        sum := @sum + i
                    end
                    @sum
                end
            end""";
        ActorBuilderGenerated g = actorBuilder()
            .setAddress(createAddress(getClass().getName() + "Actor"))
            .setSource(source)
            .generate();
        String expected = """
            local $actor_cfg_ctor in
                $create_actor_cfg_ctor(proc ($r) in // free vars: $import, $respond
                    local ArrayList, Cell, Iter, one_thru_five in
                        $import('system', ['ArrayList', 'Cell', 'Iter'])
                        local $v0 in
                            $bind([1, 2, 3, 4, 5], $v0)
                            $select_apply(ArrayList, ['new'], $v0, one_thru_five)
                        end
                        $create_proc(proc ($m) in // free vars: $respond, Cell, Iter, one_thru_five
                            local $else in
                                $create_proc(proc () in // free vars: $m
                                    local $v1 in
                                        $create_rec('error'#{'name': 'org.torqlang.core.lang.NotHandledError', 'message': $m}, $v1)
                                        throw $v1
                                    end
                                end, $else)
                                case $m of 'perform' then
                                    local $v2, sum in
                                        $select_apply(Cell, ['new'], 0, sum)
                                        local $iter, $for in
                                            $select_apply(Iter, ['new'], one_thru_five, $iter)
                                            $create_proc(proc () in // free vars: $for, $iter, sum
                                                local i, $v3 in
                                                    $iter(i)
                                                    $ne(i, eof, $v3)
                                                    if $v3 then
                                                        local $v4 in
                                                            local $v5 in
                                                                $get(sum, $v5)
                                                                $add($v5, i, $v4)
                                                            end
                                                            $set(sum, $v4)
                                                        end
                                                        $for()
                                                    end
                                                end
                                            end, $for)
                                            $for()
                                        end
                                        $get(sum, $v2)
                                        $respond($v2)
                                    end
                                else
                                    $else()
                                end
                            end
                        end, $r)
                    end
                end, $actor_cfg_ctor)
                $create_rec('SumArrayList'#{'cfg': $actor_cfg_ctor}, SumArrayList)
            end""";
        assertEquals(expected, g.createActorRecStmt().toString());
        ActorRef actorRef = g.spawn();
        Object response = RequestClient.builder()
            .setAddress(createAddress("SumArrayListClient"))
            .send(actorRef, Str.of("perform"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        assertEquals(Int32.of(15), response);
    }

}
