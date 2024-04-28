/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

final class StaticApiRouter implements ApiRouter {

    private final ApiRoute[] routingTable;

    StaticApiRouter(ApiRoute[] routingTable) {
        this.routingTable = routingTable;
    }

    private int binarySearchRoutes(ApiPath path) {
        int low = 0;
        int high = routingTable.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            ApiRoute route = routingTable[mid];
            int compare = path.compareTo(route.apiPath);
            if (compare > 0) {
                low = mid + 1;
            } else if (compare < 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return -(low + 1);
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
