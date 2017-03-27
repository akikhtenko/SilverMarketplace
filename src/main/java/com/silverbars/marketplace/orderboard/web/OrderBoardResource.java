package com.silverbars.marketplace.orderboard.web;

import com.silverbars.marketplace.orderboard.common.ErrorResult;
import com.silverbars.marketplace.orderboard.order.OrderManagementService;
import com.silverbars.marketplace.orderboard.order.RegisterOrder;
import com.silverbars.marketplace.orderboard.ordersummary.OrderSummaryService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.ok;

@Path("/board")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OrderBoardResource {
    private OrderManagementService orderManagementService;
    private OrderSummaryService orderSummaryService;

    @Autowired
    public OrderBoardResource(OrderManagementService orderManagementService, OrderSummaryService orderSummaryService) {
        this.orderManagementService = orderManagementService;
        this.orderSummaryService = orderSummaryService;
    }

    @GET
    @Path("/orders/summary")
    public Response summariseOrders() {
        return ok(orderSummaryService.summariseOrders()).build();
    }

    @POST
    @Path("/orders")
    public Response registerOrder(RegisterOrder command) {
        return command == null
                ? Response.status(BAD_REQUEST).entity(new ErrorResult("Missing order details")).build()
                : ok(orderManagementService.registerOrder(command)).build();
    }

    @DELETE
    @Path("/orders/{orderId}")
    public Response cancelOrder(@PathParam("orderId") String orderId) {
        if (orderId == null) {
            return Response.status(BAD_REQUEST).entity(new ErrorResult("Missing orderId to cancel")).build();
        }

        orderManagementService.cancelOrder(orderId);
        return ok().build();
    }
}