package com.silverbars.marketplace.orderboard.order;

import static com.silverbars.marketplace.orderboard.order.Order.Builder.anOrder;

public class OrderFactory {
    public static Order.Builder orderFrom(RegisterOrder registerOrderCommand) {
        return anOrder()
                .ofType(registerOrderCommand.type())
                .forUser(registerOrderCommand.userId())
                .withQuantity(registerOrderCommand.quantity())
                .withPriceLevel(registerOrderCommand.priceLevel());
    }
}
