/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.examples;

import org.torqlang.core.klvm.*;
import org.torqlang.core.local.Actor;
import org.torqlang.core.local.ActorRef;
import org.torqlang.core.local.Envelope;
import org.torqlang.core.local.StreamClient;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public final class IntPublisher extends AbstractExample {

    public static final String SOURCE = """
        actor IntPublisher(first, last, incr) in
            import system[ArrayList, Cell]
            import system.Procs.respond
            var next_int = Cell.new(first)
            handle ask 'request'#{'count': n} in
                func calculate_to() in
                    var to = @next_int + (n - 1) * incr
                    if to < last then to else last end
                end
                var response = ArrayList.new()
                var to = calculate_to()
                while @next_int <= to do
                    response.add(@next_int)
                    next_int := @next_int + incr
                end
                respond(response.to_tuple())
                if @next_int <= last then
                    eof#{'more': true}
                else
                    eof#{'more': false}
                end
            end
        end""";

    public static void main(String[] args) throws Exception {
        new IntPublisher().performWithErrorCheck();
        System.exit(0);
    }

    @Override
    public final void perform() throws Exception {

        ActorRef actorRef = Actor.builder()
            .setArgs(List.of(Int32.of(10), Int32.of(20), Int32.I32_1))
            .spawn(SOURCE)
            .actorRef();

        CompleteRec m = Rec.completeRecBuilder()
            .setLabel(Str.of("request"))
            .addField(Str.of("count"), Int32.of(2)).build();

        Queue<Envelope> response = StreamClient.builder()
            .sendAndAwaitEof(actorRef, m, 100, TimeUnit.MILLISECONDS);

        if (response.size() != 2) {
            throw new IllegalStateException("Mailbox size is not 2");
        }

        // Validate we have 2 envelopes in our mailbox:
        //   1:  [10, 11]
        //   2:  eof#['more': true]

        Envelope next = response.remove();
        if (!(next.message() instanceof CompleteRec ints)) {
            throw new IllegalStateException("First envelope is not a CompleteRec");
        }
        if (ints.fieldCount() != 2) {
            throw new IllegalStateException("Integer count is not 2");
        }
        if (!(ints.valueAt(0).equals(Int32.of(10)))) {
            throw new IllegalStateException("First integer is not 10");
        }
        if (!(ints.valueAt(1).equals(Int32.of(11)))) {
            throw new IllegalStateException("Second integer is not 11");
        }

        next = response.remove();
        if (!(next.message() instanceof CompleteRec eof)) {
            throw new IllegalStateException("Second envelope is not a CompleteRec");
        }
        if (eof.fieldCount() != 1) {
            throw new IllegalStateException("Second envelope field count is not 1");
        }
        if (eof.findValue(Str.of("more")).equals(Bool.FALSE)) {
            throw new IllegalStateException("More is false");
        }
    }

}
