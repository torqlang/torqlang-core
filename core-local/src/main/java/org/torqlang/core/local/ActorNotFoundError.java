/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.klvm.MachineError;

public class ActorNotFoundError extends MachineError {
    public static final String ACTOR_NOT_FOUND = "Actor not found";
    public final Address address;

    public ActorNotFoundError(Address address) {
        super(ACTOR_NOT_FOUND);
        this.address = address;
    }
}
