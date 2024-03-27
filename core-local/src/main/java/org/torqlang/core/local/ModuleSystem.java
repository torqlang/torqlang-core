/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.klvm.CompleteRec;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/*
 * INVARIANT: Each loader must be thread safe and always return the same value.
 */
public final class ModuleSystem {

    private static final ConcurrentHashMap<String, CompleteRec> cache = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String, Supplier<CompleteRec>> loaders = new ConcurrentHashMap<>();

    public static CompleteRec moduleAt(String path) {
        CompleteRec answer = cache.get(path);
        if (answer != null) {
            return answer;
        }
        Supplier<CompleteRec> loader = loaders.get(path);
        if (loader == null) {
            throw new ModuleNotFoundError(path);
        }
        answer = loader.get();
        cache.put(path, answer);
        return answer;
    }

    public static void register(String path, Supplier<CompleteRec> loader) {
        loaders.put(path, loader);
        cache.remove(path);
    }

}
