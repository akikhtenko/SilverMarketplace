package com.silverbars.marketplace.orderboard.infrastructure;

import com.silverbars.marketplace.orderboard.common.LockingRunner;
import com.silverbars.marketplace.orderboard.order.EventStore;
import com.silverbars.marketplace.orderboard.order.OrderEvent;
import com.silverbars.marketplace.orderboard.order.OrderHistory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

public class InMemoryEventStore implements EventStore {
    private Map<String, MutableOrderHistory> eventsByOrderId = new ConcurrentHashMap<>();
    private LockingRunner<String> withLock;

    public InMemoryEventStore(LockingRunner<String> orderLockingRunner) {
        this.withLock = requireNonNull(orderLockingRunner, "orderLockingRunner must not be null");
    }

    @Override
    public boolean tryStore(long version, OrderEvent event) {
        eventsByOrderId.putIfAbsent(event.orderId(), new MutableOrderHistory());
        MutableOrderHistory orderHistory = eventsByOrderId.get(event.orderId());

        return withLock.on(event.orderId()).run(() -> {
            if (orderHistory.version() != version) {
                return false;
            }
            orderHistory.add(event);
            return true;
        });
    }

    @Override
    public Optional<OrderHistory> loadHistory(String orderId) {
        return Optional.ofNullable(eventsByOrderId.get(orderId));
    }
}
