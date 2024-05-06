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
    private final ModuleEntry[] entries;

    BasicActorSystem(String name, Executor executor, List<ModuleEntry> entries) {
        this.name = name;
        this.executor = executor != null ?
            executor : ActorSystemDefaults.executor();
        this.entries = entries.toArray(new ModuleEntry[0]);
        Arrays.sort(this.entries);
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
        int i = BinarySearchTools.search(entries, (m) -> path.compareTo(m.path));
        if (i < 0) {
            throw new ModuleNotFoundError(path);
        }
        return entries[i].moduleRec;
    }

    @Override
    public final String name() {
        return name;
    }

}
