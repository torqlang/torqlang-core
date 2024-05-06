/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.junit.jupiter.api.Test;
import org.torqlang.core.klvm.ActorCfg;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestStaticApiRouter {

    private static final String SOURCE = """
        actor TestApi() in
            handle ask 'GET'#{'query': query} in
                nothing
            end
        end""";

    @Test
    public void test01() throws Exception {

        StaticApiRouter router;
        ActorCfg testCfg = Actor.builder().configure(SOURCE).actorCfg();

        router = new StaticApiRouter(new ApiRoute[0]);
        assertNull(router.findRoute(new ApiPath("/orders")));

        router = new StaticApiRouter(new ApiRoute[]{
            new ApiRoute(new ApiPath("/orders"), testCfg)
        });
        assertNotNull(router.findRoute(new ApiPath("/orders")));

        router = new StaticApiRouter(new ApiRoute[]{
            new ApiRoute(new ApiPath("/orders"), testCfg),
            new ApiRoute(new ApiPath("/orders/{id})"), testCfg)
        });
        assertNotNull(router.findRoute(new ApiPath("/orders")));

        router = new StaticApiRouter(new ApiRoute[]{
            new ApiRoute(new ApiPath("/inventory"), testCfg),
            new ApiRoute(new ApiPath("/orders"), testCfg)
        });
        assertNotNull(router.findRoute(new ApiPath("/orders")));

        router = new StaticApiRouter(new ApiRoute[]{
            new ApiRoute(new ApiPath("/inventory"), testCfg),
            new ApiRoute(new ApiPath("/orders"), testCfg),
            new ApiRoute(new ApiPath("/orders/{id}"), testCfg)
        });
        assertNotNull(router.findRoute(new ApiPath("/orders")));
    }

}
