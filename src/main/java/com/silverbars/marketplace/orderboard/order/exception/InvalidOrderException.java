package com.silverbars.marketplace.orderboard.order.exception;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class InvalidOrderException extends RuntimeException {
    private List<String> reasons;

    public InvalidOrderException(String message, List<String> reasons) {
        super(message);
        this.reasons = requireNonNull(reasons, "reasons must not be null");
    }

    public List<String> reasons() {
        return reasons;
    }
}
