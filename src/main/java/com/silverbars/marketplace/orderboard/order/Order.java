package com.silverbars.marketplace.orderboard.order;

import com.google.common.base.Objects;
import com.silverbars.marketplace.orderboard.common.OrderType;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.collect.Lists.newArrayList;
import static com.silverbars.marketplace.orderboard.order.Order.Builder.anOrder;
import static java.lang.String.format;
import static java.math.BigDecimal.ZERO;

public final class Order {
    private String id;
    private String userId;
    private BigDecimal quantity;
    private BigDecimal priceLevel;
    private OrderType type;

    private Order(String id, OrderType type, String userId, BigDecimal quantity, BigDecimal priceLevel) {
        this.id = id;
        this.type = type;
        this.userId = userId;
        this.quantity = quantity;
        this.priceLevel = priceLevel;
    }

    public String id() {
        return id;
    }

    public String userId() {
        return userId;
    }

    public BigDecimal quantity() {
        return quantity;
    }

    public BigDecimal priceLevel() {
        return priceLevel;
    }

    public OrderType type() {
        return type;
    }

    public List<String> validate() {
        List<String> errors = newArrayList();

        validateId(errors);
        validateType(errors);
        validatePriceLevel(errors);
        validateQuantity(errors);
        validateUserId(errors);

        return errors;
    }

    private void validateType(List<String> errors) {
        if (type == null) {
            errors.add("Order type must be provided");
        }
    }

    private void validatePriceLevel(List<String> errors) {
        if (priceLevel == null) {
            errors.add("Price level must be provided");
        } else if (priceLevel.compareTo(ZERO) != 1) {
            errors.add(format("Price level must be a positive number while %s was provided", priceLevel));
        }
    }

    private void validateQuantity(List<String> errors) {
        if (quantity == null) {
            errors.add("Quantity must be provided");
        } else if (quantity.compareTo(ZERO) != 1) {
            errors.add(format("Quantity must be a positive number while %s was provided", quantity));
        }
    }

    private void validateUserId(List<String> errors) {
        if (userId == null) {
            errors.add("UserId must be provided");
        } else if (userId.isEmpty()) {
            errors.add("UserId must not be blank");
        }
    }

    private void validateId(List<String> errors) {
        if (id == null) {
            errors.add("Id must be provided");
        } else if (id.isEmpty()) {
            errors.add("Id must not be blank");
        }
    }

    public Builder copy() {
        return anOrder().ofType(type).withId(id)
                .withPriceLevel(priceLevel).withQuantity(quantity).forUser(userId);
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("id", id)
                .add("type", type)
                .add("priceLevel", priceLevel)
                .add("quantity", quantity)
                .add("userId", userId)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, type, userId, priceLevel, quantity);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (this.getClass() != other.getClass()) {
            return false;
        }

        final Order that = (Order) other;

        return Objects.equal(this.id, that.id)
                && Objects.equal(this.type, that.type)
                && Objects.equal(this.userId, that.userId)
                && Objects.equal(this.priceLevel, that.priceLevel)
                && Objects.equal(this.quantity, that.quantity);
    }

    public static class Builder {
        private String id;
        private String userId;
        private OrderType type;
        private BigDecimal quantity;
        private BigDecimal priceLevel;

        private Builder() {
        }

        public static Builder anOrder() {
            return new Builder();
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder ofType(OrderType type) {
            this.type = type;
            return this;
        }

        public Builder forUser(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder withQuantity(BigDecimal quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder withPriceLevel(BigDecimal priceLevel) {
            this.priceLevel = priceLevel;
            return this;
        }

        public Order build() {
            return new Order(id, type, userId, quantity, priceLevel);
        }
    }
}
