package com.silverbars.marketplace.orderboard.pricelevel;

import org.junit.Test;

import static com.silverbars.marketplace.orderboard.common.OrderType.BUY;
import static com.silverbars.marketplace.orderboard.pricelevel.PriceLevelSummary.aPriceLevelSummary;
import static java.math.BigDecimal.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class PriceLevelSummaryTest {

    private static final PriceLevelSummaryId SUMMARY_ID = PriceLevelSummaryId.from(ONE, BUY);

    @Test
    public void should_construct_well_formed_price_level_summary() {
        PriceLevelSummary priceLevelSummary = aPriceLevelSummary(SUMMARY_ID);

        assertThat(priceLevelSummary.id(), is(SUMMARY_ID));
    }

    @Test
    public void should_construct_new_price_level_summary_with_zero_quantity() {
        PriceLevelSummary priceLevelSummary = aPriceLevelSummary(SUMMARY_ID);

        assertThat(priceLevelSummary.quantity(), is(ZERO));
    }

    @Test
    public void should_adjust_current_quantity_by_given_delta() {
        PriceLevelSummary priceLevelSummary = aPriceLevelSummary(SUMMARY_ID).adjustBy(TEN);

        assertThat(priceLevelSummary.quantity(), is(TEN));
    }


    @Test
    public void should_advise_equality_when_compared_to_identical_price_level_summary() {
        assertThat(aPriceLevelSummary(SUMMARY_ID).adjustBy(TEN),
                is(equalTo(aPriceLevelSummary(SUMMARY_ID).adjustBy(TEN))));
    }

    @Test
    public void should_advise_inequality_when_compared_to_different_price_level_summary() {
        assertThat(aPriceLevelSummary(SUMMARY_ID),
                is(not(equalTo(aPriceLevelSummary(SUMMARY_ID).adjustBy(TEN)))));
    }
}