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
import org.torqlang.core.local.RequestClient;

import java.util.concurrent.TimeUnit;

import static org.torqlang.core.local.ActorSystem.actorBuilder;
import static org.torqlang.core.local.ActorSystem.createAddress;
import static org.torqlang.examples.ExamplesTools.checkExpectedResponse;

public final class SimpleMath {

    public static final String SOURCE = """
        actor SimpleMath() in
            actor Number(n) in
                ask 'get' in n end
            end
            var n1 = spawn(Number.cfg(1)),
                n2 = spawn(Number.cfg(2)),
                n3 = spawn(Number.cfg(3))
            ask 'calculate' in
                n1.ask('get') + n2.ask('get') * n3.ask('get')
            end
        end""";

    public static void main(String[] args) throws Exception {
        perform();
        System.exit(0);
    }

    public static void perform() throws Exception {

        ActorRef actorRef = actorBuilder()
            .setAddress(createAddress(SimpleMath.class.getName()))
            .setSource(SOURCE)
            .spawn();

        Object response = RequestClient.builder()
            .setAddress(createAddress("SimpleMathClient"))
            .send(actorRef, Str.of("calculate"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);

        checkExpectedResponse(Int32.of(7), response);
    }

}
