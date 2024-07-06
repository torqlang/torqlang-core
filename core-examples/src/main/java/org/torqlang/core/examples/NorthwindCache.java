/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.examples;

import static org.torqlang.core.examples.AbstractExample.readTextFromResource;

public class NorthwindCache {

    private static volatile String ordersJsonText;

    public static String ordersJsonText() throws Exception {
        if (ordersJsonText != null) {
            return ordersJsonText;
        }
        return ordersJsonText = readTextFromResource("/northwind/orders.json");
    }

}
