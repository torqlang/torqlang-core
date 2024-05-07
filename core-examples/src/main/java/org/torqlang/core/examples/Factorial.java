/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.examples;

import org.torqlang.core.klvm.Int64;
import org.torqlang.core.local.Actor;
import org.torqlang.core.local.ActorRef;
import org.torqlang.core.local.RequestClient;

import java.util.concurrent.TimeUnit;

public final class Factorial extends AbstractExample {

    public static final String SOURCE = """
        actor Factorial() in
            func fact(x) in
                func fact_cps(n, k) in
                    if n < 2 then k
                    else fact_cps(n - 1, n * k) end
                end
                fact_cps(x, 1)
            end
            handle ask x in
                fact(x)
            end
        end""";

    public static void main(String[] args) throws Exception {
        new Factorial().performWithErrorCheck();
        System.exit(0);
    }

    @Override
    public final void perform() throws Exception {

        ActorRef actorRef = Actor.builder().spawn(SOURCE).actorRef();

        Object response = RequestClient.builder()
            .sendAndAwaitResponse(actorRef, Int64.of(10), 100, TimeUnit.MILLISECONDS);

        checkExpectedResponse(Int64.of(3628800), response);
    }

}
