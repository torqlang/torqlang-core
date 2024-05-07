/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.examples;

import org.torqlang.core.klvm.CompleteRec;
import org.torqlang.core.klvm.Rec;
import org.torqlang.core.klvm.Str;
import org.torqlang.core.local.Actor;

final class ExamplesMod {

    private final CompleteRec moduleRec;

    private ExamplesMod() {
        try {
            moduleRec = Rec.completeRecBuilder()
                .addField(Str.of("IntPublisher"), Actor.compileForImport(IntPublisher.SOURCE))
                .build();
        } catch (Exception exc) {
            throw new IllegalStateException("ExamplesMod error", exc);
        }
    }

    public static CompleteRec moduleRec() {
        return LazySingleton.SINGLETON.moduleRec;
    }

    private static class LazySingleton {
        private static final ExamplesMod SINGLETON = new ExamplesMod();
    }

}
