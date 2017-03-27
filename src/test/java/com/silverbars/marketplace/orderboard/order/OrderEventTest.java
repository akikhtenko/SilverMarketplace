package com.silverbars.marketplace.orderboard.order;

import org.junit.Test;

import static com.silverbars.marketplace.orderboard.order.Order.Builder.anOrder;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class OrderEventTest {
    @Test
    public void should_construct_well_formed_order_event() {
        Order order = anOrder().withId("orderId").build();
        SpecificOrderEvent orderEvent = new SpecificOrderEvent(order);

        assertThat(orderEvent.order(), is(order));
        assertThat(orderEvent.orderId(), is(order.id()));
    }

    @Test
    public void should_advise_equality_when_compared_to_idential_order_event() {
        assertThat(new SpecificOrderEvent(anOrder().withId("orderId").build()),
                is(equalTo(new SpecificOrderEvent(anOrder().withId("orderId").build()))));
    }

    @Test
    public void should_advise_inequality_when_compared_to_different_event() {
        assertThat(new SpecificOrderEvent(anOrder().withId("orderId").build()),
                is(not(equalTo(new SpecificOrderEvent(anOrder().withId("anotherOrderId").build())))));
    }
}