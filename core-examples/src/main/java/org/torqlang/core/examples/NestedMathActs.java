/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.examples;

import org.torqlang.core.klvm.Int32;
import org.torqlang.core.klvm.Str;
import org.torqlang.core.local.Actor;
import org.torqlang.core.local.ActorRef;
import org.torqlang.core.local.RequestClient;

import java.util.concurrent.TimeUnit;

import static org.torqlang.core.local.Address.createAddress;

public final class NestedMathActs extends AbstractExample {

    public static final String SOURCE = """
        actor NestedMathActs() in
            handle ask 'calculate' in
                var a, b, c, d
                a = act b + c + act d + 11 end end
                c = act b + d end
                d = act 5 end
                b = 7
                a
            end
        end""";

    public static void main(String[] args) throws Exception {
        new NestedMathActs().performWithErrorCheck();
        System.exit(0);
    }

    @Override
    public final void perform() throws Exception {

        ActorRef actorRef = Actor.builder()
            .setAddress(createAddress(NestedMathActs.class.getName()))
            .setSource(SOURCE)
            .spawn()
            .actorRef();

        Object response = RequestClient.builder()
            .setAddress(createAddress("NestedMathClient"))
            .send(actorRef, Str.of("calculate"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);

        checkExpectedResponse(Int32.of(35), response);
    }

}
