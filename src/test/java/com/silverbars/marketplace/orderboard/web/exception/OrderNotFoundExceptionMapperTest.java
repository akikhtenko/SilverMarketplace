package com.silverbars.marketplace.orderboard.web.exception;

import com.silverbars.marketplace.orderboard.common.ErrorResult;
import com.silverbars.marketplace.orderboard.order.exception.OrderNotFoundException;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class OrderNotFoundExceptionMapperTest {
    @Test
    public void should_map_unexpected_exception_into_http_not_found_response() {
        Response response = new OrderNotFoundExceptionMapper().toResponse(new OrderNotFoundException("Failure message"));

        assertThat(response.getStatus(), is(NOT_FOUND.getStatusCode()));
        assertThat(response.getEntity(), is(new ErrorResult("Failure message")));
    }
}