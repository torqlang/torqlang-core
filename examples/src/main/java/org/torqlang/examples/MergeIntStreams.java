/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.examples;

import org.torqlang.core.actor.ActorRef;
import org.torqlang.core.klvm.CompleteTuple;
import org.torqlang.core.klvm.Int32;
import org.torqlang.core.klvm.Rec;
import org.torqlang.core.klvm.Str;
import org.torqlang.core.local.ModuleSystem;
import org.torqlang.core.local.RequestClient;

import java.util.concurrent.TimeUnit;

import static org.torqlang.core.local.ActorSystem.actorBuilder;
import static org.torqlang.core.local.ActorSystem.createAddress;

public class MergeIntStreams {

    public static final String SOURCE = """
        actor MergeIntStreams() in
            import system[ArrayList, Cell, Iter, Stream]
            import examples.IntPublisher
            ask 'merge' in
                var odd_iter = Iter.new(Stream.new(spawn(IntPublisher.cfg(1, 10, 2)), 'request'#{'count': 3})),
                    even_iter = Iter.new(Stream.new(spawn(IntPublisher.cfg(2, 10, 2)), 'request'#{'count': 2}))
                var answer = ArrayList.new()
                var odd_next = Cell.new(odd_iter()),
                    even_next = Cell.new(even_iter())
                while @odd_next != eof && @even_next != eof do
                    if (@odd_next < @even_next) then
                        answer.add(@odd_next)
                        odd_next := odd_iter()
                    else
                        answer.add(@even_next)
                        even_next := even_iter()
                    end
                end
                while @odd_next != eof do
                    answer.add(@odd_next)
                    odd_next := odd_iter()
                end
                while @even_next != eof do
                    answer.add(@even_next)
                    even_next := even_iter()
                end
                answer.to_tuple()
            end
        end""";

    public static void main(String[] args) throws Exception {
        perform();
        System.exit(0);
    }

    public static void perform() throws Exception {
        ModuleSystem.register("examples", ExamplesMod::moduleRec);
        ActorRef actorRef = actorBuilder()
            .setAddress(createAddress(MergeIntStreams.class.getName()))
            .setSource(SOURCE)
            .spawn();
        Object response = RequestClient.builder()
            .setAddress(createAddress("MergeIntStreamsClient"))
            .send(actorRef, Str.of("merge"))
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        CompleteTuple expectedTuple = Rec.completeTupleBuilder()
            .addValue(Int32.of(1))
            .addValue(Int32.of(2))
            .addValue(Int32.of(3))
            .addValue(Int32.of(4))
            .addValue(Int32.of(5))
            .addValue(Int32.of(6))
            .addValue(Int32.of(7))
            .addValue(Int32.of(8))
            .addValue(Int32.of(9))
            .addValue(Int32.of(10))
            .build();
        if (!response.equals(expectedTuple)) {
            throw new IllegalStateException("Request failed: " + response);
        }
    }

}
