/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.klvm.ActorCfg;

@SuppressWarnings("ClassCanBeRecord")
public final class ApiRoute {

    public final ApiPath apiPath;
    public final ActorCfg actorCfg;

    public ApiRoute(ApiPath apiPath, ActorCfg actorCfg) {
        this.apiPath = apiPath;
        this.actorCfg = actorCfg;
    }

}
