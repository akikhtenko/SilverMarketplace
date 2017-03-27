package com.silverbars.marketplace.orderboard.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.UUID.randomUUID;

public class UUIDOrderIdGenerator implements OrderIdGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(UUIDOrderIdGenerator.class);

    @Override
    public String generateId() {
        String generatedOrderId = randomUUID().toString();

        LOG.debug("Generated new order id {}", generatedOrderId);
        return generatedOrderId;
    }
}
