package com.silverbars.marketplace.orderboard.ordersummary;

import org.junit.Test;

import static com.silverbars.marketplace.orderboard.ordersummary.OrderSummaryInfo.Builder.anOrderSummary;
import static com.silverbars.marketplace.orderboard.ordersummary.PriceLevelSummaryInfo.Builder.aSummary;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class OrderSummaryInfoTest {
    private static final PriceLevelSummaryInfo BUY_PRICE_LEVEL_SUMMARY_INFO =
            aSummary().forPriceLevel(ONE).withQuantity(TEN).build();
    private static final PriceLevelSummaryInfo SELL_PRICE_LEVEL_SUMMARY_INFO =
            aSummary().forPriceLevel(ONE).withQuantity(ONE).build();

    @Test
    public void should_construct_well_formed_summary_info_object() {
        OrderSummaryInfo orderSummaryInfo = anOrderSummary()
                .withBuySideSummaries(singleton(BUY_PRICE_LEVEL_SUMMARY_INFO))
                .withSellSideSummaries(singleton(SELL_PRICE_LEVEL_SUMMARY_INFO)).build();

        assertThat(orderSummaryInfo.buySideSummaries(), is(singletonList(BUY_PRICE_LEVEL_SUMMARY_INFO)));
        assertThat(orderSummaryInfo.sellSideSummaries(), is(singletonList(SELL_PRICE_LEVEL_SUMMARY_INFO)));
    }

    @Test
    public void should_advise_equality_when_compared_to_idential_summary_info() {
        OrderSummaryInfo orderSummaryInfo = anOrderSummary()
                .withBuySideSummaries(singleton(BUY_PRICE_LEVEL_SUMMARY_INFO))
                .withSellSideSummaries(singleton(SELL_PRICE_LEVEL_SUMMARY_INFO)).build();
        OrderSummaryInfo anotherOrderSummaryInfo = anOrderSummary()
                .withBuySideSummaries(singleton(BUY_PRICE_LEVEL_SUMMARY_INFO))
                .withSellSideSummaries(singleton(SELL_PRICE_LEVEL_SUMMARY_INFO)).build();

        assertThat(orderSummaryInfo, is(equalTo(anotherOrderSummaryInfo)));
    }

    @Test
    public void should_advise_inequality_when_compared_to_different_summary_info() {
        OrderSummaryInfo orderSummaryInfo = anOrderSummary()
                .withBuySideSummaries(singleton(BUY_PRICE_LEVEL_SUMMARY_INFO))
                .withSellSideSummaries(singleton(SELL_PRICE_LEVEL_SUMMARY_INFO)).build();
        OrderSummaryInfo anotherOrderSummaryInfo = anOrderSummary()
                .withBuySideSummaries(singleton(BUY_PRICE_LEVEL_SUMMARY_INFO))
                .withSellSideSummaries(singleton(aSummary().forPriceLevel(TEN).withQuantity(ONE).build())).build();

        assertThat(orderSummaryInfo, is(not(equalTo(anotherOrderSummaryInfo))));
    }
}