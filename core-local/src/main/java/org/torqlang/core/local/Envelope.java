/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

public interface Envelope {

    static Envelope createControlNotify(Object message) {
        return new LocalEnvelope(true, message, null, null);
    }

    static Envelope createControlRequest(Object message, ActorRef requester, Object requestId) {
        return new LocalEnvelope(true, message, requester, requestId);
    }

    static Envelope createControlResponse(Object message, Object requestId) {
        return new LocalEnvelope(true, message, null, requestId);
    }

    static Envelope createNotify(Object message) {
        return new LocalEnvelope(false, message, null, null);
    }

    static Envelope createRequest(Object message, ActorRef requester, Object requestId) {
        return new LocalEnvelope(false, message, requester, requestId);
    }

    static Envelope createResponse(Object message, Object requestId) {
        return new LocalEnvelope(false, message, null, requestId);
    }

    /**
     * Return true if this envelope contains a message for the actor's controller instead of its behavior.
     */
    boolean isControl();

    /**
     * Return true if this envelope contains only a message (no request ID).
     */
    default boolean isNotify() {
        return requestId() == null;
    }

    /**
     * Return true if this envelope contains a requester.
     */
    default boolean isRequest() {
        return requester() != null;
    }

    /**
     * Return true if this envelope contains a request ID but no requester.
     */
    default boolean isResponse() {
        return requestId() != null && requester() == null;
    }

    /**
     * A message that is meaningful to the actor that receives the envelope.
     */
    Object message();

    /**
     * A unique ID that correlates an asynchronous request with its asynchronous response. A request ID is
     * given on a request and returned on a response.
     */
    Object requestId();

    /**
     * A reference that will become the target of the response. Only request messages specify a requester.
     */
    ActorRef requester();

}
