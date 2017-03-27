package com.silverbars.marketplace.orderboard.infrastructure;

import com.silverbars.marketplace.orderboard.order.OrderEvent;
import com.silverbars.marketplace.orderboard.order.SpecificOrderEvent;
import org.junit.Test;

import java.util.Iterator;

import static com.google.common.collect.Lists.newArrayList;
import static com.silverbars.marketplace.orderboard.order.Order.Builder.anOrder;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class MutableOrderHistoryTest {
    @Test
    public void should_create_order_history_with_initial_history_version_zero() {
        assertThat(new MutableOrderHistory().version(), is(0L));
    }

    @Test
    public void should_advise_valid_empty_history_when_no_events_added() {
        Iterator<OrderEvent> events = new MutableOrderHistory().events();
        assertFalse(events.hasNext());
    }

    @Test
    public void should_incerase_history_version_when_adding_new_events() {
        MutableOrderHistory orderHistory = new MutableOrderHistory();
        orderHistory.add(new OrderEvent(anOrder().build()) {});
        orderHistory.add(new OrderEvent(anOrder().build()) {});

        assertThat(orderHistory.version(), is(2L));
    }

    @Test
    public void should_advise_order_history_when_events_added() {
        MutableOrderHistory orderHistory = new MutableOrderHistory();
        OrderEvent firstEvent = new OrderEvent(anOrder().withQuantity(ONE).build()) {};
        OrderEvent secondEvent = new OrderEvent(anOrder().withQuantity(TEN).build()) {};
        orderHistory.add(firstEvent);
        orderHistory.add(secondEvent);

        Iterator<OrderEvent> events = orderHistory.events();

        assertThat(newArrayList(events), is(asList(firstEvent, secondEvent)));
    }

    @Test
    public void should_advise_equality_when_compared_to_idential_history() {
        MutableOrderHistory firstOrderHistory = new MutableOrderHistory();
        firstOrderHistory.add(new SpecificOrderEvent(anOrder().withQuantity(ONE).build()));
        MutableOrderHistory secondOrderHistory = new MutableOrderHistory();
        secondOrderHistory.add(new SpecificOrderEvent(anOrder().withQuantity(ONE).build()));

        assertThat(firstOrderHistory, is(equalTo(secondOrderHistory)));
    }

    @Test
    public void should_advise_inequality_when_compared_to_different_history() {
        MutableOrderHistory firstOrderHistory = new MutableOrderHistory();
        firstOrderHistory.add(new SpecificOrderEvent(anOrder().withQuantity(ONE).build()));
        MutableOrderHistory secondOrderHistory = new MutableOrderHistory();
        secondOrderHistory.add(new SpecificOrderEvent(anOrder().withQuantity(TEN).build()));

        assertThat(firstOrderHistory, is(not(equalTo(secondOrderHistory))));
    }
}