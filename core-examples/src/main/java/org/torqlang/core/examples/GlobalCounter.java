/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.examples;

import org.torqlang.core.klvm.Int32;
import org.torqlang.core.klvm.Str;
import org.torqlang.core.local.*;

import java.util.concurrent.TimeUnit;

public final class GlobalCounter extends AbstractExample {

    public static final String GLOBAL_COUNTER = """
        actor Counter() in
            import system.Cell
            var c = Cell.new(0)
            handle ask 'get' in
                @c
            end
            handle tell 'incr' in
                c := @c + 1
            end
        end""";

    public static final String TEST_CLIENT = """
        actor Client() in
            import system.Procs.actor_at
            var global_counter = actor_at('examples.Counter.global')
            handle ask 'get' in
                global_counter.ask('get')
            end
            handle tell 'incr' in
                global_counter.tell('incr')
            end
        end""";

    public static void main(String[] args) throws Exception {
        new GlobalCounter().performWithErrorCheck();
        System.exit(0);
    }

    @Override
    public void perform() throws Exception {

        ActorRef globalCounterRef = Actor.builder().spawn(GLOBAL_COUNTER).actorRef();

        ActorSystem system = ActorSystem.builder()
            .addDefaultModules()
            .addActor("examples.Counter.global", globalCounterRef)
            .build();

        ActorRef testClientRef = Actor.builder()
            .setSystem(system)
            .spawn(TEST_CLIENT)
            .actorRef();
        testClientRef.send(Envelope.createNotify(Str.of("incr")));
        testClientRef.send(Envelope.createNotify(Str.of("incr")));
        Object response = RequestClient.builder()
            .sendAndAwaitResponse(testClientRef, Str.of("get"), 100, TimeUnit.MILLISECONDS);

        checkExpectedResponse(Int32.of(2), response);
    }

}
