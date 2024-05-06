/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.examples;

import org.torqlang.core.klvm.*;
import org.torqlang.core.lang.ValueTools;
import org.torqlang.core.local.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

public final class MergeIntStreams extends AbstractExample {

    public static final String SOURCE = """
        actor MergeIntStreams() in
            import system[ArrayList, Cell, Stream, ValueIter]
            import examples.IntPublisher
            handle ask 'merge' in
                var odd_iter = ValueIter.new(Stream.new(spawn(IntPublisher.cfg(1, 10, 2)), 'request'#{'count': 3})),
                    even_iter = ValueIter.new(Stream.new(spawn(IntPublisher.cfg(2, 10, 2)), 'request'#{'count': 2}))
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
        new MergeIntStreams().performWithErrorCheck();
        System.exit(0);
    }

    @Override
    public final void perform() throws Exception {

        ActorSystem system = ActorSystem.builder()
            .addDefaultModules()
            .addModule("examples", ExamplesMod.moduleRec())
            .build();

        ActorRef actorRef = Actor.builder()
            .setSystem(system)
            .spawn(SOURCE).actorRef();

        Object response = RequestClient.builder()
            .sendAndAwaitResponse(actorRef, Str.of("merge"), 100, TimeUnit.MILLISECONDS);

        List<?> expectedTuple = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        checkExpectedResponse(expectedTuple, ValueTools.toNativeValue((Complete) response));
    }

}
