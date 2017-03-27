package com.silverbars.marketplace.orderboard.order;

import java.util.Optional;

public interface EventStore {
    long INITIAL_VERSION = 0L;

    /**
     * Tries to store a new order event in the presence of concurrent events on the same order.
     * Locking on a given order is done in an optimistic manner using an order history version number.
     *
     * @param version Version of corresponding OrderHistory object that was previously obtained from loadHistory()
     * @param event Event to be stored
     * @return flag indicating whether event has been accepted or rejected by optimistic locking logic
     */
    boolean tryStore(long version, OrderEvent event);

    Optional<OrderHistory> loadHistory(String orderId);

    default void store(OrderRegisteredEvent event) {
        tryStore(INITIAL_VERSION, event);
    }
}