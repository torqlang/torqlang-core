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
                null
            end
        end""";

    @Test
    public void test01() throws Exception {

        ActorImage testActorImage = Actor.builder()
            .setSystem(ActorSystem.defaultSystem())
            .actorImage(SOURCE);

        StaticApiRouter router;

        router = new StaticApiRouter(new ApiRoute[0]);
        assertNull(router.findRoute(new ApiPath("/orders")));

        router = new StaticApiRouter(new ApiRoute[]{
            new ApiRoute(new ApiPath("/orders"), testActorImage)
        });
        assertNotNull(router.findRoute(new ApiPath("/orders")));
        assertNull(router.findRoute(new ApiPath("/orders/1")));
        assertNull(router.findRoute(new ApiPath("/shippers")));

        router = new StaticApiRouter(new ApiRoute[]{
            new ApiRoute(new ApiPath("/orders"), testActorImage),
            new ApiRoute(new ApiPath("/orders/{id}"), testActorImage)
        });
        assertNotNull(router.findRoute(new ApiPath("/orders")));
        assertNull(router.findRoute(new ApiPath("/shippers")));
        assertNotNull(router.findRoute(new ApiPath("/orders/1")));
        assertNull(router.findRoute(new ApiPath("/orders/1/releases")));

        router = new StaticApiRouter(new ApiRoute[]{
            new ApiRoute(new ApiPath("/inventory"), testActorImage),
            new ApiRoute(new ApiPath("/orders"), testActorImage)
        });
        assertNotNull(router.findRoute(new ApiPath("/inventory")));
        assertNull(router.findRoute(new ApiPath("/shippers")));
        assertNull(router.findRoute(new ApiPath("/inventory/1")));
        assertNotNull(router.findRoute(new ApiPath("/orders")));

        router = new StaticApiRouter(new ApiRoute[]{
            new ApiRoute(new ApiPath("/inventory"), testActorImage),
            new ApiRoute(new ApiPath("/inventory/{id}"), testActorImage),
            new ApiRoute(new ApiPath("/orders"), testActorImage),
            new ApiRoute(new ApiPath("/orders/{id}"), testActorImage)
        });
        assertNotNull(router.findRoute(new ApiPath("/orders")));
        assertNotNull(router.findRoute(new ApiPath("/inventory")));
        assertNull(router.findRoute(new ApiPath("/shippers")));
        assertNotNull(router.findRoute(new ApiPath("/inventory/1")));
        assertNull(router.findRoute(new ApiPath("/inventory/1/locations")));
        assertNotNull(router.findRoute(new ApiPath("/orders/1")));
        assertNull(router.findRoute(new ApiPath("/orders/1/releases")));

        router = new StaticApiRouter(new ApiRoute[]{
            new ApiRoute(new ApiPath("/orders"), testActorImage),
            new ApiRoute(new ApiPath("/orders/{id}"), testActorImage),
            new ApiRoute(new ApiPath("/orders/{id}/releases"), testActorImage),
            new ApiRoute(new ApiPath("/orders/{id}/releases/{id}"), testActorImage)
        });
        assertNotNull(router.findRoute(new ApiPath("/orders")));
        assertNotNull(router.findRoute(new ApiPath("/orders/1")));
        assertNotNull(router.findRoute(new ApiPath("/orders/1/releases")));
        assertNotNull(router.findRoute(new ApiPath("/orders/1/releases/1")));
        assertNull(router.findRoute(new ApiPath("/shippers")));
        assertNull(router.findRoute(new ApiPath("/orders/1/customer")));
        assertNull(router.findRoute(new ApiPath("/orders/1/customer/address")));
    }

}
