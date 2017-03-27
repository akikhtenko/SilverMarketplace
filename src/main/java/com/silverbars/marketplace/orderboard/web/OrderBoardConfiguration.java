package com.silverbars.marketplace.orderboard.web;

import com.google.common.eventbus.EventBus;
import com.silverbars.marketplace.orderboard.common.Retrier;
import com.silverbars.marketplace.orderboard.infrastructure.InMemoryEventStore;
import com.silverbars.marketplace.orderboard.infrastructure.InMemoryPriceLevelSummaryRepository;
import com.silverbars.marketplace.orderboard.order.*;
import com.silverbars.marketplace.orderboard.ordersummary.OrderSummaryService;
import com.silverbars.marketplace.orderboard.pricelevel.OrderEventListener;
import com.silverbars.marketplace.orderboard.common.LockingRunner;
import com.silverbars.marketplace.orderboard.pricelevel.PriceLevelSummaryId;
import com.silverbars.marketplace.orderboard.pricelevel.PriceLevelSummaryRepository;
import com.silverbars.marketplace.orderboard.web.exception.CatchAllExceptionMapper;
import com.silverbars.marketplace.orderboard.web.exception.InvalidOrderExceptionMapper;
import com.silverbars.marketplace.orderboard.web.exception.OrderNotFoundExceptionMapper;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("/marketplace")
public class OrderBoardConfiguration extends ResourceConfig {

    private static final int MAX_ORDER_CANCELLAION_ATTEMPTS = 10;

    public OrderBoardConfiguration() {
        register(OrderBoardResource.class);
        register(InvalidOrderExceptionMapper.class);
        register(OrderNotFoundExceptionMapper.class);
        register(CatchAllExceptionMapper.class);
    }

    @Bean
    public LockingRunner<String> orderLockingRunner() {
        return new LockingRunner<>();
    }

    @Bean
    public EventBus eventBus() {
        return new EventBus("OrderEventBus");
    }

    @Bean
    public EventStore eventStore() {
        return new ObservableEventStore(new InMemoryEventStore(orderLockingRunner()), eventBus());
    }

    @Bean
    public OrderIdGenerator orderIdGenerator() {
        return new UUIDOrderIdGenerator();
    }

    @Bean
    public Retrier orderCancellationRetrier() {
        return new Retrier(MAX_ORDER_CANCELLAION_ATTEMPTS);
    }

    @Bean
    public OrderManagementService orderManagementService() {
        return new OrderManagementService(eventStore(), orderIdGenerator(), orderCancellationRetrier());
    }

    @Bean
    public PriceLevelSummaryRepository priceLevelSummaryRepository() {
        return new InMemoryPriceLevelSummaryRepository();
    }

    @Bean
    public LockingRunner<PriceLevelSummaryId> priceLevelSummaryLockingRunner() {
        return new LockingRunner<>();
    }

    @Bean
    public OrderEventListener orderEventListener() {
        OrderEventListener orderEventListener =
                new OrderEventListener(priceLevelSummaryRepository(), priceLevelSummaryLockingRunner());
        eventBus().register(orderEventListener);

        return orderEventListener;
    }

    @Bean
    public OrderSummaryService orderSummaryService() {
        return new OrderSummaryService(priceLevelSummaryRepository());
    }

}
