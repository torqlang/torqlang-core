/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.actor;

public interface Envelope {

    /**
     * Return true if this envelope contains a message for the actor's controller instead of its behavior.
     */
    boolean isControl();

    /**
     * Return true if this envelope contains only a message.
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
     * Return true if this envelope contains a request id but no requester.
     */
    default boolean isResponse() {
        return requestId() != null && requester() == null;
    }

    /**
     * A message that is meaningful to the actor that receives the envelope.
     */
    Object message();

    /**
     * A unique id that correlates an asynchronous request with its asynchronous response. A request id is
     * given on a request and returned on a response.
     */
    Object requestId();

    /**
     * A reference that will become the target of the response. Only request messages specify a requester.
     */
    ActorRef requester();

}
