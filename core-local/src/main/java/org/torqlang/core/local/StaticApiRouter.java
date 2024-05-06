/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.util.BinarySearchTools;

final class StaticApiRouter implements ApiRouter {

    private final ApiRoute[] routingTable;

    StaticApiRouter(ApiRoute[] routingTable) {
        this.routingTable = routingTable;
    }

    private int binarySearchRoutes(ApiPath path) {
        return BinarySearchTools.search(routingTable, (r) -> path.compareTo(r.apiPath));
    }

    @Override
    public final ApiRoute findRoute(ApiPath path) {
        int index = binarySearchRoutes(path);
        if (index < 0) {
            return null;
        }
        return routingTable[index];
    }

}
