package com.silverbars.marketplace.orderboard.ordersummary;

import com.silverbars.marketplace.orderboard.common.OrderType;
import com.silverbars.marketplace.orderboard.pricelevel.PriceLevelSummary;
import com.silverbars.marketplace.orderboard.pricelevel.PriceLevelSummaryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.silverbars.marketplace.orderboard.ordersummary.OrderSummaryInfo.Builder.anOrderSummary;
import static com.silverbars.marketplace.orderboard.ordersummary.PriceLevelSummaryInfo.Builder.aSummary;
import static java.util.Objects.requireNonNull;

public class OrderSummaryService {
    private static final Logger LOG = LoggerFactory.getLogger(OrderSummaryService.class);

    private static final Comparator<PriceLevelSummaryInfo> ASCENDING_PRICE = Comparator.comparing(PriceLevelSummaryInfo::priceLevel);
    private static final Comparator<PriceLevelSummaryInfo> DESCENDING_PRICE = ASCENDING_PRICE.reversed();

    private PriceLevelSummaryRepository priceLevelSummaryRepository;

    public OrderSummaryService(PriceLevelSummaryRepository priceLevelSummaryRepository) {
        this.priceLevelSummaryRepository = requireNonNull(priceLevelSummaryRepository, "priceLevelSummaryRepository must not be null");
    }

    public OrderSummaryInfo summariseOrders() {
        LOG.info("Received request from client to summarise all live orders");

        SortedSet<PriceLevelSummaryInfo> sellSideSummaries = new TreeSet<>(ASCENDING_PRICE);
        SortedSet<PriceLevelSummaryInfo> buySideSummaries = new TreeSet<>(DESCENDING_PRICE);

        collectSummariesForSides(sellSideSummaries, buySideSummaries);

        return anOrderSummary()
                .withSellSideSummaries(sellSideSummaries)
                .withBuySideSummaries(buySideSummaries)
                .build();
    }

    private void collectSummariesForSides(SortedSet<PriceLevelSummaryInfo> sellSideSummaries, SortedSet<PriceLevelSummaryInfo> buySideSummaries) {
        List<PriceLevelSummary> priceLevelSummaries = priceLevelSummaryRepository.all();
        for (PriceLevelSummary priceLevelSummary: priceLevelSummaries) {
            OrderType orderType = priceLevelSummary.id().orderType();
            switch (orderType) {
                case BUY:
                    buySideSummaries.add(summaryInfoFrom(priceLevelSummary));
                    break;
                case SELL:
                    sellSideSummaries.add(summaryInfoFrom(priceLevelSummary));
                    break;
                default:
                    // Just log without failing user read
                    LOG.warn("Unknown order type {} encountered", orderType);
            }
        }
    }

    private static PriceLevelSummaryInfo summaryInfoFrom(PriceLevelSummary priceLevelSummary) {
        return aSummary()
                .forPriceLevel(priceLevelSummary.id().priceLevel())
                .withQuantity(priceLevelSummary.quantity())
                .build();
    }
}
