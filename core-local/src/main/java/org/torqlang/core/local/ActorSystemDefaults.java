/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

final class ActorSystemDefaults {

    static final ActorSystem DEFAULT_SYSTEM;
    static final ExecutorService DEFAULT_EXECUTOR;

    static {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        DEFAULT_EXECUTOR = Executors.newFixedThreadPool(Math.max(4, availableProcessors));
        DEFAULT_SYSTEM = ActorSystem.builder()
            .setName("Default")
            .setExecutor(DEFAULT_EXECUTOR)
            .build();
    }

    static ExecutorService executor() {
        return DEFAULT_EXECUTOR;
    }

    static void shutdownAndAwait(long millis) throws InterruptedException {
        DEFAULT_EXECUTOR.shutdown();
        if (!DEFAULT_EXECUTOR.awaitTermination(millis, TimeUnit.MILLISECONDS)) {
            throw new IllegalStateException("Time expired awaiting termination");
        }
    }

}
