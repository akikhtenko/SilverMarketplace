package com.silverbars.marketplace.orderboard.ordersummary;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Objects;

import java.math.BigDecimal;

import static com.google.common.base.MoreObjects.toStringHelper;

@JsonDeserialize(builder = PriceLevelSummaryInfo.Builder.class)
public final class PriceLevelSummaryInfo {
    private BigDecimal priceLevel;
    private BigDecimal quantity;

    private PriceLevelSummaryInfo(BigDecimal priceLevel, BigDecimal quantity) {
        this.priceLevel = priceLevel;
        this.quantity = quantity;
    }

    @JsonProperty("priceLevel")
    public BigDecimal priceLevel() {
        return priceLevel;
    }

    @JsonProperty("quantity")
    public BigDecimal quantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("priceLevel", priceLevel)
                .add("quantity", quantity)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(priceLevel, quantity);
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

        final PriceLevelSummaryInfo that = (PriceLevelSummaryInfo) other;

        return Objects.equal(this.priceLevel, that.priceLevel)
                && Objects.equal(this.quantity, that.quantity);
    }

    public static class Builder {
        private BigDecimal priceLevel;
        private BigDecimal quantity;

        public static Builder aSummary() {
            return new Builder();
        }

        @JsonProperty("priceLevel")
        public Builder forPriceLevel(BigDecimal priceLevel) {
            this.priceLevel = priceLevel;
            return this;
        }

        @JsonProperty("quantity")
        public Builder withQuantity(BigDecimal quantity) {
            this.quantity = quantity;
            return this;
        }

        public PriceLevelSummaryInfo build() {
            return new PriceLevelSummaryInfo(priceLevel, quantity);
        }
    }
}
