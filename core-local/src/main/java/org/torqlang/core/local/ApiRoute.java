/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.klvm.ActorCfg;
import org.torqlang.core.klvm.Rec;

public final class ApiRoute {

    public final ApiPath apiPath;
    public final ApiTarget apiTarget;

    public ApiRoute(ApiPath apiPath, ApiTarget apiTarget) {
        this.apiPath = apiPath;
        this.apiTarget = apiTarget;
    }

    public ApiRoute(ApiPath apiPath, ActorCfg actorCfg) {
        this(apiPath, ApiTarget.create(actorCfg));
    }

    public ApiRoute(ApiPath apiPath, ActorRef actorRef) {
        this(apiPath, ApiTarget.create(actorRef));
    }

    public ApiRoute(ApiPath apiPath, Rec actorRec) {
        this(apiPath, ApiTarget.create(actorRec));
    }

}
