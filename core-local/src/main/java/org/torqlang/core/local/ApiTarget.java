/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

public interface ApiTarget {

    static ApiTarget create(ActorImage actorImage) {
        return new ApiTargetActorImage(actorImage);
    }

    static ApiTarget create(ActorRef actorRef) {
        return new ApiTargetActorRef(actorRef);
    }

    Object value();

    final class ApiTargetActorImage implements ApiTarget {
        public final ActorImage actorImage;

        ApiTargetActorImage(ActorImage actorImage) {
            this.actorImage = actorImage;
        }

        @Override
        public final ActorImage value() {
            return actorImage;
        }
    }

    final class ApiTargetActorRef implements ApiTarget {
        public final ActorRef actorRef;

        ApiTargetActorRef(ActorRef actorRef) {
            this.actorRef = actorRef;
        }

        @Override
        public final ActorRef value() {
            return actorRef;
        }
    }
}
