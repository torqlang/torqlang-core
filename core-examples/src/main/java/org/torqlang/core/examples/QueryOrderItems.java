/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.examples;

public class QueryOrderItems extends AbstractExample {

    public static final String SOURCE = """
        // meta#{'path': '/orders/{id}/items?{query-string}'}
        actor OrderItems() in
            import system.ArrayList
            import examples.NorthwindCache.get_orders
            var orders = get_orders()
            handle ask 'GET'#{'id': id, 'query-string': query_string} in
                skip
            end
        end""";

    public static void main(String[] args) throws Exception {
        new QueryOrderItems().performWithErrorCheck();
        System.exit(0);
    }

    @Override
    public final void perform() throws Exception {
        // TODO
    }

}
