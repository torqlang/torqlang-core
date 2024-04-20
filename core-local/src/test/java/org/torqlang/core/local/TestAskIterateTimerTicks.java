/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.junit.Test;
import org.torqlang.core.actor.ActorRef;
import org.torqlang.core.klvm.FailedValue;
import org.torqlang.core.klvm.Int32;
import org.torqlang.core.klvm.Str;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.torqlang.core.local.ActorSystem.createAddress;

public class TestAskIterateTimerTicks {

    @Test
    public void test() throws Exception {
        String source = """
            actor IterateTimerTicks() in
                import system[Cell, Iter, Stream, Timer]
                handle ask 'iterate' in
                    var tick_count = Cell.new(0)
                    var timer_stream = Stream.new(spawn(Timer.cfg(1, 'microseconds')),
                        'request'#{'ticks': 5})
                    for tick in Iter.new(timer_stream) do
                        tick_count := @tick_count + 1
                    end
                    @tick_count
                end
            end""";
        ActorBuilderGenerated g = Actor.builder()
            .setAddress(createAddress(getClass().getName() + "Actor"))
            .setSource(source)
            .generate();
        String expected = """
            local $actor_cfgtr in
                $create_actor_cfgtr(proc ($r) in // free vars: $import, $respond, $spawn
                    local Cell, Iter, Stream, Timer, $v0, $v9 in
                        $import('system', ['Cell', 'Iter', 'Stream', 'Timer'])
                        $create_proc(proc ($m) in // free vars: $respond, $spawn, Cell, Iter, Stream, Timer
                            local $else in
                                $create_proc(proc () in // free vars: $m
                                    local $v1 in
                                        $create_rec('error'#{'name': 'org.torqlang.core.lang.AskNotHandledError', 'message': $m}, $v1)
                                        throw $v1
                                    end
                                end, $else)
                                case $m of 'iterate' then
                                    local $v2, tick_count, timer_stream in
                                        $select_apply(Cell, ['new'], 0, tick_count)
                                        local $v3, $v5 in
                                            local $v4 in
                                                $select_apply(Timer, ['cfg'], 1, 'microseconds', $v4)
                                                $spawn($v4, $v3)
                                            end
                                            $bind('request'#{'ticks': 5}, $v5)
                                            $select_apply(Stream, ['new'], $v3, $v5, timer_stream)
                                        end
                                        local $iter, $for in
                                            $select_apply(Iter, ['new'], timer_stream, $iter)
                                            $create_proc(proc () in // free vars: $for, $iter, tick_count
                                                local tick, $v6 in
                                                    $iter(tick)
                                                    $ne(tick, eof, $v6)
                                                    if $v6 then
                                                        local $v7 in
                                                            local $v8 in
                                                                $get(tick_count, $v8)
                                                                $add($v8, 1, $v7)
                                                            end
                                                            $set(tick_count, $v7)
                                                        end
                                                        $for()
                                                    end
                                                end
                                            end, $for)
                                            $for()
                                        end
                                        $get(tick_count, $v2)
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
                $create_rec('IterateTimerTicks'#{'cfg': $actor_cfgtr}, IterateTimerTicks)
            end""";
        assertEquals(expected, g.createActorRecStmt().toString());
        ActorRef actorRef = g.spawn();
        Object response = RequestClient.builder()
            .setAddress(createAddress("IterateTimerTicksClient"))
            .send(actorRef, Str.of("iterate"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        if (response instanceof FailedValue failedValue) {
            System.err.println(failedValue.toDetailsString());
        }
        assertEquals(Int32.of(5), response);
    }

}
