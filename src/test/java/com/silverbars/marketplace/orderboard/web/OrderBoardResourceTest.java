package com.silverbars.marketplace.orderboard.web;

import com.silverbars.marketplace.orderboard.common.ErrorResult;
import com.silverbars.marketplace.orderboard.order.OrderManagementService;
import com.silverbars.marketplace.orderboard.order.RegisterOrder;
import com.silverbars.marketplace.orderboard.ordersummary.OrderSummaryInfo;
import com.silverbars.marketplace.orderboard.ordersummary.OrderSummaryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.core.Response;

import static com.silverbars.marketplace.orderboard.order.RegisterOrder.Builder.aRegisterOrderCommand;
import static com.silverbars.marketplace.orderboard.ordersummary.OrderSummaryInfo.Builder.anOrderSummary;
import static com.silverbars.marketplace.orderboard.ordersummary.PriceLevelSummaryInfo.Builder.aSummary;
import static java.math.BigDecimal.TEN;
import static java.util.Collections.singleton;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderBoardResourceTest {
    private static final String ORDER_ID = "orderId";
    @Mock
    private OrderManagementService orderManagementService;
    @Mock
    private OrderSummaryService orderSummaryService;
    @InjectMocks
    private OrderBoardResource orderBoardResource;

    @Test
    public void should_report_http_bad_request_when_registered_order_ommitted() {
        Response response = orderBoardResource.registerOrder(null);

        assertThat(response.getStatus(), is(BAD_REQUEST.getStatusCode()));
        assertThat(response.getEntity(), is(new ErrorResult("Missing order details")));
    }

    @Test
    public void should_delegate_to_service_and_report_order_id_with_http_ok_when_registering_order() {
        RegisterOrder registerOrderCommand = aRegisterOrderCommand().forUser("wbuffett").build();
        when(orderManagementService.registerOrder(registerOrderCommand)).thenReturn(ORDER_ID);

        Response response = orderBoardResource.registerOrder(registerOrderCommand);

        assertThat(response.getStatus(), is(OK.getStatusCode()));
        assertThat(response.getEntity(), is(ORDER_ID));
    }

    @Test
    public void should_report_http_bad_request_when_cancelled_order_id_ommitted() {
        Response response = orderBoardResource.cancelOrder(null);

        assertThat(response.getStatus(), is(BAD_REQUEST.getStatusCode()));
        assertThat(response.getEntity(), is(new ErrorResult("Missing orderId to cancel")));
    }

    @Test
    public void should_delegate_to_service_and_report_http_ok_when_cancelling_order() {
        Response response = orderBoardResource.cancelOrder(ORDER_ID);

        assertThat(response.getStatus(), is(OK.getStatusCode()));
        verify(orderManagementService).cancelOrder(ORDER_ID);
    }

    @Test
    public void should_delegate_to_service_and_report_result_with_http_ok_when_requesting_summary() {
        OrderSummaryInfo resultantSummary =
                anOrderSummary().withBuySideSummaries(singleton(aSummary().forPriceLevel(TEN).build())).build();
        when(orderSummaryService.summariseOrders()).thenReturn(resultantSummary);

        Response response = orderBoardResource.summariseOrders();

        assertThat(response.getStatus(), is(OK.getStatusCode()));
        assertThat(response.getEntity(), is(resultantSummary));
    }
}