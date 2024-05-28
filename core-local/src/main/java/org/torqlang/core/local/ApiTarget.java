/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.klvm.ActorCfg;
import org.torqlang.core.klvm.Rec;

public interface ApiTarget {

    static ApiTarget create(ActorCfg actorCfg) {
        return new ApiTargetActorCfg(actorCfg);
    }

    static ApiTarget create(ActorRef actorRef) {
        return new ApiTargetActorRef(actorRef);
    }

    static ApiTarget create(Rec actorRec) {
        return new ApiTargetActorRec(actorRec);
    }

    Object value();

    final class ApiTargetActorCfg implements ApiTarget {
        public final ActorCfg actorCfg;

        ApiTargetActorCfg(ActorCfg actorCfg) {
            this.actorCfg = actorCfg;
        }

        @Override
        public final ActorCfg value() {
            return actorCfg;
        }
    }

    final class ApiTargetActorRec implements ApiTarget {
        public final Rec actorRec;

        ApiTargetActorRec(Rec actorRec) {
            this.actorRec = actorRec;
        }

        @Override
        public final Rec value() {
            return actorRec;
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
