/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.examples;

import org.torqlang.core.klvm.Complete;
import org.torqlang.core.lang.ValueTools;
import org.torqlang.core.local.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class QueryOrders extends AbstractExample {

    public static final String SOURCE = """
        actor Orders() in
            import system[ArrayList, FieldIter, ValueIter]
            var orders = ${1}
            handle ask 'GET'#{'headers': headers, 'path': path, 'query': query} in
                func matches_query(order) in
                    for field in FieldIter.new(query) do
                        if order[field.0] != field.1 then
                            return false
                        end
                    end
                    true
                end
                case path
                    of ['orders'] then
                        var array_list = ArrayList.new()
                        for order in ValueIter.new(orders) do
                            if matches_query(order) then
                                array_list.add(order);
                            end
                        end
                        array_list.to_tuple()
                    of ['orders', order_id] then
                        orders[order_id]
                    else
                        throw 'error'#{
                            'message': 'Invalid request',
                            'details': {
                                'path': path
                            }
                        }
                end
            end
            handle ask 'POST'#{'headers': headers, 'path': path, 'query': query, 'body': body} in
                // For now, just echo the params
                'POST'#{
                    'headers': headers,
                    'path': path,
                    'query': query,
                    'body': body
                }
            end
        end""";

    public static void main(String[] args) throws Exception {
        new QueryOrders().performWithErrorCheck();
        System.exit(0);
    }

    @Override
    public final void perform() throws Exception {

        // Compile Orders API handler and capture its image. Usually, this is performed once at startup.
        String queryOrdersSource = SOURCE.replace("${1}", NorthwindCache.ordersJsonText());
        ActorImage ordersImage = Actor.captureImage(queryOrdersSource);
        ApiRouter router = ApiRouter.staticBuilder()
            .addRoute("/orders", ordersImage)
            .build();

        // Spawn orders API handler using an actor image. Usually, this is performed by a REST server each
        // time a request is received. Spawning an actor using an image is fast.
        ApiRoute route = router.findRoute(new ApiPath("/orders"));
        ActorRef actorRef = Actor.spawn(Address.create(getClass().getName() + "Actor"),
            (ActorImage) route.apiTarget.value());

        // Test the orders API handler by sending it a request
        Map<?, ?> requestMap = Map.of(
            "$label", "GET",
            "$rec", Map.of(
                "headers", Map.of(),
                "path", List.of("orders"),
                "query", Map.of(
                    "ship_city", "Las Vegas"
                )
            )
        );
        Object response = RequestClient.builder().sendAndAwaitResponse(actorRef,
            ValueTools.toKernelValue(requestMap), Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        checkNotFailedValue(response);
        List<?> nativeResponse = (List<?>) ValueTools.toNativeValue((Complete) response);
        checkExpectedResponse(4, nativeResponse.size());
        for (Object obj : nativeResponse) {
            checkExpectedResponse("Las Vegas", ((Map<?, ?>) obj).get("ship_city"));
        }
    }

}
