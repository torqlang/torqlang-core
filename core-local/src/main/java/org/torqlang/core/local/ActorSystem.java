/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.klvm.CompleteRec;

import java.util.concurrent.Executor;

public interface ActorSystem {

    static ActorSystemBuilder builder() {
        return new ActorSystemBuilder();
    }

    static Executor defaultExecutor() {
        return ActorSystemDefaults.DEFAULT_EXECUTOR;
    }

    static ActorSystem defaultSystem() {
        return ActorSystemDefaults.DEFAULT_SYSTEM;
    }

    ActorRefObj actorAt(Address address);

    Logger createLogger();

    Mailbox createMailbox();

    Executor executor();

    CompleteRec moduleAt(String path);

    String name();
}
