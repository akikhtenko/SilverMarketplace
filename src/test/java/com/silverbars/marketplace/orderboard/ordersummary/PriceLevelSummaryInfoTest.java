package com.silverbars.marketplace.orderboard.ordersummary;

import org.junit.Test;

import static com.silverbars.marketplace.orderboard.ordersummary.PriceLevelSummaryInfo.Builder.aSummary;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class PriceLevelSummaryInfoTest {
    @Test
    public void should_construct_well_formed_price_level_summary_info_object() {
        PriceLevelSummaryInfo priceLevelSummaryInfo = aSummary().forPriceLevel(ONE).withQuantity(TEN).build();

        assertThat(priceLevelSummaryInfo.priceLevel(), is(ONE));
        assertThat(priceLevelSummaryInfo.quantity(), is(TEN));
    }

    @Test
    public void should_advise_equality_when_compared_to_idential_price_level_summary_info() {
        assertThat(aSummary().forPriceLevel(ONE).withQuantity(TEN).build(),
                is(equalTo(aSummary().forPriceLevel(ONE).withQuantity(TEN).build())));
    }

    @Test
    public void should_advise_inequality_when_compared_to_different_price_level_summary_info() {
        assertThat(aSummary().forPriceLevel(ONE).withQuantity(TEN).build(),
                is(not(equalTo(aSummary().forPriceLevel(ONE).withQuantity(ONE).build()))));
    }
}