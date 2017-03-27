package com.silverbars.marketplace.orderboard.common;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RetrierTest {
    private static final int NEVER_SUCCEED = -1;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void should_deny_creating_retries_with_negative_max_retries() {
        exception.expectMessage("maxRetries can't be less than zero");
        new Retrier(-1);
    }

    @Test
    public void should_attempt_operation_once_given_zero_max_retries() {
        exception.expectMessage("Operation [operation] aborted after 1 attempts");

        int maxRetries = 0;
        assertThat(countOperationAttempts(maxRetries, NEVER_SUCCEED), is(1));
    }

    @Test
    public void should_retry_enough_times_before_failing() {
        exception.expectMessage("Operation [operation] aborted after 6 attempts");

        int maxRetries = 5;
        assertThat(countOperationAttempts(maxRetries, NEVER_SUCCEED), is(6));
    }

    @Test
    public void should_retry_enough_times_to_succeed() {
        int successAttempt = 3;
        int maxRetries = successAttempt - 1;
        assertThat(countOperationAttempts(maxRetries, successAttempt), is(successAttempt));
    }

    private int countOperationAttempts(int maxRetries, int successAttempt) {
        AtomicInteger counter = new AtomicInteger(0);
        new Retrier(maxRetries)
                .doWithConditionalRetry("operation", () -> counter.incrementAndGet() == successAttempt);
        return counter.get();
    }
}