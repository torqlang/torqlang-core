/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.junit.jupiter.api.Test;
import org.torqlang.core.klvm.FailedValue;
import org.torqlang.core.klvm.Int32;
import org.torqlang.core.klvm.Str;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestAskIterateTimerTicks {

    @Test
    public void test() throws Exception {
        String source = """
            actor IterateTimerTicks() in
                import system[Cell, Stream, Timer, ValueIter]
                handle ask 'iterate' in
                    var tick_count = Cell.new(0)
                    var timer_stream = Stream.new(spawn(Timer.cfg(1, 'microseconds')),
                        'request'#{'ticks': 5})
                    for tick in ValueIter.new(timer_stream) do
                        tick_count := @tick_count + 1
                    end
                    @tick_count
                end
            end""";
        ActorBuilderGenerated g = Actor.builder()
            .setAddress(Address.create(getClass().getName() + "Actor"))
            .setSource(source)
            .generate();
        String expected = """
            local $actor_cfgtr in
                $create_actor_cfgtr(proc ($r) in // free vars: $import, $respond, $spawn
                    local Cell, Stream, Timer, ValueIter, $v0, $v10 in
                        $import('system', ['Cell', 'Stream', 'Timer', 'ValueIter'])
                        $create_proc(proc ($m) in // free vars: $respond, $spawn, Cell, Stream, Timer, ValueIter
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
                                case $m of 'iterate' then
                                    local $v3, tick_count, timer_stream in
                                        $select_apply(Cell, ['new'], 0, tick_count)
                                        local $v4, $v6 in
                                            local $v5 in
                                                $select_apply(Timer, ['cfg'], 1, 'microseconds', $v5)
                                                $spawn($v5, $v4)
                                            end
                                            $bind('request'#{'ticks': 5}, $v6)
                                            $select_apply(Stream, ['new'], $v4, $v6, timer_stream)
                                        end
                                        local $iter, $for in
                                            $select_apply(ValueIter, ['new'], timer_stream, $iter)
                                            $create_proc(proc () in // free vars: $for, $iter, tick_count
                                                local tick, $v7 in
                                                    $iter(tick)
                                                    $ne(tick, eof, $v7)
                                                    if $v7 then
                                                        local $v8 in
                                                            local $v9 in
                                                                $get(tick_count, $v9)
                                                                $add($v9, 1, $v8)
                                                            end
                                                            $set(tick_count, $v8)
                                                        end
                                                        $for()
                                                    end
                                                end
                                            end, $for)
                                            $for()
                                        end
                                        $get(tick_count, $v3)
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
                $create_rec('IterateTimerTicks'#{'cfg': $actor_cfgtr}, IterateTimerTicks)
            end""";
        assertEquals(expected, g.createActorRecStmt().toString());
        ActorRef actorRef = g.spawn().actorRef();
        Object response = RequestClient.builder()
            .setAddress(Address.create("IterateTimerTicksClient"))
            .send(actorRef, Str.of("iterate"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        if (response instanceof FailedValue failedValue) {
            System.err.println(failedValue.toDetailsString());
        }
        assertEquals(Int32.of(5), response);
    }

}
