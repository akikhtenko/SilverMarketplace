package com.silverbars.marketplace.orderboard.order;

import com.google.common.base.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;

public abstract class OrderEvent {
    private Order order;

    public OrderEvent(Order order) {
        this.order = order;
    }

    public String orderId() {
        return order.id();
    }

    public Order order() {
        return order;
    }

    @Override
    public String toString() {
        return toStringHelper(this).add("order", order).toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(order);
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

        final OrderEvent that = (OrderEvent) other;

        return Objects.equal(this.order, that.order);
    }
}
