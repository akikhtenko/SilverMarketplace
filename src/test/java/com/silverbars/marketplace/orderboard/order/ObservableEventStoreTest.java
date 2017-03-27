package com.silverbars.marketplace.orderboard.order;

import com.google.common.eventbus.EventBus;
import com.silverbars.marketplace.orderboard.infrastructure.MutableOrderHistory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static com.silverbars.marketplace.orderboard.order.Order.Builder.anOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ObservableEventStoreTest {
    private static final long VERSION = 1L;
    private static final OrderEvent ORDER_EVENT = new OrderEvent(anOrder().withId("orderId").build()) {};

    @Mock
    private EventStore underlyingEventStore;
    @Mock
    private EventBus outboundEventBus;
    @InjectMocks
    private ObservableEventStore observableEventStore;

    @Test
    public void should_report_event_rejection_on_underlying_event_store_rejection() {
        when(underlyingEventStore.tryStore(VERSION, ORDER_EVENT)).thenReturn(false);

        assertThat(observableEventStore.tryStore(VERSION, ORDER_EVENT), is(false));
    }

    @Test
    public void should_report_event_acceptance_on_underlying_event_store_acceptance() {
        when(underlyingEventStore.tryStore(VERSION, ORDER_EVENT)).thenReturn(true);

        assertThat(observableEventStore.tryStore(VERSION, ORDER_EVENT), is(true));
    }

    @Test
    public void should_skip_subscribers_notification_on_underlying_event_store_rejection() {
        when(underlyingEventStore.tryStore(VERSION, ORDER_EVENT)).thenReturn(false);

        observableEventStore.tryStore(VERSION, ORDER_EVENT);

        verifyZeroInteractions(outboundEventBus);
    }

    @Test
    public void should_notify_subscribers_on_underlying_event_store_acceptance() {
        when(underlyingEventStore.tryStore(VERSION, ORDER_EVENT)).thenReturn(true);

        observableEventStore.tryStore(VERSION, ORDER_EVENT);

        verify(outboundEventBus).post(ORDER_EVENT);
    }

    @Test
    public void should_delegate_event_history_retrieval_to_underlying_event_store() {
        MutableOrderHistory expectedOrderHistory = new MutableOrderHistory();
        expectedOrderHistory.add(ORDER_EVENT);
        expectedOrderHistory.add(ORDER_EVENT);

        when(underlyingEventStore.loadHistory("orderId")).thenReturn(Optional.of(expectedOrderHistory));

        assertThat(observableEventStore.loadHistory("orderId"), is(Optional.of(expectedOrderHistory)));
    }
}