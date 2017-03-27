package com.silverbars.marketplace.orderboard.order;

import com.silverbars.marketplace.orderboard.common.Retrier;
import com.silverbars.marketplace.orderboard.infrastructure.MutableOrderHistory;
import com.silverbars.marketplace.orderboard.order.exception.InvalidOrderException;
import com.silverbars.marketplace.orderboard.order.exception.OrderNotFoundException;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static com.silverbars.marketplace.orderboard.common.OrderType.BUY;
import static com.silverbars.marketplace.orderboard.order.Order.Builder.anOrder;
import static com.silverbars.marketplace.orderboard.order.OrderFactory.orderFrom;
import static com.silverbars.marketplace.orderboard.order.RegisterOrder.Builder.aRegisterOrderCommand;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderManagementServiceTest {
    private static final int NO_RETRIES = 0;
    @Mock
    private EventStore eventStore;
    @Mock
    private OrderIdGenerator orderIdGenerator;

    private OrderManagementService orderManagementService;

    @Before
    public void setup_order_management_service() {
        Retrier retrier = new Retrier(NO_RETRIES);
        orderManagementService = new OrderManagementService(eventStore, orderIdGenerator, retrier);
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void should_deny_invalid_order_registration() {
        exception.expect(InvalidOrderException.class);
        exception.expectMessage("Order registration failed");
        exception.expect(reasons(equalTo(asList("Id must be provided", "UserId must be provided"))));

        RegisterOrder invalidOrder = aRegisterOrderCommand().ofType(BUY).withPriceLevel(ONE).withQuantity(TEN).build();

        orderManagementService.registerOrder(invalidOrder);
    }

    @Test
    public void should_store_order_event_when_registering_valid_order() {
        when(orderIdGenerator.generateId()).thenReturn("id");
        RegisterOrder validOrder = aRegisterOrderCommand().ofType(BUY).withPriceLevel(ONE).withQuantity(TEN).forUser("user").build();

        orderManagementService.registerOrder(validOrder);

        verify(eventStore).store(new OrderRegisteredEvent(orderFrom(validOrder).withId("id").build()));
    }

    @Test
    public void should_report_order_id_when_registering_valid_order() {
        when(orderIdGenerator.generateId()).thenReturn("id");
        RegisterOrder validOrder = aRegisterOrderCommand().ofType(BUY).withPriceLevel(ONE).withQuantity(TEN).forUser("user").build();

        assertThat(orderManagementService.registerOrder(validOrder), is("id"));
    }

    @Test
    public void should_deny_cancellation_of_non_existing_order() {
        when(eventStore.loadHistory("orderId")).thenReturn(empty());

        exception.expect(OrderNotFoundException.class);
        exception.expectMessage("Non existing order orderId could not be cancelled");

        orderManagementService.cancelOrder("orderId");
    }

    @Test
    public void should_deny_storing_cancellation_event_when_rejected_too_many_times() {
        MutableOrderHistory orderHistory = new MutableOrderHistory();
        orderHistory.add(new OrderRegisteredEvent(anOrder().build()));
        when(eventStore.loadHistory("orderId")).thenReturn(Optional.of(orderHistory));
        when(eventStore.tryStore(anyLong(), any(OrderEvent.class))).thenReturn(false);

        exception.expectMessage("Operation [Cancellation event for order orderId] aborted after 1 attempts");

        orderManagementService.cancelOrder("orderId");
    }

    @Test
    public void should_store_cancellation_event_based_on_first_event_of_existing_order() {
        MutableOrderHistory orderHistory = new MutableOrderHistory();
        Order orderUnderFirstEvent = anOrder().forUser("wbuffett").build();
        orderHistory.add(new OrderRegisteredEvent(orderUnderFirstEvent));
        orderHistory.add(new OrderRegisteredEvent(anOrder().withId("gsoros").build()));

        when(eventStore.loadHistory("orderId")).thenReturn(Optional.of(orderHistory));
        when(eventStore.tryStore(anyLong(), any(OrderEvent.class))).thenReturn(true);

        orderManagementService.cancelOrder("orderId");

        verify(eventStore).tryStore(2, new OrderCancelledEvent(orderUnderFirstEvent));
    }

    private static FeatureMatcher<InvalidOrderException, List<String>> reasons(Matcher<List<String>> matcher) {
        return new FeatureMatcher<InvalidOrderException, List<String>>(matcher, " exception reasons", "reasons") {
            @Override
            protected List<String> featureValueOf(InvalidOrderException actual) {
                return actual.reasons();
            }
        };
    }

}