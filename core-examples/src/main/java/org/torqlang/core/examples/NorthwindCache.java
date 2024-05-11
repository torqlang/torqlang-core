/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.examples;

import org.torqlang.core.klvm.Complete;
import org.torqlang.core.lang.JsonParser;
import org.torqlang.core.lang.ValueTools;

import java.util.List;

import static org.torqlang.core.examples.AbstractExample.readTextFromResource;

public class NorthwindCache {

    private static volatile Complete ordersCache;

    public static Complete getOrders() throws Exception {
        if (ordersCache != null) {
            return ordersCache;
        }
        String ordersJsonText = readTextFromResource("/northwind/orders.json");
        List<?> ordersJsonList = (List<?>) JsonParser.parse(ordersJsonText);
        return ordersCache = ValueTools.toKernelValue(ordersJsonList);
    }

}
