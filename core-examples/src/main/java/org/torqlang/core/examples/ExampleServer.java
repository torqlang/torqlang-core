/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.examples;

import org.torqlang.core.local.Actor;
import org.torqlang.core.local.ActorImage;
import org.torqlang.core.local.ApiRouter;
import org.torqlang.core.server.ApiHandler;
import org.torqlang.core.server.CoreServer;
import org.torqlang.core.server.EchoHandler;

public class ExampleServer {

    public static void main(String[] args) throws Exception {

        String queryOrdersSource = QueryOrders.SOURCE.replace("${1}", NorthwindCache.ordersJsonText());

        ActorImage ordersImage = Actor.captureImage(queryOrdersSource);

        CoreServer server = CoreServer.builder()
            .setPort(8080)
            .addContextHandler(new EchoHandler(), "/echo")
            .addContextHandler(ApiHandler.builder()
                .setApiRouter(ApiRouter.staticBuilder()
                    .addRoute("/orders", ordersImage)
                    .addRoute("/orders/{id}", ordersImage)
                    .build())
                .build(), "/api")
            .build();
        server.start();
        server.join();
    }

}
