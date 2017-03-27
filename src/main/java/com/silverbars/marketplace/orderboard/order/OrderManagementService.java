package com.silverbars.marketplace.orderboard.order;

import com.silverbars.marketplace.orderboard.common.Retrier;
import com.silverbars.marketplace.orderboard.order.exception.InvalidOrderException;
import com.silverbars.marketplace.orderboard.order.exception.OrderNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static com.silverbars.marketplace.orderboard.order.OrderFactory.orderFrom;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class OrderManagementService {
    private static final Logger LOG = LoggerFactory.getLogger(OrderManagementService.class);

    private EventStore eventStore;
    private OrderIdGenerator orderIdGenerator;
    private Retrier retrier;

    public OrderManagementService(EventStore eventStore, OrderIdGenerator orderIdGenerator, Retrier retrier) {
        this.eventStore = requireNonNull(eventStore, "eventStore must not be null");
        this.orderIdGenerator = requireNonNull(orderIdGenerator, "orderIdGenerator must not be null");
        this.retrier = requireNonNull(retrier, "retrier must not be null");
    }

    public String registerOrder(RegisterOrder registerOrder) {
        LOG.info("Received command to register new order with details: {}", registerOrder);

        Order targetOrder = orderFrom(registerOrder).withId(orderIdGenerator.generateId()).build();
        List<String> errors = targetOrder.validate();
        if (!errors.isEmpty()) {
            throw new InvalidOrderException("Order registration failed", errors);
        }

        eventStore.store(new OrderRegisteredEvent(targetOrder));

        LOG.info("Registered order with id {}", targetOrder.id());
        return targetOrder.id();
    }

    public void cancelOrder(String orderId) {
        LOG.info("Received command to cancel order {}", orderId);

        retrier.doWithConditionalRetry(format("Cancellation event for order %s", orderId), () -> {
            Optional<OrderHistory> maybeOrderHistory = eventStore.loadHistory(orderId);

            return maybeOrderHistory.map(orderHistory -> {
                Order cancellationTarget = rollup(orderHistory.events());
                return eventStore.tryStore(orderHistory.version(), new OrderCancelledEvent(cancellationTarget));
            }).orElseThrow(() -> new OrderNotFoundException(format("Non existing order %s could not be cancelled", orderId)));
        });

        LOG.info("Cancelled order {}", orderId);
    }

    private Order rollup(Iterator<OrderEvent> historicalEvents) {
        // Trivially take first event's target since we don't deal with updates yet
        return historicalEvents.next().order();
    }
}
