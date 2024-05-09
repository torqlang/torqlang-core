/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.server;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

public final class CoreServerBuilder {

    private int port;
    private final ContextHandlerCollection contextHandlers = new ContextHandlerCollection();

    CoreServerBuilder() {
    }

    public final CoreServerBuilder setPort(int port) {
        this.port = port;
        return this;
    }

    public final int port() {
        return port;
    }

    public final CoreServerBuilder addContextHandler(Handler handler, String contextPath) {
        ContextHandler contextHandler = new ContextHandler(handler, contextPath);
        contextHandlers.addHandler(contextHandler);
        return this;
    }

    public CoreServer build() {
        Server server = new Server(port);
        Connector connector = new ServerConnector(server);
        server.addConnector(connector);
        server.setHandler(contextHandlers);
        return new CoreServer(server, port);
    }

}
