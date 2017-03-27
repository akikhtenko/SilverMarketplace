package com.silverbars.marketplace.orderboard.infrastructure;

import com.google.common.base.Objects;
import com.silverbars.marketplace.orderboard.order.OrderEvent;
import com.silverbars.marketplace.orderboard.order.OrderHistory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.MoreObjects.toStringHelper;

public class MutableOrderHistory implements OrderHistory {
    private long version = 0;
    private List<OrderEvent> events;

    public MutableOrderHistory() {
        this.events = new LinkedList<>();
    }

    public void add(OrderEvent orderEvent) {
        events.add(orderEvent);
        version++;
    }

    public long version() {
        return version;
    }

    public Iterator<OrderEvent> events() {
        return events.iterator();
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("version", version)
                .add("events", events)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(version, events);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (this.getClass() != other.getClass()) {
            return false;
        }

        final MutableOrderHistory that = (MutableOrderHistory) other;

        return Objects.equal(this.version, that.version)
                && Objects.equal(this.events, that.events);
    }
}
