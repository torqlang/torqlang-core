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

public final class NestedMathActs {

    public static final String SOURCE = """
        actor NestedMathActs() in
            ask 'calculate' in
                var a, b, c, d
                a = act b + c + act d + 11 end end
                c = act b + d end
                d = act 5 end
                b = 7
                a
            end
        end""";

    public static void main(String[] args) throws Exception {
        perform();
        System.exit(0);
    }

    public static void perform() throws Exception {
        ActorRef actorRef = actorBuilder()
            .setAddress(createAddress(NestedMathActs.class.getName()))
            .setSource(SOURCE)
            .spawn();
        Object expected = Int32.of(35);
        Object response = RequestClient.builder()
            .setAddress(createAddress("NestedMathClient"))
            .send(actorRef, Str.of("calculate"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        if (!response.equals(expected)) {
            throw new IllegalStateException("Request failed: " + response);
        }
    }

}
