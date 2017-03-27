package com.silverbars.marketplace.orderboard.infrastructure;

import com.silverbars.marketplace.orderboard.common.LockingRunner;
import com.silverbars.marketplace.orderboard.order.OrderEvent;
import org.junit.Test;

import java.util.Optional;

import static com.silverbars.marketplace.orderboard.order.EventStore.INITIAL_VERSION;
import static com.silverbars.marketplace.orderboard.order.Order.Builder.anOrder;
import static java.util.Optional.empty;
import static java.util.stream.LongStream.range;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class InMemoryEventStoreTest {
    private static final OrderEvent ORDER_EVENT =
            new OrderEvent(anOrder().withId("orderId").build()) {};;
    private InMemoryEventStore inMemoryEventStore = new InMemoryEventStore(new LockingRunner<>());

    @Test
    public void should_advise_empty_history_when_order_id_never_seen() {
        assertThat(inMemoryEventStore.loadHistory("never seen id"), is(empty()));
    }

    @Test
    public void should_reject_first_seen_order_event_when_version_provided_is_not_zero() {
        assertFalse(inMemoryEventStore.tryStore(1, ORDER_EVENT));
    }

    @Test
    public void should_accept_first_seen_order_event_when_version_provided_is_zero() {
        assertTrue(inMemoryEventStore.tryStore(INITIAL_VERSION, ORDER_EVENT));
    }

    @Test
    public void should_advise_history_for_previously_stored_order_id() {
        MutableOrderHistory expectedOrderHistory = new MutableOrderHistory();
        expectedOrderHistory.add(ORDER_EVENT);

        inMemoryEventStore.tryStore(INITIAL_VERSION, ORDER_EVENT);

        assertThat(inMemoryEventStore.loadHistory("orderId"), is(Optional.of(expectedOrderHistory)));
    }

    @Test
    public void should_advise_accumulated_history_of_order_id_after_series_of_events() {
        MutableOrderHistory expectedOrderHistory = new MutableOrderHistory();
        range(0,3).forEach(i -> expectedOrderHistory.add(ORDER_EVENT));

        range(0,3).forEach(version -> inMemoryEventStore.tryStore(version, ORDER_EVENT));

        assertThat(inMemoryEventStore.loadHistory("orderId"), is(Optional.of(expectedOrderHistory)));
    }
}