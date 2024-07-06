/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.klvm.EnvEntry;
import org.torqlang.core.klvm.OpaqueValue;

public final class ActorImage extends OpaqueValue {
    public final ActorSystem system;
    public final EnvEntry askHandlerEntry;
    public final EnvEntry tellHandlerEntry;

    public ActorImage(ActorSystem system, EnvEntry askHandlerEntry, EnvEntry tellHandlerEntry) {
        this.system = system;
        this.askHandlerEntry = askHandlerEntry;
        this.tellHandlerEntry = tellHandlerEntry;
    }
}
