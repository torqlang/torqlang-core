/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.examples;

import org.torqlang.core.actor.ActorRef;
import org.torqlang.core.klvm.Int32;
import org.torqlang.core.klvm.Str;
import org.torqlang.core.local.Actor;
import org.torqlang.core.local.RequestClient;

import java.util.concurrent.TimeUnit;

public final class ConcurrentMathWithIncr extends AbstractExample {

    public static final String SOURCE = """
        actor ConcurrentMath() in
            import system.Cell
            actor Number(n) in
                var value = Cell.new(n)
                handle ask 'get' in
                    @value
                end
                handle tell 'incr' in
                    value := @value + 1
                end
            end
            var n1 = spawn(Number.cfg(0)),
                n2 = spawn(Number.cfg(0)),
                n3 = spawn(Number.cfg(0))
            handle ask 'calculate' in
                n1.tell('incr')
                n2.tell('incr'); n2.tell('incr')
                n3.tell('incr'); n3.tell('incr'); n3.tell('incr')
                n1.ask('get') + n2.ask('get') * n3.ask('get')
            end
        end""";

    public static void main(String[] args) throws Exception {
        new ConcurrentMathWithIncr().performWithErrorCheck();
        System.exit(0);
    }

    @Override
    public final void perform() throws Exception {

        ActorRef actorRef = Actor.builder().spawn(SOURCE).actorRef();

        // 1 + 2 * 3
        Object response = RequestClient.builder()
            .sendAndAwaitResponse(actorRef, Str.of("calculate"), 100, TimeUnit.MILLISECONDS);
        checkExpectedResponse(Int32.of(7), response);

        // 2 + 4 * 6
        response = RequestClient.builder()
            .sendAndAwaitResponse(actorRef, Str.of("calculate"), 100, TimeUnit.MILLISECONDS);
        checkExpectedResponse(Int32.of(26), response);

        // 3 + 6 * 9
        response = RequestClient.builder()
            .sendAndAwaitResponse(actorRef, Str.of("calculate"), 100, TimeUnit.MILLISECONDS);
        checkExpectedResponse(Int32.of(57), response);
    }

}
