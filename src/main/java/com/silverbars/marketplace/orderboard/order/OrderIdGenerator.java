package com.silverbars.marketplace.orderboard.order;

import java.util.function.Supplier;

public interface OrderIdGenerator extends Supplier<String> {
    String generateId();

    default String get() {
        return generateId();
    }
}
