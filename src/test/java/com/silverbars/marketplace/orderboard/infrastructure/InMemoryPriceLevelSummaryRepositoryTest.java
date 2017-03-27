package com.silverbars.marketplace.orderboard.infrastructure;

import com.silverbars.marketplace.orderboard.pricelevel.PriceLevelSummary;
import com.silverbars.marketplace.orderboard.pricelevel.PriceLevelSummaryId;
import org.junit.Test;

import java.util.Optional;

import static com.silverbars.marketplace.orderboard.common.OrderType.BUY;
import static com.silverbars.marketplace.orderboard.common.OrderType.SELL;
import static com.silverbars.marketplace.orderboard.pricelevel.PriceLevelSummary.aPriceLevelSummary;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.util.Optional.empty;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class InMemoryPriceLevelSummaryRepositoryTest {
    private InMemoryPriceLevelSummaryRepository inMemoryPriceLevelSummaryRepository = new InMemoryPriceLevelSummaryRepository();

    @Test
    public void should_advise_empty_result_when_loading_summary_by_unseen_id() {
        assertThat(inMemoryPriceLevelSummaryRepository.load(PriceLevelSummaryId.from(ONE, BUY)), is(empty()));
    }

    @Test
    public void should_load_previously_stored_summary() {
        PriceLevelSummaryId summaryId = PriceLevelSummaryId.from(ONE, BUY);
        PriceLevelSummary summary = aPriceLevelSummary(summaryId).adjustBy(TEN);

        inMemoryPriceLevelSummaryRepository.store(summary);

        assertThat(inMemoryPriceLevelSummaryRepository.load(summaryId), is(Optional.of(summary)));
    }

    @Test
    public void should_load_all_previously_stored_summaries() {
        PriceLevelSummary summaryOne = aPriceLevelSummary(PriceLevelSummaryId.from(ONE, BUY)).adjustBy(TEN);
        PriceLevelSummary summaryTwo = aPriceLevelSummary(PriceLevelSummaryId.from(ONE, SELL)).adjustBy(TEN);
        PriceLevelSummary summaryThree = aPriceLevelSummary(PriceLevelSummaryId.from(TEN, BUY)).adjustBy(TEN);

        inMemoryPriceLevelSummaryRepository.store(summaryOne);
        inMemoryPriceLevelSummaryRepository.store(summaryTwo);
        inMemoryPriceLevelSummaryRepository.store(summaryThree);

        assertThat(inMemoryPriceLevelSummaryRepository.all(), containsInAnyOrder(summaryOne, summaryTwo, summaryThree));
    }
}