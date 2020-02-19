package com.silverbars.marketplace.orderboard.pricelevel;

import com.google.common.eventbus.Subscribe;
import com.silverbars.marketplace.orderboard.common.LockingRunner;
import com.silverbars.marketplace.orderboard.order.Order;
import com.silverbars.marketplace.orderboard.order.OrderCancelledEvent;
import com.silverbars.marketplace.orderboard.order.OrderRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.silverbars.marketplace.orderboard.pricelevel.PriceLevelSummary.aPriceLevelSummary;
import static java.math.BigDecimal.ZERO;
import static java.util.Objects.requireNonNull;

public class OrderEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(OrderEventListener.class);

    private PriceLevelSummaryRepository priceLevelSummaryRepository;
    private LockingRunner<PriceLevelSummaryId> withLock;

    public OrderEventListener(PriceLevelSummaryRepository priceLevelSummaryRepository, LockingRunner<PriceLevelSummaryId> priceLevelSummaryLockingRunner) {
        this.priceLevelSummaryRepository = requireNonNull(priceLevelSummaryRepository, "priceLevelSummaryRepository must not be null");
        this.withLock = requireNonNull(priceLevelSummaryLockingRunner, "priceLevelSummaryLockingRunner must not be null");
    }

    @Subscribe
    public void handle(OrderRegisteredEvent orderRegisteredEvent) {
        LOG.info("Received registration event for order {}", orderRegisteredEvent.orderId());
        adjustPriceLevelSummaryFrom(orderRegisteredEvent.order());
    }

    @Subscribe
    public void handle(OrderCancelledEvent orderCancelledEvent) {
        LOG.info("Received cancellation event for order {}", orderCancelledEvent.orderId());
        Order targetOrder = orderCancelledEvent.order();
        Order inverseOrder = targetOrder.copy().withQuantity(targetOrder.quantity().negate()).build();

        adjustPriceLevelSummaryFrom(inverseOrder);
    }

    private void adjustPriceLevelSummaryFrom(Order order) {
        PriceLevelSummaryId priceLevelSummaryId = PriceLevelSummaryId.from(order.priceLevel(), order.type());

        withLock.on(priceLevelSummaryId).run(() -> adjustAndSave(order, priceLevelSummaryId));
    }

    private void adjustAndSave(Order order, PriceLevelSummaryId priceLevelSummaryId) {
        PriceLevelSummary priceLevelSummary =
                priceLevelSummaryRepository.load(priceLevelSummaryId).orElse(aPriceLevelSummary(priceLevelSummaryId));

        LOG.info("Adjusting summary of price level {} and order type {} with quantity {} by {}",
                order.priceLevel(), order.type(), priceLevelSummary.quantity(), order.quantity());

        PriceLevelSummary adjustedSummary = priceLevelSummary.adjustBy(order.quantity());
        if (ZERO.compareTo(adjustedSummary.quantity()) < 0) {
            priceLevelSummaryRepository.store(adjustedSummary);
        } else {
            priceLevelSummaryRepository.delete(priceLevelSummaryId);
        }
    }
}
