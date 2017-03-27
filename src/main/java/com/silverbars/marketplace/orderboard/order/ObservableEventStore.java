package com.silverbars.marketplace.orderboard.order;

import com.google.common.eventbus.EventBus;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class ObservableEventStore implements EventStore {
    private EventStore underlyingEventStore;
    private EventBus outboundEventBus;

    public ObservableEventStore(EventStore underlyingEventStore, EventBus outboundEventBus) {
        this.underlyingEventStore = requireNonNull(underlyingEventStore, "underlyingEventStore must not be null");
        this.outboundEventBus = requireNonNull(outboundEventBus, "outboundEventBus must not be null");
    }

    @Override
    public boolean tryStore(long version, OrderEvent event) {
        boolean accepted = underlyingEventStore.tryStore(version, event);
        if (accepted) {
            outboundEventBus.post(event);
        }
        return accepted;
    }

    @Override
    public Optional<OrderHistory> loadHistory(String orderId) {
        return underlyingEventStore.loadHistory(orderId);
    }
}
