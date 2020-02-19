package com.silverbars.marketplace.orderboard.pricelevel;

import com.silverbars.marketplace.orderboard.common.LockingRunner;
import com.silverbars.marketplace.orderboard.order.Order;
import com.silverbars.marketplace.orderboard.order.OrderCancelledEvent;
import com.silverbars.marketplace.orderboard.order.OrderRegisteredEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static com.silverbars.marketplace.orderboard.common.OrderType.BUY;
import static com.silverbars.marketplace.orderboard.order.Order.Builder.anOrder;
import static com.silverbars.marketplace.orderboard.pricelevel.PriceLevelSummary.aPriceLevelSummary;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.util.Optional.empty;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderEventListenerTest {
    private static final Order ORDER = anOrder().ofType(BUY).withPriceLevel(ONE).withQuantity(TEN).build();
    private static final OrderRegisteredEvent ORDER_REGISTRATION_EVENT = new OrderRegisteredEvent(ORDER);
    private static final PriceLevelSummaryId PRICE_LEVEL_SUMMARY_ID = PriceLevelSummaryId.from(ONE, BUY);

    @Mock
    private PriceLevelSummaryRepository priceLevelSummaryRepository;
    private OrderEventListener orderEventListener;

    @Before
    public void setup_order_event_listener() {
        orderEventListener = new OrderEventListener(priceLevelSummaryRepository, new LockingRunner<>());
    }

    @Test
    public void should_store_initial_summary_on_first_order_registration_event_for_price_level() {
        when(priceLevelSummaryRepository.load(PRICE_LEVEL_SUMMARY_ID)).thenReturn(empty());

        orderEventListener.handle(ORDER_REGISTRATION_EVENT);

        verify(priceLevelSummaryRepository).store(aPriceLevelSummary(PRICE_LEVEL_SUMMARY_ID).adjustBy(TEN));
    }

    @Test
    public void should_store_adjusted_summary_on_non_first_order_registration_event_for_price_level() {
        when(priceLevelSummaryRepository.load(PRICE_LEVEL_SUMMARY_ID)).thenReturn(
                Optional.of(aPriceLevelSummary(PRICE_LEVEL_SUMMARY_ID).adjustBy(new BigDecimal(100))));

        orderEventListener.handle(ORDER_REGISTRATION_EVENT);

        verify(priceLevelSummaryRepository).store(
                aPriceLevelSummary(PRICE_LEVEL_SUMMARY_ID).adjustBy(new BigDecimal(110)));
    }

    @Test
    public void should_store_adjusted_summary_on_order_cancellation_event() {
        when(priceLevelSummaryRepository.load(PRICE_LEVEL_SUMMARY_ID)).thenReturn(
                Optional.of(aPriceLevelSummary(PRICE_LEVEL_SUMMARY_ID).adjustBy(new BigDecimal(100))));

        orderEventListener.handle(new OrderCancelledEvent(ORDER));

        verify(priceLevelSummaryRepository).store(
                aPriceLevelSummary(PRICE_LEVEL_SUMMARY_ID).adjustBy(new BigDecimal(90)));
    }

    @Test
    public void should_delete_singleton_summary_on_order_cancellation_event() {
        when(priceLevelSummaryRepository.load(PRICE_LEVEL_SUMMARY_ID)).thenReturn(
                Optional.of(aPriceLevelSummary(PRICE_LEVEL_SUMMARY_ID).adjustBy(TEN)));

        orderEventListener.handle(new OrderCancelledEvent(ORDER));

        verify(priceLevelSummaryRepository).delete(PRICE_LEVEL_SUMMARY_ID);
    }
}