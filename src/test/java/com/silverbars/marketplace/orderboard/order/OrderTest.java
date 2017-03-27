package com.silverbars.marketplace.orderboard.order;

import org.junit.Test;

import java.math.BigDecimal;

import static com.silverbars.marketplace.orderboard.common.OrderType.BUY;
import static com.silverbars.marketplace.orderboard.common.OrderType.SELL;
import static com.silverbars.marketplace.orderboard.order.Order.Builder.anOrder;
import static java.math.BigDecimal.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class OrderTest {
    private static final Order ORDER =
            anOrder().ofType(BUY).withId("orderId").withPriceLevel(ONE).withQuantity(TEN).forUser("wbuffett").build();
    private static final String BLANK = "";

    @Test
    public void should_construct_well_formed_order_object() {
        assertThat(ORDER.id(), is("orderId"));
        assertThat(ORDER.type(), is(BUY));
        assertThat(ORDER.priceLevel(), is(ONE));
        assertThat(ORDER.quantity(), is(TEN));
        assertThat(ORDER.userId(), is("wbuffett"));
    }

    @Test
    public void should_advise_equality_when_compared_to_idential_order() {
        Order anotherOrder =
                anOrder().ofType(BUY).withId("orderId").withPriceLevel(ONE).withQuantity(TEN).forUser("wbuffett").build();

        assertThat(ORDER, is(equalTo(anotherOrder)));
    }

    @Test
    public void should_advise_inequality_when_compared_to_different_order() {
        Order anotherOrder = anOrder().ofType(SELL).withId("orderId").withPriceLevel(ONE).withQuantity(TEN).forUser("wbuffett").build();

        assertThat(ORDER, is(not(equalTo(anotherOrder))));
    }

    @Test
    public void should_copy_order_into_identical_object() {
        assertThat(ORDER.copy().build(), is(equalTo(ORDER)));
    }

    @Test
    public void should_report_no_validation_errors_on_valid_order() {
        assertThat(ORDER.validate(), is(emptyList()));
    }

    @Test
    public void should_report_validation_error_when_id_not_provided() {
        Order invalidOrder = ORDER.copy().withId(null).build();

        assertThat(invalidOrder.validate(), is(singletonList("Id must be provided")));
    }

    @Test
    public void should_report_validation_error_when_id_is_blank() {
        Order invalidOrder = ORDER.copy().withId(BLANK).build();

        assertThat(invalidOrder.validate(), is(singletonList("Id must not be blank")));
    }

    @Test
    public void should_report_validation_error_when_type_not_provided() {
        Order invalidOrder = ORDER.copy().ofType(null).build();

        assertThat(invalidOrder.validate(), is(singletonList("Order type must be provided")));
    }

    @Test
    public void should_report_validation_error_when_price_level_not_provided() {
        Order invalidOrder = ORDER.copy().withPriceLevel(null).build();

        assertThat(invalidOrder.validate(), is(singletonList("Price level must be provided")));
    }

    @Test
    public void should_report_validation_error_when_price_level_is_zero() {
        Order invalidOrder = ORDER.copy().withPriceLevel(ZERO).build();

        assertThat(invalidOrder.validate(), is(singletonList("Price level must be a positive number while 0 was provided")));
    }

    @Test
    public void should_report_validation_error_when_price_level_is_negative() {
        Order invalidOrder = ORDER.copy().withPriceLevel(new BigDecimal(-1)).build();

        assertThat(invalidOrder.validate(), is(singletonList("Price level must be a positive number while -1 was provided")));
    }

    @Test
    public void should_report_validation_error_when_quantity_not_provided() {
        Order invalidOrder = ORDER.copy().withQuantity(null).build();

        assertThat(invalidOrder.validate(), is(singletonList("Quantity must be provided")));
    }

    @Test
    public void should_report_validation_error_when_quantity_is_zero() {
        Order invalidOrder = ORDER.copy().withQuantity(ZERO).build();

        assertThat(invalidOrder.validate(), is(singletonList("Quantity must be a positive number while 0 was provided")));
    }

    @Test
    public void should_report_validation_error_when_quantity_is_negative() {
        Order invalidOrder = ORDER.copy().withQuantity(new BigDecimal(-1)).build();

        assertThat(invalidOrder.validate(), is(singletonList("Quantity must be a positive number while -1 was provided")));
    }

    @Test
    public void should_report_validation_error_when_user_id_not_provided() {
        Order invalidOrder = ORDER.copy().forUser(null).build();

        assertThat(invalidOrder.validate(), is(singletonList("UserId must be provided")));
    }

    @Test
    public void should_report_validation_error_when_user_id_is_blank() {
        Order invalidOrder = ORDER.copy().forUser(BLANK).build();

        assertThat(invalidOrder.validate(), is(singletonList("UserId must not be blank")));
    }

    @Test
    public void should_report_mulriple_validation_errors_when_id_and_type_not_provided() {
        Order invalidOrder = ORDER.copy().ofType(null).withId(null).build();

        assertThat(invalidOrder.validate(), is(asList("Id must be provided", "Order type must be provided")));
    }
}