package com.silverbars.marketplace.orderboard.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Objects;
import com.silverbars.marketplace.orderboard.common.OrderType;

import java.math.BigDecimal;

import static com.google.common.base.MoreObjects.toStringHelper;

@JsonDeserialize(builder = RegisterOrder.Builder.class)
public final class RegisterOrder {
    private String userId;
    private BigDecimal quantity;
    private BigDecimal priceLevel;
    private OrderType type;

    private RegisterOrder(OrderType type, String userId, BigDecimal quantity, BigDecimal priceLevel) {
        this.type = type;
        this.userId = userId;
        this.quantity = quantity;
        this.priceLevel = priceLevel;
    }

    @JsonProperty("userId")
    public String userId() {
        return userId;
    }

    @JsonProperty("quantity")
    public BigDecimal quantity() {
        return quantity;
    }

    @JsonProperty("priceLevel")
    public BigDecimal priceLevel() {
        return priceLevel;
    }

    @JsonProperty("type")
    public OrderType type() {
        return type;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("type", type)
                .add("priceLevel", priceLevel)
                .add("quantity", quantity)
                .add("userId", userId)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type, userId, priceLevel, quantity);
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

        final RegisterOrder that = (RegisterOrder) other;

        return Objects.equal(this.type, that.type)
                && Objects.equal(this.userId, that.userId)
                && Objects.equal(this.priceLevel, that.priceLevel)
                && Objects.equal(this.quantity, that.quantity);
    }

    public static class Builder {
        private String userId;
        private OrderType type;
        private BigDecimal quantity;
        private BigDecimal priceLevel;

        private Builder() {
        }

        public static Builder aRegisterOrderCommand() {
            return new Builder();
        }

        @JsonProperty("type")
        public Builder ofType(OrderType type) {
            this.type = type;
            return this;
        }

        @JsonProperty("userId")
        public Builder forUser(String userId) {
            this.userId = userId;
            return this;
        }

        @JsonProperty("quantity")
        public Builder withQuantity(BigDecimal quantity) {
            this.quantity = quantity;
            return this;
        }

        @JsonProperty("priceLevel")
        public Builder withPriceLevel(BigDecimal priceLevel) {
            this.priceLevel = priceLevel;
            return this;
        }

        public RegisterOrder build() {
            return new RegisterOrder(type, userId, quantity, priceLevel);
        }
    }
}
