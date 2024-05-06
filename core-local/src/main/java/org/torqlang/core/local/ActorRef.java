/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

/**
 * An opaque reference to an actor that is used to send messages. An actor reference decouples
 * the sender and receiver from the underlying communications that send and receive messages.
 */
public interface ActorRef {

    Address address();

    void send(Envelope envelope);

}
