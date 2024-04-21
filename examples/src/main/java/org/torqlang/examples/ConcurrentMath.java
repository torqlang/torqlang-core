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

import static org.torqlang.examples.ExamplesTools.checkExpectedResponse;

public final class ConcurrentMath {

    public static final String SOURCE = """
        actor ConcurrentMath() in
            actor Number(n) in
                handle ask 'get' in
                    n
                end
            end
            var n1 = spawn(Number.cfg(1)),
                n2 = spawn(Number.cfg(2)),
                n3 = spawn(Number.cfg(3))
            handle ask 'calculate' in
                n1.ask('get') + n2.ask('get') * n3.ask('get')
            end
        end""";

    public static void main(String[] args) throws Exception {
        perform();
        System.exit(0);
    }

    public static void perform() throws Exception {

        ActorRef actorRef = Actor.builder()
            .spawn(SOURCE);

        Object response = RequestClient.builder()
            .sendAndAwaitResponse(actorRef, Str.of("calculate"), 100, TimeUnit.MILLISECONDS);
        checkExpectedResponse(Int32.of(7), response);
    }

}