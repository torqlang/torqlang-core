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

public class TestAskSumArrayListWithImportAlias {

    @Test
    public void test() throws Exception {
        String source = """
            actor SumArrayList() in
                import system[ArrayList as JavaArrayList, Cell, ValueIter]
                var one_thru_five = JavaArrayList.new([1, 2, 3, 4, 5])
                handle ask 'perform' in
                    var sum = Cell.new(0)
                    for i in ValueIter.new(one_thru_five) do
                        sum := @sum + i
                    end
                    @sum
                end
            end""";
        ActorBuilderGenerated g = Actor.builder()
            .setAddress(Address.create(getClass().getName() + "Actor"))
            .setSource(source)
            .generate();
        String expected = """
            local $actor_cfgtr in
                $create_actor_cfgtr(proc ($r) in // free vars: $import, $respond
                    local JavaArrayList, Cell, ValueIter, one_thru_five, $v1, $v8 in
                        $import('system', [['ArrayList', 'JavaArrayList'], 'Cell', 'ValueIter'])
                        local $v0 in
                            $bind([1, 2, 3, 4, 5], $v0)
                            $select_apply(JavaArrayList, ['new'], $v0, one_thru_five)
                        end
                        $create_proc(proc ($m) in // free vars: $respond, Cell, ValueIter, one_thru_five
                            local $else in
                                $create_proc(proc () in // free vars: $m
                                    local $v2 in
                                        local $v3 in
                                            $create_rec({'request': $m}, $v3)
                                            $create_rec('error'#{'name': 'org.torqlang.core.lang.AskNotHandledError', 'message': 'Actor could not match request message with an \\'ask\\' handler.', 'details': $v3}, $v2)
                                        end
                                        throw $v2
                                    end
                                end, $else)
                                case $m of 'perform' then
                                    local $v4, sum in
                                        $select_apply(Cell, ['new'], 0, sum)
                                        local $iter, $for in
                                            $select_apply(ValueIter, ['new'], one_thru_five, $iter)
                                            $create_proc(proc () in // free vars: $for, $iter, sum
                                                local i, $v5 in
                                                    $iter(i)
                                                    $ne(i, eof, $v5)
                                                    if $v5 then
                                                        local $v6 in
                                                            local $v7 in
                                                                $get(sum, $v7)
                                                                $add($v7, i, $v6)
                                                            end
                                                            $set(sum, $v6)
                                                        end
                                                        $for()
                                                    end
                                                end
                                            end, $for)
                                            $for()
                                        end
                                        $get(sum, $v4)
                                        $respond($v4)
                                    end
                                else
                                    $else()
                                end
                            end
                        end, $v1)
                        $create_proc(proc ($m) in
                            local $v9 in
                                local $v10 in
                                    $create_rec({'notify': $m}, $v10)
                                    $create_rec('error'#{'name': 'org.torqlang.core.lang.TellNotHandledError', 'message': 'Actor could not match notify message with a \\'tell\\' handler.', 'details': $v10}, $v9)
                                end
                                throw $v9
                            end
                        end, $v8)
                        $create_tuple('handlers'#[$v1, $v8], $r)
                    end
                end, $actor_cfgtr)
                $create_rec('SumArrayList'#{'cfg': $actor_cfgtr}, SumArrayList)
            end""";
        assertEquals(expected, g.createActorRecStmt().toString());
        ActorRef actorRef = g.spawn().actorRef();
        Object response = RequestClient.builder()
            .setAddress(Address.create("SumArrayListClient"))
            .send(actorRef, Str.of("perform"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        assertEquals(Int32.of(15), response);
    }

}
