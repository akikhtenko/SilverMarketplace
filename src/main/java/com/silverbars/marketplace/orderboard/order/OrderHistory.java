package com.silverbars.marketplace.orderboard.order;

import java.util.Iterator;

public interface OrderHistory {
    long version();
    Iterator<OrderEvent> events();
}
