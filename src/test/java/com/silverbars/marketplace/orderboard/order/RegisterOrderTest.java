package com.silverbars.marketplace.orderboard.order;

import org.junit.Test;

import static com.silverbars.marketplace.orderboard.common.OrderType.BUY;
import static com.silverbars.marketplace.orderboard.common.OrderType.SELL;
import static com.silverbars.marketplace.orderboard.order.RegisterOrder.Builder.aRegisterOrderCommand;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class RegisterOrderTest {
    private static final RegisterOrder ORDER_СOMMAND =
            aRegisterOrderCommand().ofType(BUY).withPriceLevel(ONE).withQuantity(TEN).forUser("wbuffett").build();

    @Test
    public void should_construct_well_formed_create_order_command() {
        assertThat(ORDER_СOMMAND.type(), is(BUY));
        assertThat(ORDER_СOMMAND.priceLevel(), is(ONE));
        assertThat(ORDER_СOMMAND.quantity(), is(TEN));
        assertThat(ORDER_СOMMAND.userId(), is("wbuffett"));
    }

    @Test
    public void should_advise_equality_when_compared_to_idential_command() {
        RegisterOrder anotherCommand =
                aRegisterOrderCommand().ofType(BUY).withPriceLevel(ONE).withQuantity(TEN).forUser("wbuffett").build();

        assertThat(ORDER_СOMMAND, is(equalTo(anotherCommand)));
    }

    @Test
    public void should_advise_inequality_when_compared_to_different_command() {
        RegisterOrder anotherCommand =
                aRegisterOrderCommand().ofType(SELL).withPriceLevel(ONE).withQuantity(TEN).forUser("wbuffett").build();

        assertThat(ORDER_СOMMAND, is(not(equalTo(anotherCommand))));
    }
}