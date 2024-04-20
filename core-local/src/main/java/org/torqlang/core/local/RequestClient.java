/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.actor.ActorRef;
import org.torqlang.core.actor.Address;
import org.torqlang.core.actor.Envelope;
import org.torqlang.core.klvm.Complete;
import org.torqlang.core.klvm.Nothing;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.torqlang.core.local.ActorSystem.createRequest;

public class RequestClient implements RequestClientInit, RequestClientResponse {
    private static final AtomicInteger nextClientId = new AtomicInteger(0);

    private Address address;
    private RequestClientActor requestClientActor;
    private CompletableFuture<Envelope> futureResponse;

    public static RequestClientInit builder() {
        return new RequestClient();
    }

    @Override
    public final Address address() {
        return address;
    }

    @Override
    public final Object awaitResponse(long timeout, TimeUnit unit) throws Exception {
        return futureResponse.get(timeout, unit).message();
    }

    @Override
    public final CompletableFuture<Envelope> futureResponse() {
        return futureResponse;
    }

    @Override
    public final RequestClientResponse send(ActorRef actorRef, Complete message) {
        if (requestClientActor != null) {
            throw new IllegalStateException("Request already sent");
        }
        if (address == null) {
            address = ActorSystem.createAddress("anonymous-request-client-" + nextClientId.getAndIncrement());
        }
        futureResponse = new CompletableFuture<>();
        requestClientActor = new RequestClientActor();
        actorRef.send(createRequest(message, requestClientActor, Nothing.SINGLETON));
        return this;
    }

    @Override
    public final Object sendAndAwaitResponse(ActorRef actorRef, Complete message, long timeout, TimeUnit unit)
        throws Exception
    {
        send(actorRef, message);
        return awaitResponse(timeout, unit);
    }

    @Override
    public final RequestClientInit setAddress(Address address) {
        this.address = address;
        return this;
    }

    private class RequestClientActor implements ActorRef {
        public RequestClientActor() {
        }

        @Override
        public final Address address() {
            return address;
        }

        @Override
        public final void send(Envelope envelope) {
            futureResponse.completeAsync(() -> envelope);
        }
    }

}
