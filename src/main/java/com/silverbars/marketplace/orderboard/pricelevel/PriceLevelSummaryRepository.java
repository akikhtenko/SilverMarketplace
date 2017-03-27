package com.silverbars.marketplace.orderboard.pricelevel;

import java.util.List;
import java.util.Optional;

public interface PriceLevelSummaryRepository {
    void store(PriceLevelSummary priceLevelSummary);
    Optional<PriceLevelSummary> load(PriceLevelSummaryId priceLevelSummaryId);
    List<PriceLevelSummary> all();
}
