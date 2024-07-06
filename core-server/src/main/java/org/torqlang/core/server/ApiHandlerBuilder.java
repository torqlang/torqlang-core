/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.server;

import org.torqlang.core.local.ActorSystem;
import org.torqlang.core.local.ApiRouter;

public final class ApiHandlerBuilder {

    private ActorSystem system;
    private ApiRouter apiRouter;

    ApiHandlerBuilder() {
    }

    public ApiHandler build() {
        return new ApiHandler(system, apiRouter);
    }

    public final ApiHandlerBuilder setApiRouter(ApiRouter apiRouter) {
        this.apiRouter = apiRouter;
        return this;
    }

    public final ApiHandlerBuilder setSystem(ActorSystem system) {
        this.system = system;
        return this;
    }

}
