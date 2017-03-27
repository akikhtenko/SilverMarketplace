package com.silverbars.marketplace.orderboard.order.exception;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class OrderNotFoundExceptionTest {
    @Test
    public void should_advise_exception_message() {
        assertThat(new OrderNotFoundException("Failure").getMessage(), is("Failure"));
    }
}