/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.junit.jupiter.api.Test;
import org.torqlang.core.klvm.Int32;
import org.torqlang.core.klvm.Str;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.torqlang.core.local.Address.createAddress;

public class TestAskSumArrayList {

    @Test
    public void test() throws Exception {
        String source = """
            actor SumArrayList() in
                import system[ArrayList, Cell, ValueIter]
                var one_thru_five = ArrayList.new([1, 2, 3, 4, 5])
                handle ask 'perform' in
                    var sum = Cell.new(0)
                    for i in ValueIter.new(one_thru_five) do
                        sum := @sum + i
                    end
                    @sum
                end
            end""";
        ActorBuilderGenerated g = Actor.builder()
            .setAddress(createAddress(getClass().getName() + "Actor"))
            .setSource(source)
            .generate();
        String expected = """
            local $actor_cfgtr in
                $create_actor_cfgtr(proc ($r) in // free vars: $import, $respond
                    local ArrayList, Cell, ValueIter, one_thru_five, $v1, $v7 in
                        $import('system', ['ArrayList', 'Cell', 'ValueIter'])
                        local $v0 in
                            $bind([1, 2, 3, 4, 5], $v0)
                            $select_apply(ArrayList, ['new'], $v0, one_thru_five)
                        end
                        $create_proc(proc ($m) in // free vars: $respond, Cell, ValueIter, one_thru_five
                            local $else in
                                $create_proc(proc () in // free vars: $m
                                    local $v2 in
                                        $create_rec('error'#{'name': 'org.torqlang.core.lang.AskNotHandledError', 'message': $m}, $v2)
                                        throw $v2
                                    end
                                end, $else)
                                case $m of 'perform' then
                                    local $v3, sum in
                                        $select_apply(Cell, ['new'], 0, sum)
                                        local $iter, $for in
                                            $select_apply(ValueIter, ['new'], one_thru_five, $iter)
                                            $create_proc(proc () in // free vars: $for, $iter, sum
                                                local i, $v4 in
                                                    $iter(i)
                                                    $ne(i, eof, $v4)
                                                    if $v4 then
                                                        local $v5 in
                                                            local $v6 in
                                                                $get(sum, $v6)
                                                                $add($v6, i, $v5)
                                                            end
                                                            $set(sum, $v5)
                                                        end
                                                        $for()
                                                    end
                                                end
                                            end, $for)
                                            $for()
                                        end
                                        $get(sum, $v3)
                                        $respond($v3)
                                    end
                                else
                                    $else()
                                end
                            end
                        end, $v1)
                        $create_proc(proc ($m) in
                            local $v8 in
                                $create_rec('error'#{'name': 'org.torqlang.core.lang.TellNotHandledError', 'message': $m}, $v8)
                                throw $v8
                            end
                        end, $v7)
                        $create_tuple('handlers'#[$v1, $v7], $r)
                    end
                end, $actor_cfgtr)
                $create_rec('SumArrayList'#{'cfg': $actor_cfgtr}, SumArrayList)
            end""";
        assertEquals(expected, g.createActorRecStmt().toString());
        ActorRef actorRef = g.spawn().actorRef();
        Object response = RequestClient.builder()
            .setAddress(createAddress("SumArrayListClient"))
            .send(actorRef, Str.of("perform"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        assertEquals(Int32.of(15), response);
    }

}
