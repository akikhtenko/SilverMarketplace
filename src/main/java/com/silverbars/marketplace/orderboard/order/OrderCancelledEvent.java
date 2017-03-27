package com.silverbars.marketplace.orderboard.order;

public class OrderCancelledEvent extends OrderEvent {
    public OrderCancelledEvent(Order targetOrder) {
        super(targetOrder);
    }
}
