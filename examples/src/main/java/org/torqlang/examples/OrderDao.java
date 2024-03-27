/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.examples;

import org.torqlang.core.actor.ActorRef;
import org.torqlang.core.klvm.*;
import org.torqlang.core.local.RequestClient;

import java.util.concurrent.TimeUnit;

import static org.torqlang.core.local.ActorSystem.actorBuilder;
import static org.torqlang.core.local.ActorSystem.createAddress;

public final class OrderDao {

    public static final String SOURCE = """
        actor OrderDao() in
            import system[ArrayList, HashMap, LocalDate, Rec, ValueIter]
            var orders = HashMap.new()
            var order_lines = HashMap.new()
            orders.put('ORDER-001', {
                'id': 'ORDER-001',
                'customerId': 'CUST-001',
                'orderDate': LocalDate.new('2021-01-15'),
                'promiseDate': LocalDate.new('2021-02-15')
            })
            order_lines.put(['ORDER-001', 'LINE-001'], {
                'id': 'LINE-001',
                'lineNumber': 1,
                'orderId': 'ORDER-001',
                'productId': 'PROD-001',
                'orderQuantity': 3,
                'price': 12.00m,
                'amountDue': 36.00m
            })
            order_lines.put(['ORDER-001', 'LINE-002'], {
                'id': 'LINE-002',
                'lineNumber': 2,
                'orderId': 'ORDER-001',
                'productId': 'PROD-002',
                'orderQuantity': 7,
                'price': 32.00m,
                'amountDue': 224.00m
            })
            orders.put('ORDER-002', {
                'id': 'ORDER-002',
                'customerId': 'CUST-002',
                'orderDate': LocalDate.new('2021-01-25'),
                'promiseDate': LocalDate.new('2021-02-25')
            })
            order_lines.put(['ORDER-002', 'LINE-001'], {
                'id': 'LINE-001',
                'lineNumber': 1,
                'orderId': 'ORDER-002',
                'productId': 'PROD-003',
                'orderQuantity': 1,
                'price': 8.00m,
                'amountDue': 8.00m
            })
            ask 'find-order'#{'order-id': id} in
                var order = orders.get(id)
                if order == nothing then
                    throw 'error'#{'name': 'org.torqlang.examples.NotFoundError',
                                   'message': 'Order not found', 'order-id': id}
                end
                var selected_lines = ArrayList.new()
                for line in ValueIter.new(order_lines) do
                    if line.orderId == id then
                        selected_lines.add(line)
                    end
                end
                Rec.assign({'order-lines': selected_lines.to_tuple()}, order)
            end
        end""";

    public static void main(String[] args) throws Exception {
        perform();
        System.exit(0);
    }

    public static void perform() throws Exception {
        ActorRef actorRef = actorBuilder()
            .setAddress(createAddress(OrderDao.class.getName()))
            .setSource(SOURCE)
            .spawn();

        // Find order ORDER-002

        CompleteRec message = Rec.completeRecBuilder()
            .setLabel(Str.of("find-order"))
            .addField(Str.of("order-id"), Str.of("ORDER-002"))
            .build();
        Object response = RequestClient.builder()
            .setAddress(createAddress("OrderDaoClient"))
            .send(actorRef, message)
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        if (!(response instanceof CompleteRec completeRec)) {
            throw new IllegalStateException("Not a CompleteRec: " + response);
        }
        if (!completeRec.findValue(Str.of("id")).equals(Str.of("ORDER-002"))) {
            throw new IllegalStateException("Not ORDER-002: " + response);
        }

        // Generate NotFoundError

        message = Rec.completeRecBuilder()
            .setLabel(Str.of("find-order"))
            .addField(Str.of("order-id"), Str.of("ORDER-999"))
            .build();
        response = RequestClient.builder()
            .setAddress(createAddress("OrderDaoClient"))
            .send(actorRef, message)
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        if (!(response instanceof FailedValue failedValue)) {
            throw new IllegalStateException("Not a FailedValue: " + response);
        }
        if (!(failedValue.error() instanceof CompleteRec failedValueError)) {
            throw new IllegalStateException("FailedValue error is not a CompleteRec");
        }
        String expected = "org.torqlang.examples.NotFoundError";
        Str errorName = (Str) failedValueError.findValue(Str.of("name"));
        if (!errorName.value.equals(expected)) {
            throw new IllegalStateException("Error name is not: " + expected);
        }
        expected = "Order not found";
        Str errorMsg = (Str) failedValueError.findValue(Str.of("message"));
        if (!errorMsg.value.equals(expected)) {
            throw new IllegalStateException("Error message is not: " + expected);
        }
        expected = "ORDER-999";
        Str errorOrderId = (Str) failedValueError.findValue(Str.of("order-id"));
        if (!errorOrderId.value.equals(expected)) {
            throw new IllegalStateException("Order ID is not: " + expected);
        }
    }

}
