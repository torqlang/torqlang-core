/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.junit.jupiter.api.Test;
import org.torqlang.core.klvm.*;
import org.torqlang.core.lang.ActorSntc;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

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
                handle ask {'hello': num} in
                    'Hello, ' + num + '! is ' + fact(num)
                end
            end""";
        ActorBuilderGenerated g = Actor.builder()
            .setAddress(Address.create(getClass().getName() + "Actor"))
            .setSource(source)
            .generate();
        String expected = """
            local $actor_cfgtr in
                $create_actor_cfgtr(proc ($r) in // free vars: $respond
                    local fact, $v3, $v10 in
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
                                    local $v4 in
                                        local $v5 in
                                            $create_rec({'request': $m}, $v5)
                                            $create_rec('error'#{'name': 'org.torqlang.core.lang.AskNotHandledError', 'message': 'Actor could not match request message with an \\'ask\\' handler.', 'details': $v5}, $v4)
                                        end
                                        throw $v4
                                    end
                                end, $else)
                                case $m of {'hello': num} then
                                    local $v6 in
                                        local $v7, $v9 in
                                            local $v8 in
                                                $add('Hello, ', num, $v8)
                                                $add($v8, '! is ', $v7)
                                            end
                                            fact(num, $v9)
                                            $add($v7, $v9, $v6)
                                        end
                                        $respond($v6)
                                    end
                                else
                                    $else()
                                end
                            end
                        end, $v3)
                        $create_proc(proc ($m) in
                            local $v11 in
                                local $v12 in
                                    $create_rec({'notify': $m}, $v12)
                                    $create_rec('error'#{'name': 'org.torqlang.core.lang.TellNotHandledError', 'message': 'Actor could not match notify message with a \\'tell\\' handler.', 'details': $v12}, $v11)
                                end
                                throw $v11
                            end
                        end, $v10)
                        $create_tuple('handlers'#[$v3, $v10], $r)
                    end
                end, $actor_cfgtr)
                $create_rec('HelloFactorial'#{'cfg': $actor_cfgtr}, HelloFactorial)
            end""";
        assertEquals(expected, g.createActorRecStmt().toString());
        ActorRef actorRef = g.spawn().actorRef();
        CompleteRec m = Rec.completeRecBuilder().addField(Str.of("hello"), Dec128.of(10)).build();
        Object response = RequestClient.builder()
            .setAddress(Address.create("HelloFactorialClient"))
            .send(actorRef, m)
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        assertEquals(Str.of("Hello, 10! is 3628800"), response);
    }

    @Test
    public void testHelloWorld() throws Exception {
        String source = """
            actor HelloWorld() in
                handle ask 'hello' in
                    'Hello, World!'
                end
            end""";
        ActorBuilderGenerated g = Actor.builder()
            .setAddress(Address.create(getClass().getName() + "Actor"))
            .setSource(source)
            .generate();
        String expected = """
            local $actor_cfgtr in
                $create_actor_cfgtr(proc ($r) in // free vars: $respond
                    local $v0, $v4 in
                        $create_proc(proc ($m) in // free vars: $respond
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
                                case $m of 'hello' then
                                    local $v3 in
                                        $bind('Hello, World!', $v3)
                                        $respond($v3)
                                    end
                                else
                                    $else()
                                end
                            end
                        end, $v0)
                        $create_proc(proc ($m) in
                            local $v5 in
                                local $v6 in
                                    $create_rec({'notify': $m}, $v6)
                                    $create_rec('error'#{'name': 'org.torqlang.core.lang.TellNotHandledError', 'message': 'Actor could not match notify message with a \\'tell\\' handler.', 'details': $v6}, $v5)
                                end
                                throw $v5
                            end
                        end, $v4)
                        $create_tuple('handlers'#[$v0, $v4], $r)
                    end
                end, $actor_cfgtr)
                $create_rec('HelloWorld'#{'cfg': $actor_cfgtr}, HelloWorld)
            end""";
        assertEquals(expected, g.createActorRecStmt().toString());
        ActorRef actorRef = g.spawn().actorRef();
        Object response = RequestClient.builder()
            .setAddress(Address.create("HelloWorldClient"))
            .send(actorRef, Str.of("hello"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        assertEquals(Str.of("Hello, World!"), response);

        // Say 'hello' a second time
        response = RequestClient.builder()
            .setAddress(Address.create("HelloWorldClient"))
            .send(actorRef, Str.of("hello"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        assertEquals(Str.of("Hello, World!"), response);

        // Say 'goodbye' which will not be handled
        response = RequestClient.builder()
            .setAddress(Address.create("HelloWorldClient"))
            .send(actorRef, Str.of("goodbye"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        assertInstanceOf(FailedValue.class, response);
        FailedValue failedValue = (FailedValue) response;
        assertEquals("FailedValue(error='error'#{'details': {'request': 'goodbye'}, 'message': 'Actor could not match request message with an \\'ask\\' handler.', 'name': 'org.torqlang.core.lang.AskNotHandledError'})", failedValue.toString());
    }

    @Test
    public void testHelloWorldFromActorSntc() throws Exception {
        String source = """
            actor HelloWorld() in
                handle ask 'hello' in
                    'Hello, World!'
                end
            end""";
        // Run a separate builder to get the ActorSntc
        ActorSntc actorSntc = Actor.builder()
            .setAddress(Address.create(getClass().getName() + "Actor"))
            .setSource(source)
            .rewrite()
            .actorSntc();
        // Now, show we can create an actor from just an ActorSntc (no source)
        ActorRef actorRef = Actor.builder()
            .setAddress(Address.create(getClass().getName() + "Actor"))
            .setActorSntc(actorSntc)
            .spawn().actorRef();
        // Send 'hello' and verify 'Hello, World!'
        Object response = RequestClient.builder()
            .setAddress(Address.create("HelloWorldClient"))
            .send(actorRef, Str.of("hello"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        assertEquals(Str.of("Hello, World!"), response);
    }

}
