package com.silverbars.marketplace.orderboard.order.exception;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class InvalidOrderExceptionTest {
    @Test
    public void should_construct_well_formed_exception_object() {
        List<String> reasons = asList("reasonOne", "reasonTwo");
        InvalidOrderException exception = new InvalidOrderException("Failure", reasons);

        assertThat(exception.getMessage(), is("Failure"));
        assertThat(exception.reasons(), is(reasons));
    }
}