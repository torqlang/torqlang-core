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
import static org.torqlang.core.local.ActorSystem.actorBuilder;
import static org.torqlang.core.local.ActorSystem.createAddress;

public class TestAskIterateTimerTicks {

    @Test
    public void test() throws Exception {
        String source = """
            actor IterateTimerTicks() in
                import system[Cell, Iter, Stream, Timer]
                ask 'iterate' in
                    var tick_count = Cell.new(0)
                    var timer_stream = Stream.new(spawn(Timer.cfg(1, 'microseconds')),
                        'request'#{'ticks': 5})
                    for tick in Iter.new(timer_stream) do
                        tick_count := @tick_count + 1
                    end
                    @tick_count
                end
            end""";
        ActorBuilderGenerated g = actorBuilder()
            .setAddress(createAddress(getClass().getName() + "Actor"))
            .setSource(source)
            .generate();
        String expected = """
            local $actor_cfg_ctor in
                $create_actor_cfg_ctor(proc ($r) in // free vars: $import, $respond, $spawn
                    local Cell, Iter, Stream, Timer in
                        $import('system', ['Cell', 'Iter', 'Stream', 'Timer'])
                        $create_proc(proc ($m) in // free vars: $respond, $spawn, Cell, Iter, Stream, Timer
                            local $else in
                                $create_proc(proc () in // free vars: $m
                                    local $v0 in
                                        $create_rec('error'#{'name': 'org.torqlang.core.lang.NotHandledError', 'message': $m}, $v0)
                                        throw $v0
                                    end
                                end, $else)
                                case $m of 'iterate' then
                                    local $v1, tick_count, timer_stream in
                                        $select_apply(Cell, ['new'], 0, tick_count)
                                        local $v2, $v4 in
                                            local $v3 in
                                                $select_apply(Timer, ['cfg'], 1, 'microseconds', $v3)
                                                $spawn($v3, $v2)
                                            end
                                            $bind('request'#{'ticks': 5}, $v4)
                                            $select_apply(Stream, ['new'], $v2, $v4, timer_stream)
                                        end
                                        local $iter, $for in
                                            $select_apply(Iter, ['new'], timer_stream, $iter)
                                            $create_proc(proc () in // free vars: $for, $iter, tick_count
                                                local tick, $v5 in
                                                    $iter(tick)
                                                    $ne(tick, eof, $v5)
                                                    if $v5 then
                                                        local $v6 in
                                                            local $v7 in
                                                                $get(tick_count, $v7)
                                                                $add($v7, 1, $v6)
                                                            end
                                                            $set(tick_count, $v6)
                                                        end
                                                        $for()
                                                    end
                                                end
                                            end, $for)
                                            $for()
                                        end
                                        $get(tick_count, $v1)
                                        $respond($v1)
                                    end
                                else
                                    $else()
                                end
                            end
                        end, $r)
                    end
                end, $actor_cfg_ctor)
                $create_rec('IterateTimerTicks'#{'cfg': $actor_cfg_ctor}, IterateTimerTicks)
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
