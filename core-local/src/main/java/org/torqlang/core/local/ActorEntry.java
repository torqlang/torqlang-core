/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import java.util.Objects;

@SuppressWarnings("ClassCanBeRecord")
public final class ActorEntry implements Comparable<ActorEntry> {
    public final Address address;
    public final ActorRef actorRef;

    public ActorEntry(Address address, ActorRef actorRef) {
        this.address = address;
        this.actorRef = actorRef;
    }

    @Override
    public final int compareTo(ActorEntry actorEntry) {
        return address.compareTo(actorEntry.address);
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        ActorEntry that = (ActorEntry) other;
        return Objects.equals(address, that.address);
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(address);
    }
}
