/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.junit.jupiter.api.Test;

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

        ActorRef testActorRef = Actor.builder()
            .setSystem(ActorSystem.defaultSystem())
            .spawn(SOURCE)
            .actorRef();

        StaticApiRouter router;

        router = new StaticApiRouter(new ApiRoute[0]);
        assertNull(router.findRoute(new ApiPath("/orders")));

        router = new StaticApiRouter(new ApiRoute[]{
            new ApiRoute(new ApiPath("/orders"), testActorRef)
        });
        assertNotNull(router.findRoute(new ApiPath("/orders")));

        router = new StaticApiRouter(new ApiRoute[]{
            new ApiRoute(new ApiPath("/orders"), testActorRef),
            new ApiRoute(new ApiPath("/orders/{id})"), testActorRef)
        });
        assertNotNull(router.findRoute(new ApiPath("/orders")));

        router = new StaticApiRouter(new ApiRoute[]{
            new ApiRoute(new ApiPath("/inventory"), testActorRef),
            new ApiRoute(new ApiPath("/orders"), testActorRef)
        });
        assertNotNull(router.findRoute(new ApiPath("/orders")));

        router = new StaticApiRouter(new ApiRoute[]{
            new ApiRoute(new ApiPath("/inventory"), testActorRef),
            new ApiRoute(new ApiPath("/orders"), testActorRef),
            new ApiRoute(new ApiPath("/orders/{id}"), testActorRef)
        });
        assertNotNull(router.findRoute(new ApiPath("/orders")));
    }

}
