package com.silverbars.marketplace.orderboard.pricelevel;

import com.google.common.base.Objects;

import java.math.BigDecimal;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.math.BigDecimal.ZERO;

public final class PriceLevelSummary {
    private PriceLevelSummaryId id;
    private BigDecimal quantity;

    private PriceLevelSummary(PriceLevelSummaryId id, BigDecimal quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public static PriceLevelSummary aPriceLevelSummary(PriceLevelSummaryId id) {
        return new PriceLevelSummary(id, ZERO);
    }

    public PriceLevelSummaryId id() {
        return id;
    }

    public BigDecimal quantity() {
        return quantity;
    }

    public PriceLevelSummary adjustBy(BigDecimal deltaQuantity) {
        return new PriceLevelSummary(id, quantity.add(deltaQuantity));
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("id", id)
                .add("quantity", quantity)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, quantity);
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

        final PriceLevelSummary that = (PriceLevelSummary) other;

        return Objects.equal(this.id, that.id)
                && Objects.equal(this.quantity, that.quantity);
    }
}
