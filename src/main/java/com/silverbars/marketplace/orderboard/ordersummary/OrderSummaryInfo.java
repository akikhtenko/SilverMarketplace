package com.silverbars.marketplace.orderboard.ordersummary;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Objects;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableList;

@JsonDeserialize(builder = OrderSummaryInfo.Builder.class)
public final class OrderSummaryInfo {
    private List<PriceLevelSummaryInfo> sellSideSummaries;
    private List<PriceLevelSummaryInfo> buySideSummaries;

    private OrderSummaryInfo(List<PriceLevelSummaryInfo> sellSideSummaries, List<PriceLevelSummaryInfo> buySideSummaries) {
        this.sellSideSummaries = sellSideSummaries;
        this.buySideSummaries = buySideSummaries;
    }

    @JsonProperty("sellSide")
    public List<PriceLevelSummaryInfo> sellSideSummaries() {
        return unmodifiableList(sellSideSummaries);
    }

    @JsonProperty("buySide")
    public List<PriceLevelSummaryInfo> buySideSummaries() {
        return unmodifiableList(buySideSummaries);
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("sellSideSummaries", sellSideSummaries)
                .add("buySideSummaries", buySideSummaries)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sellSideSummaries, buySideSummaries);
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

        final OrderSummaryInfo that = (OrderSummaryInfo) other;

        return Objects.equal(this.sellSideSummaries, that.sellSideSummaries)
                && Objects.equal(this.buySideSummaries, that.buySideSummaries);
    }

    public static class Builder {
        private List<PriceLevelSummaryInfo> sellSideSummaries;
        private List<PriceLevelSummaryInfo> buySideSummaries;

        public static Builder anOrderSummary() {
            return new Builder();
        }

        @JsonProperty("sellSide")
        public Builder withSellSideSummaries(Collection<PriceLevelSummaryInfo> sellSideSummaries) {
            this.sellSideSummaries = newArrayList(sellSideSummaries);
            return this;
        }

        @JsonProperty("buySide")
        public Builder withBuySideSummaries(Collection<PriceLevelSummaryInfo> buySideSummaries) {
            this.buySideSummaries = newArrayList(buySideSummaries);
            return this;
        }

        public OrderSummaryInfo build() {
            return new OrderSummaryInfo(sellSideSummaries, buySideSummaries);
        }
    }
}
