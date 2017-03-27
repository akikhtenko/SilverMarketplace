package com.silverbars.marketplace.orderboard.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;

public class Retrier {
    private static final Logger LOG = LoggerFactory.getLogger(Retrier.class);
    private int maxRetries;

    public Retrier(int maxRetries) {
        checkArgument(maxRetries >= 0, "maxRetries can't be less than zero");
        this.maxRetries = maxRetries;
    }

    public void doWithConditionalRetry(String operationDescription, Supplier<Boolean> rejectableOperation) {
        int executionCounter = 0;
        boolean accepted = false;
        while (!accepted && executionCounter++ <= maxRetries) {
            LOG.debug("Trying operation [{}]. Attempt #{}", operationDescription, executionCounter);
            accepted = firstNonNull(rejectableOperation.get(), false);
        }

        if (!accepted) {
            throw new RuntimeException(format("Operation [%s] aborted after %s attempts", operationDescription, executionCounter - 1));
        }
    }
}