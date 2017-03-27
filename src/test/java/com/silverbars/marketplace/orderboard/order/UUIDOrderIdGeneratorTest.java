package com.silverbars.marketplace.orderboard.order;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class UUIDOrderIdGeneratorTest {
    @Test
    public void should_generate_random_unique_order_id() {
        UUIDOrderIdGenerator orderIdGenerator = new UUIDOrderIdGenerator();

        assertThat(orderIdGenerator.generateId(), is(not(equalTo(orderIdGenerator.generateId()))));
    }
}