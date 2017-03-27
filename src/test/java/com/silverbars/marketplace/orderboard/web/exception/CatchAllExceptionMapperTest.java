package com.silverbars.marketplace.orderboard.web.exception;

import com.silverbars.marketplace.orderboard.common.ErrorResult;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CatchAllExceptionMapperTest {
    @Test
    public void should_map_unexpected_exception_into_http_internal_error_response_hiding_exception_message() {
        Response response = new CatchAllExceptionMapper().toResponse(new RuntimeException("Failure message"));

        assertThat(response.getStatus(), is(INTERNAL_SERVER_ERROR.getStatusCode()));
        assertThat(response.getEntity(), is(new ErrorResult("Sorry, something is broken. We'll look into it.")));
    }
}