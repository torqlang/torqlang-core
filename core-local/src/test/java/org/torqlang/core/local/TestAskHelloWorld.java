/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.junit.Test;
import org.torqlang.core.actor.ActorRef;
import org.torqlang.core.klvm.*;
import org.torqlang.core.lang.ActorSntc;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.torqlang.core.local.ActorSystem.actorBuilder;
import static org.torqlang.core.local.ActorSystem.createAddress;

public class TestAskHelloWorld {

    @Test
    public void testHelloFactorial() throws Exception {
        String source = """
            actor HelloFactorial() in
                func fact(x) in
                    func fact_cps(n, k) in
                        if n < 2m then
                            k
                        else
                            fact_cps(n - 1m, n * k)
                        end
                    end
                    fact_cps(x, 1m)
                end
                ask {'hello': num} in
                    'Hello, ' + num + '! is ' + fact(num)
                end
            end""";
        ActorBuilderGenerated g = actorBuilder()
            .setAddress(createAddress(getClass().getName() + "Actor"))
            .setSource(source)
            .generate();
        String expected = """
            local $actor_cfg_ctor in
                $create_actor_cfg_ctor(proc ($r) in // free vars: $respond
                    local fact in
                        $create_proc(proc (x, $r) in
                            local fact_cps in
                                $create_proc(proc (n, k, $r) in // free vars: fact_cps
                                    local $v0 in
                                        $lt(n, 2m, $v0)
                                        if $v0 then
                                            $bind(k, $r)
                                        else
                                            local $v1, $v2 in
                                                $sub(n, 1m, $v1)
                                                $mult(n, k, $v2)
                                                fact_cps($v1, $v2, $r)
                                            end
                                        end
                                    end
                                end, fact_cps)
                                fact_cps(x, 1m, $r)
                            end
                        end, fact)
                        $create_proc(proc ($m) in // free vars: $respond, fact
                            local $else in
                                $create_proc(proc () in // free vars: $m
                                    local $v3 in
                                        $create_rec('error'#{'name': 'org.torqlang.core.lang.NotHandledError', 'message': $m}, $v3)
                                        throw $v3
                                    end
                                end, $else)
                                case $m of {'hello': num} then
                                    local $v4 in
                                        local $v5, $v7 in
                                            local $v6 in
                                                $add('Hello, ', num, $v6)
                                                $add($v6, '! is ', $v5)
                                            end
                                            fact(num, $v7)
                                            $add($v5, $v7, $v4)
                                        end
                                        $respond($v4)
                                    end
                                else
                                    $else()
                                end
                            end
                        end, $r)
                    end
                end, $actor_cfg_ctor)
                $create_rec('HelloFactorial'#{'cfg': $actor_cfg_ctor}, HelloFactorial)
            end""";
        assertEquals(expected, g.createActorRecStmt().toString());
        ActorRef actorRef = g.spawn();
        CompleteRec m = Rec.completeRecBuilder().addField(Str.of("hello"), Dec128.of(10)).build();
        Object response = RequestClient.builder()
            .setAddress(createAddress("HelloFactorialClient"))
            .send(actorRef, m)
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        assertEquals(Str.of("Hello, 10! is 3628800"), response);
    }

    @Test
    public void testHelloWorld() throws Exception {
        String source = """
            actor HelloWorld() in
                ask 'hello' in
                    'Hello, World!'
                end
            end""";
        ActorBuilderGenerated g = actorBuilder()
            .setAddress(createAddress(getClass().getName() + "Actor"))
            .setSource(source)
            .generate();
        String expected = """
            local $actor_cfg_ctor in
                $create_actor_cfg_ctor(proc ($r) in // free vars: $respond
                    $create_proc(proc ($m) in // free vars: $respond
                        local $else in
                            $create_proc(proc () in // free vars: $m
                                local $v0 in
                                    $create_rec('error'#{'name': 'org.torqlang.core.lang.NotHandledError', 'message': $m}, $v0)
                                    throw $v0
                                end
                            end, $else)
                            case $m of 'hello' then
                                local $v1 in
                                    $bind('Hello, World!', $v1)
                                    $respond($v1)
                                end
                            else
                                $else()
                            end
                        end
                    end, $r)
                end, $actor_cfg_ctor)
                $create_rec('HelloWorld'#{'cfg': $actor_cfg_ctor}, HelloWorld)
            end""";
        assertEquals(expected, g.createActorRecStmt().toString());
        ActorRef actorRef = g.spawn();
        Object response = RequestClient.builder()
            .setAddress(createAddress("HelloWorldClient"))
            .send(actorRef, Str.of("hello"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        assertEquals(Str.of("Hello, World!"), response);

        // Say 'hello' a second time
        response = RequestClient.builder()
            .setAddress(createAddress("HelloWorldClient"))
            .send(actorRef, Str.of("hello"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        assertEquals(Str.of("Hello, World!"), response);

        // Say 'goodbye' which will not be handled
        response = RequestClient.builder()
            .setAddress(createAddress("HelloWorldClient"))
            .send(actorRef, Str.of("goodbye"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        assertTrue(response instanceof FailedValue);
        FailedValue failedValue = (FailedValue) response;
        assertEquals("FailedValue(error='error'#{'message': 'goodbye', 'name': 'org.torqlang.core.lang.NotHandledError'})", failedValue.toString());
    }

    @Test
    public void testHelloWorldFromActorSntc() throws Exception {
        String source = """
            actor HelloWorld() in
                ask 'hello' in
                    'Hello, World!'
                end
            end""";
        // Run a separate builder to get the ActorSntc
        ActorSntc actorSntc = actorBuilder()
            .setAddress(createAddress(getClass().getName() + "Actor"))
            .setSource(source)
            .rewrite()
            .actorSntc();
        // Now, show we can create an actor from just an ActorSntc (no source)
        ActorRef actorRef = actorBuilder()
            .setAddress(createAddress(getClass().getName() + "Actor"))
            .setActorSntc(actorSntc)
            .spawn();
        // Send 'hello' and verify 'Hello, World!'
        Object response = RequestClient.builder()
            .setAddress(createAddress("HelloWorldClient"))
            .send(actorRef, Str.of("hello"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        assertEquals(Str.of("Hello, World!"), response);
    }

}
