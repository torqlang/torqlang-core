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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

public class NorthwindCache {

    private static volatile Complete ordersCache;

    public static String readStringFromUrl(URL url) throws IOException {
        try (InputStream inputStream = url.openStream()) {
            StringBuilder answer = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line = br.readLine();
                while (line != null) {
                    answer.append(line);
                    line = br.readLine();
                    if (line != null) {
                        answer.append("\n");
                    }
                }
            }
            return answer.toString();
        }
    }

    public static Complete getOrders() throws IOException {
        if (ordersCache != null) {
            return ordersCache;
        }
        URL ordersUrl = NorthwindCache.class.getResource("/northwind/orders.json");
        String ordersJsonText = readStringFromUrl(ordersUrl);
        List<?> ordersJsonList = (List<?>) JsonParser.parse(ordersJsonText);
        return ordersCache = ValueTools.toKernelValue(ordersJsonList);
    }

    public static void main(String[] args) throws IOException {
        System.out.println(getOrders());
    }

}
