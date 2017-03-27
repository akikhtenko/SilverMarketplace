package com.silverbars.marketplace.orderboard.order;

import org.junit.Test;

import static com.silverbars.marketplace.orderboard.common.OrderType.BUY;
import static com.silverbars.marketplace.orderboard.order.Order.Builder.anOrder;
import static com.silverbars.marketplace.orderboard.order.OrderFactory.orderFrom;
import static com.silverbars.marketplace.orderboard.order.RegisterOrder.Builder.aRegisterOrderCommand;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class OrderFactoryTest {
    @Test
    public void should_initiate_order_from_register_order_command() {
        RegisterOrder registerOrderCommand =
                aRegisterOrderCommand().ofType(BUY).withPriceLevel(ONE).withQuantity(TEN).forUser("user").build();

        assertThat(orderFrom(registerOrderCommand).build(), is(
                anOrder().ofType(BUY).withPriceLevel(ONE).withQuantity(TEN).forUser("user").build()
        ));
    }
}