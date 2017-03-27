package com.silverbars.marketplace.orderboard.web.exception;

import com.silverbars.marketplace.orderboard.common.ErrorResult;
import com.silverbars.marketplace.orderboard.order.exception.OrderNotFoundException;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Produces(MediaType.APPLICATION_JSON)
public class OrderNotFoundExceptionMapper implements ExceptionMapper<OrderNotFoundException> {
    @Override
    public Response toResponse(OrderNotFoundException exception) {
        return Response.status(NOT_FOUND).entity(new ErrorResult(exception.getMessage())).build();
    }
}
