package com.silverbars.marketplace.orderboard.web.exception;

import com.silverbars.marketplace.orderboard.common.ErrorResult;
import com.silverbars.marketplace.orderboard.order.exception.InvalidOrderException;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static java.util.Arrays.asList;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class InvalidOrderExceptionMapperTest {
    @Test
    public void should_map_invalid_order_details_into_http_bad_request_response() {
        Response response = new InvalidOrderExceptionMapper().toResponse(
                new InvalidOrderException("Failure message", asList("reasonOne", "reasonTwo")));

        assertThat(response.getStatus(), is(BAD_REQUEST.getStatusCode()));
        assertThat(response.getEntity(), is(
                new ErrorResult("Failure message", "reasonOne, reasonTwo")));
    }
}