package com.silverbars.marketplace.orderboard.order;

public class OrderRegisteredEvent extends OrderEvent {
    public OrderRegisteredEvent(Order targetOrder) {
        super(targetOrder);
    }
}
