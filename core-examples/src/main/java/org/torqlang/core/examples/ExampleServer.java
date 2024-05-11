/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.examples;

import org.torqlang.core.klvm.*;
import org.torqlang.core.local.Actor;
import org.torqlang.core.local.ActorSystem;
import org.torqlang.core.local.ApiRouter;
import org.torqlang.core.local.AsyncMethod;
import org.torqlang.core.server.ApiHandler;
import org.torqlang.core.server.CoreServer;
import org.torqlang.core.server.EchoHandler;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class ExampleServer {

    public static void main(String[] args) throws Exception {

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup.findStatic(NorthwindCache.class, "getOrders", MethodType.methodType(Complete.class));
        CompleteRec moduleRec = Rec.completeRecBuilder()
            .addField(Str.of("get_orders"), new AsyncMethod(methodHandle))
            .build();

        ActorSystem system = ActorSystem.builder()
            .addDefaultModules()
            .addModule("examples.NorthwindCache", moduleRec)
            .build();

        ActorCfg ordersCfg = Actor.builder()
            .setSystem(system)
            .configure(QueryOrders.SOURCE)
            .actorCfg();

        CoreServer server = CoreServer.builder()
            .setPort(8080)
            .addContextHandler(new EchoHandler(), "/echo")
            .addContextHandler(ApiHandler.builder()
                .setSystem(system)
                .setApiRouter(ApiRouter.staticBuilder()
                    .addRoute("/orders", ordersCfg)
                    .addRoute("/orders/{id}", ordersCfg)
                    .build())
                .build(), "/api")
            .build();
        server.start();
        server.join();
    }

}
