/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.klvm.CompleteRec;
import org.torqlang.core.util.BinarySearchTools;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

public class BasicActorSystem implements ActorSystem {

    private final String name;
    private final Executor executor;
    private final ActorEntry[] actors;
    private final ModuleEntry[] modules;

    BasicActorSystem(String name, Executor executor, List<ActorEntry> actors, List<ModuleEntry> modules) {
        this.name = name;
        this.executor = executor != null ?
            executor : ActorSystemDefaults.executor();
        this.actors = actors.toArray(new ActorEntry[0]);
        Arrays.sort(this.actors);
        this.modules = modules.toArray(new ModuleEntry[0]);
        Arrays.sort(this.modules);
    }

    @Override
    public final ActorRefObj actorAt(Address address) {
        int i = BinarySearchTools.search(actors, (a) -> address.compareTo(a.address));
        if (i < 0) {
            throw new ActorNotFoundError(address);
        }
        return actors[i].actorRefObj;
    }

    @Override
    public final Logger createLogger() {
        return Logger.createDefault();
    }

    @Override
    public final Mailbox createMailbox() {
        return Mailbox.createDefault();
    }

    @Override
    public final Executor executor() {
        return executor;
    }

    @Override
    public final CompleteRec moduleAt(String path) {
        int i = BinarySearchTools.search(modules, (m) -> path.compareTo(m.path));
        if (i < 0) {
            throw new ModuleNotFoundError(path);
        }
        return modules[i].moduleRec;
    }

    @Override
    public final String name() {
        return name;
    }

}
