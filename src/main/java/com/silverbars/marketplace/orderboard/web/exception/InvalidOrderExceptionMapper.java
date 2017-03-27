package com.silverbars.marketplace.orderboard.web.exception;

import com.silverbars.marketplace.orderboard.common.ErrorResult;
import com.silverbars.marketplace.orderboard.order.exception.InvalidOrderException;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static java.util.stream.Collectors.joining;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Produces(MediaType.APPLICATION_JSON)
public class InvalidOrderExceptionMapper implements ExceptionMapper<InvalidOrderException> {
    @Override
    public Response toResponse(InvalidOrderException exception) {
        String invalidityDetails = exception.reasons().stream().collect(joining(", "));
        return Response
                .status(BAD_REQUEST)
                .entity(new ErrorResult(exception.getMessage(), invalidityDetails))
                .build();
    }
}
