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

public class TestAskConcurrentFeatures {

    @Test
    public void test01() throws Exception {
        String source = """
            actor ConcurrentFeatures() in
                ask 'perform' in
                    var f, v
                    var a = {f: v}
                    a = act {'one': 1} end
                    var b = {f: v}
                    b = act {'one': 1} end
                    f = act 'one' end
                    a.one + b.one
                end
            end""";
        ActorBuilderGenerated g = actorBuilder()
            .setAddress(createAddress(getClass().getName() + "Actor01"))
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
                                local $v1, f, v, a, b in
                                    $create_rec({f: v}, a)
                                    $act
                                        $bind({'one': 1}, a)
                                    end
                                    $create_rec({f: v}, b)
                                    $act
                                        $bind({'one': 1}, b)
                                    end
                                    $act
                                        $bind('one', f)
                                    end
                                    local $v2, $v3 in
                                        $select(a, 'one', $v2)
                                        $select(b, 'one', $v3)
                                        $add($v2, $v3, $v1)
                                    end
                                    $respond($v1)
                                end
                            else
                                $else()
                            end
                        end
                    end, $r)
                end, $actor_cfg_ctor)
                $create_rec('ConcurrentFeatures'#{'cfg': $actor_cfg_ctor}, ConcurrentFeatures)
            end""";
        assertEquals(expected, g.createActorRecStmt().toString());
        ActorRef actorRef = g.spawn();
        Object response = RequestClient.builder()
            .setAddress(createAddress("ConcurrentFeaturesClient"))
            .send(actorRef, Str.of("perform"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        assertEquals(Int32.I32_2, response);
    }

    @Test
    public void test02() throws Exception {
        /*
         * This source tests a previous deadlock condition caused by interdependent responses, which can occur while
         * bindings records with undetermined features. The "spin waits" for responses to arrive in b, a, v order.
         */
        String sourceWithUndeterminedWaits = """
            actor ConcurrentFeatures() in
                import system.RangeIter
                proc spin_wait(n) in
                    for i in RangeIter.new(0, n) do
                        skip
                    end
                end
                ask 'perform' in
                    var a, b, f, v
                    a = {f, v}
                    b = {v, f}
                    b = act {1: 'one'} end
                    a = act spin_wait(20) {'one': 1} end
                    v = act spin_wait(4000) 1 end
                    a.one + 1
                end
            end""";
        String source = """
            actor ConcurrentFeatures() in
                ask 'perform' in
                    var a, b, f, v
                    a = {f, v}
                    b = {v, f}
                    b = act {1: 'one'} end
                    a = act {'one': 1} end
                    v = act 1 end
                    a.one + 1
                end
            end""";
        ActorBuilderGenerated g = actorBuilder()
            .setAddress(createAddress(getClass().getName() + "Actor02"))
            .setSource(source)
            .generate();
        ActorRef actorRef = g.spawn();
        Object response = RequestClient.builder()
            .setAddress(createAddress("ConcurrentFeaturesClient"))
            .send(actorRef, Str.of("perform"))
            .awaitResponse(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        if (response instanceof FailedValue failedValue) {
            System.out.println(failedValue.toDetailsString());
        }
        assertEquals(Int32.I32_2, response);
    }

    @Test
    public void test03() throws Exception {
        String source = """
            actor ConcurrentFeatures() in
                ask 'perform' in
                    var a, b, f, v
                    a = {f, v}
                    b = {v, f}
                    v = act 1 end
                    a = act {'one': 1} end
                    b = act {1: 'one'} end
                    a.one + 1
                end
            end""";
        ActorBuilderGenerated g = actorBuilder()
            .setAddress(createAddress(getClass().getName() + "Actor03"))
            .setSource(source)
            .generate();
        ActorRef actorRef = g.spawn();
        Object response = RequestClient.builder()
            .setAddress(createAddress("ConcurrentFeaturesClient"))
            .send(actorRef, Str.of("perform"))
            .awaitResponse(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        if (response instanceof FailedValue failedValue) {
            System.out.println(failedValue.toDetailsString());
        }
        assertEquals(Int32.I32_2, response);
    }

}
