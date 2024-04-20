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

import java.util.Queue;
import java.util.concurrent.TimeUnit;

public interface StreamClientInit {
    Address address();

    StreamClientResponse send(ActorRef actorRef, Complete message);

    Queue<Envelope> sendAndAwaitEof(ActorRef actorRef, Complete message, long timeout, TimeUnit unit) throws Exception;

    StreamClientInit setAddress(Address address);
}
