package com.silverbars.marketplace.orderboard.pricelevel;

import org.junit.Test;

import static com.silverbars.marketplace.orderboard.common.OrderType.BUY;
import static com.silverbars.marketplace.orderboard.common.OrderType.SELL;
import static java.math.BigDecimal.ONE;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class PriceLevelSummaryIdTest {
    @Test
    public void should_construct_well_formed_price_level_summary_id() {
        PriceLevelSummaryId priceLevelSummaryId = PriceLevelSummaryId.from(ONE, BUY);

        assertThat(priceLevelSummaryId.priceLevel(), is(ONE));
        assertThat(priceLevelSummaryId.orderType(), is(BUY));
    }

    @Test
    public void should_advise_equality_when_compared_to_identical_price_level_summary_id() {
        assertThat(PriceLevelSummaryId.from(ONE, BUY),
                is(equalTo(PriceLevelSummaryId.from(ONE, BUY))));
    }

    @Test
    public void should_advise_inequality_when_compared_to_different_price_level_summary_id() {
        assertThat(PriceLevelSummaryId.from(ONE, BUY),
                is(not(equalTo(PriceLevelSummaryId.from(ONE, SELL)))));
    }
}