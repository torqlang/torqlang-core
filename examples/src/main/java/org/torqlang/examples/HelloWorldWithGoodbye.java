/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.examples;

import org.torqlang.core.actor.ActorRef;
import org.torqlang.core.klvm.Str;
import org.torqlang.core.local.RequestClient;

import java.util.concurrent.TimeUnit;

import static org.torqlang.core.local.ActorSystem.actorBuilder;
import static org.torqlang.core.local.ActorSystem.createAddress;
import static org.torqlang.examples.ExamplesTools.checkExpectedResponse;

public class HelloWorldWithGoodbye {

    public static final String SOURCE = """
        actor HelloWorld() in
            ask 'hello' in
                'Hello, World!'
            end
            ask 'goodbye' in
                'Goodbye, World!'
            end
        end""";

    public static void main(String[] args) throws Exception {
        perform();
        System.exit(0);
    }

    public static void perform() throws Exception {

        ActorRef actorRef = actorBuilder()
            .setAddress(createAddress(HelloWorldWithGoodbye.class.getName()))
            .setSource(SOURCE)
            .spawn();

        Object response = RequestClient.builder()
            .setAddress(createAddress("HelloWorldWithGoodbyeClient"))
            .send(actorRef, Str.of("hello"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);

        checkExpectedResponse(Str.of("Hello, World!"), response);

        response = RequestClient.builder()
            .setAddress(createAddress("HelloWorldWithGoodbyeClient"))
            .send(actorRef, Str.of("goodbye"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);

        checkExpectedResponse(Str.of("Goodbye, World!"), response);
    }

}
