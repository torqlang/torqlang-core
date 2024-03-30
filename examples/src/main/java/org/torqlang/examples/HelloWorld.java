/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.examples;

import org.torqlang.core.actor.ActorRef;
import org.torqlang.core.klvm.FailedValue;
import org.torqlang.core.klvm.Str;
import org.torqlang.core.local.RequestClient;

import java.util.concurrent.TimeUnit;

import static org.torqlang.core.local.ActorSystem.actorBuilder;
import static org.torqlang.core.local.ActorSystem.createAddress;

public class HelloWorld {

    public static final String SOURCE = """
        actor HelloWorld() in
            ask 'hello' in
                'Hello, World!'
            end
        end""";

    public static void main(String[] args) throws Exception {
        perform();
        System.exit(0);
    }

    public static void perform() throws Exception {

        // Compile HelloWorld
        ActorRef actorRef = actorBuilder()
            .setAddress(createAddress(HelloWorld.class.getName()))
            .setSource(SOURCE)
            .spawn();

        // Create RequestClient and send 'hello'
        Object response = RequestClient.builder()
            .setAddress(createAddress("HelloWorldClient"))
            .send(actorRef, Str.of("hello"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);

        // Check for valid response
        if (!response.equals(Str.of("Hello, World!"))) {
            String error = "Invalid response: " + response;
            if (response instanceof FailedValue failedValue) {
                error += "\n" + failedValue.toDetailsString();
            }
            throw new IllegalStateException(error);
        }
    }

}
