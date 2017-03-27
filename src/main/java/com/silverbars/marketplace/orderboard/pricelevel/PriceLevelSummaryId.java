package com.silverbars.marketplace.orderboard.pricelevel;

import com.google.common.base.Objects;
import com.silverbars.marketplace.orderboard.common.OrderType;

import java.math.BigDecimal;

import static com.google.common.base.MoreObjects.toStringHelper;

public final class PriceLevelSummaryId {
    private BigDecimal priceLevel;
    private OrderType orderType;

    private PriceLevelSummaryId(BigDecimal priceLevel, OrderType orderType) {
        this.priceLevel = priceLevel;
        this.orderType = orderType;
    }

    public static PriceLevelSummaryId from(BigDecimal priceLevel, OrderType type) {
        return new PriceLevelSummaryId(priceLevel, type);
    }

    public BigDecimal priceLevel() {
        return priceLevel;
    }

    public OrderType orderType() {
        return orderType;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("priceLevel", priceLevel)
                .add("orderType", orderType)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(priceLevel, orderType);
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

        final PriceLevelSummaryId that = (PriceLevelSummaryId) other;

        return Objects.equal(this.priceLevel, that.priceLevel)
                && Objects.equal(this.orderType, that.orderType);
    }
}
