/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.examples;

import org.torqlang.core.klvm.CompleteRec;
import org.torqlang.core.klvm.Int32;
import org.torqlang.core.klvm.Rec;
import org.torqlang.core.klvm.Str;
import org.torqlang.core.local.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class AsyncLoadStrings extends AbstractExample {

    public static final String SOURCE = """
        actor LoadStrings() in
            import system.Rec
            import examples.Procs.load_strings
            var strings = load_strings()
            handle ask 'details' in
                {
                    'count': Rec.size(strings),
                    'first': strings[0],
                    'last': strings[Rec.size(strings) - 1]
                }
            end
        end""";

    public static List<?> loadStrings() {
        return List.of("one", "two", "three", "four", "five", "six");
    }

    public static void main(String[] args) throws Exception {
        new AsyncLoadStrings().performWithErrorCheck();
        System.exit(0);
    }

    @Override
    public final void perform() throws Exception {

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup.findStatic(AsyncLoadStrings.class, "loadStrings", MethodType.methodType(List.class));

        CompleteRec moduleRec = Rec.completeRecBuilder()
            .addField(Str.of("load_strings"), new AsyncMethod(methodHandle))
            .build();

        ActorSystem system = ActorSystem.builder()
            .addDefaultModules()
            .addModule("examples.Procs", moduleRec)
            .build();

        ActorRef actorRef = Actor.builder()
            .setSystem(system)
            .spawn(SOURCE).actorRef();

        Object response = RequestClient.builder()
            .sendAndAwaitResponse(actorRef, Str.of("details"), 100, TimeUnit.MILLISECONDS);

        CompleteRec expectedResponse = Rec.completeRecBuilder()
            .addField(Str.of("count"), Int32.of(6))
            .addField(Str.of("first"), Str.of("one"))
            .addField(Str.of("last"), Str.of("six"))
            .build();
        checkExpectedResponse(expectedResponse, response);
    }

}
