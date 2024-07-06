/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.klvm.Complete;
import org.torqlang.core.klvm.Null;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.torqlang.core.local.Envelope.createControlRequest;
import static org.torqlang.core.local.Envelope.createRequest;

public final class RequestClient implements RequestClientInit, RequestClientResponse {

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

    private void checkRequestClientActor() {
        if (requestClientActor != null) {
            throw new IllegalStateException("Request already sent");
        }
        if (address == null) {
            address = Address.UNDEFINED;
        }
        futureResponse = new CompletableFuture<>();
        requestClientActor = new RequestClientActor();
    }

    @Override
    public final CompletableFuture<Envelope> futureResponse() {
        return futureResponse;
    }

    @Override
    public final RequestClientResponse send(ActorRef actorRef, Complete message) {
        checkRequestClientActor();
        send(actorRef, createRequest(message, requestClientActor, Null.SINGLETON));
        return this;
    }

    @Override
    public final RequestClientResponse send(ActorRef actorRef, CaptureImage captureImage) {
        checkRequestClientActor();
        send(actorRef, createControlRequest(captureImage, requestClientActor, Null.SINGLETON));
        return this;
    }

    private void send(ActorRef actorRef, Envelope envelope) {
        if (!envelope.isRequest()) {
            throw new IllegalStateException("Envelope is not a request");
        }
        actorRef.send(envelope);
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
