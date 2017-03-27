package com.silverbars.marketplace.orderboard.ordersummary;

import com.silverbars.marketplace.orderboard.pricelevel.PriceLevelSummary;
import com.silverbars.marketplace.orderboard.pricelevel.PriceLevelSummaryId;
import com.silverbars.marketplace.orderboard.pricelevel.PriceLevelSummaryRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static com.silverbars.marketplace.orderboard.common.OrderType.BUY;
import static com.silverbars.marketplace.orderboard.common.OrderType.SELL;
import static com.silverbars.marketplace.orderboard.ordersummary.OrderSummaryInfo.Builder.anOrderSummary;
import static com.silverbars.marketplace.orderboard.ordersummary.PriceLevelSummaryInfo.Builder.aSummary;
import static com.silverbars.marketplace.orderboard.pricelevel.PriceLevelSummary.aPriceLevelSummary;
import static java.math.BigDecimal.ONE;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderSummaryServiceTest {
    private static final BigDecimal TWO = new BigDecimal(2);
    private static final BigDecimal THREE = new BigDecimal(3);

    @Mock
    private PriceLevelSummaryRepository priceLevelSummaryRepository;
    @InjectMocks
    private OrderSummaryService orderSummaryService;

    @Test
    public void should_advise_trivial_summary_when_board_has_no_registered_orders() {
        when(priceLevelSummaryRepository.all()).thenReturn(emptyList());

        assertThat(orderSummaryService.summariseOrders(), is(
                anOrderSummary().withSellSideSummaries(emptyList()).withBuySideSummaries(emptyList()).build()));
    }

    @Test
    public void should_collate_summary_of_registered_orders() {
        PriceLevelSummary BUY_LEVEL_ONE = aPriceLevelSummary(PriceLevelSummaryId.from(ONE, BUY)).adjustBy(new BigDecimal(37));
        PriceLevelSummary BUY_LEVEL_TWO = aPriceLevelSummary(PriceLevelSummaryId.from(TWO, BUY)).adjustBy(new BigDecimal(12));
        PriceLevelSummary BUY_LEVEL_THREE = aPriceLevelSummary(PriceLevelSummaryId.from(THREE, BUY)).adjustBy(new BigDecimal(9));
        PriceLevelSummary SELL_LEVEL_ONE = aPriceLevelSummary(PriceLevelSummaryId.from(ONE, SELL)).adjustBy(new BigDecimal(50));
        PriceLevelSummary SELL_LEVEL_TWO = aPriceLevelSummary(PriceLevelSummaryId.from(TWO, SELL)).adjustBy(new BigDecimal(21));

        when(priceLevelSummaryRepository.all()).thenReturn(
                asList(SELL_LEVEL_ONE, BUY_LEVEL_TWO, BUY_LEVEL_ONE, SELL_LEVEL_TWO, BUY_LEVEL_THREE));

        assertThat(orderSummaryService.summariseOrders(), is(
                anOrderSummary()
                        .withSellSideSummaries(asList(
                                aSummary().forPriceLevel(ONE).withQuantity(SELL_LEVEL_ONE.quantity()).build(),
                                aSummary().forPriceLevel(TWO).withQuantity(SELL_LEVEL_TWO.quantity()).build()
                        )).withBuySideSummaries(asList(
                                aSummary().forPriceLevel(THREE).withQuantity(BUY_LEVEL_THREE.quantity()).build(),
                                aSummary().forPriceLevel(TWO).withQuantity(BUY_LEVEL_TWO.quantity()).build(),
                                aSummary().forPriceLevel(ONE).withQuantity(BUY_LEVEL_ONE.quantity()).build()
                )).build()));
    }
}