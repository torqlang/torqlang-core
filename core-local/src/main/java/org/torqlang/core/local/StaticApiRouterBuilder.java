/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class StaticApiRouterBuilder {

    private final List<ApiRoute> routes = new ArrayList<>();

    public final StaticApiRouterBuilder addRoute(String pathExpr, ActorImage actorImage) {
        ApiPath path = new ApiPath(pathExpr);
        routes.add(new ApiRoute(path, actorImage));
        return this;
    }

    public final StaticApiRouterBuilder addRoute(String pathExpr, ActorRef actorRef) {
        ApiPath path = new ApiPath(pathExpr);
        routes.add(new ApiRoute(path, actorRef));
        return this;
    }

    public final ApiRouter build() {
        routes.sort(Comparator.comparing(a -> a.apiPath));
        return new StaticApiRouter(routes.toArray(new ApiRoute[0]));
    }

}
