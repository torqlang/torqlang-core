/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.examples;

import org.torqlang.core.actor.ActorRef;
import org.torqlang.core.klvm.CompleteRec;
import org.torqlang.core.klvm.Int32;
import org.torqlang.core.klvm.Rec;
import org.torqlang.core.klvm.Str;
import org.torqlang.core.local.Actor;
import org.torqlang.core.local.ModuleSystem;
import org.torqlang.core.local.RequestClient;

import java.util.concurrent.TimeUnit;

import static org.torqlang.core.local.ActorSystem.createAddress;

public final class SumOddIntsStream {

    public static final String SOURCE = """
        actor SumOddIntsStream() in
            import system[Cell, Iter, Stream]
            import examples.IntPublisher
            handle ask 'sum'#{'first': first, 'last': last} in
                var sum = Cell.new(0)
                var int_publisher = spawn(IntPublisher.cfg(first, last, 1))
                var int_stream = Stream.new(int_publisher, 'request'#{'count': 3})
                for i in Iter.new(int_stream) do
                    if i % 2 != 0 then sum := @sum + i end
                end
                @sum
            end
        end""";

    public static void main(String[] args) throws Exception {
        perform();
        System.exit(0);
    }

    public static void perform() throws Exception {

        ModuleSystem.register("examples", ExamplesMod::moduleRec);

        ActorRef actorRef = Actor.builder()
            .setAddress(createAddress(SumOddIntsStream.class.getName()))
            .setSource(SOURCE)
            .spawn();

        // 1 + 3 + 5 + 7 + 9 = 25
        Object expected = Int32.of(25);
        CompleteRec message = Rec.completeRecBuilder()
            .setLabel(Str.of("sum"))
            .addField(Str.of("first"), Int32.I32_1)
            .addField(Str.of("last"), Int32.of(10))
            .build();
        Object response = RequestClient.builder()
            .setAddress(createAddress("SumOddIntsClient"))
            .send(actorRef, message)
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        if (!response.equals(expected)) {
            throw new IllegalStateException("Request failed: " + response);
        }

        // 1 + 3 + 5 + 7 + 9 + 11 = 36
        expected = Int32.of(36);
        message = Rec.completeRecBuilder()
            .setLabel(Str.of("sum"))
            .addField(Str.of("first"), Int32.I32_1)
            .addField(Str.of("last"), Int32.of(11))
            .build();
        response = RequestClient.builder()
            .setAddress(createAddress("SumOddIntsClient"))
            .send(actorRef, message)
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        if (!response.equals(expected)) {
            throw new IllegalStateException("Request failed: " + response);
        }
    }

}
