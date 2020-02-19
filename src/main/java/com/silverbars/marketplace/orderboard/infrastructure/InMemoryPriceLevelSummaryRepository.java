package com.silverbars.marketplace.orderboard.infrastructure;

import com.google.common.collect.Lists;
import com.silverbars.marketplace.orderboard.pricelevel.PriceLevelSummary;
import com.silverbars.marketplace.orderboard.pricelevel.PriceLevelSummaryId;
import com.silverbars.marketplace.orderboard.pricelevel.PriceLevelSummaryRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryPriceLevelSummaryRepository implements PriceLevelSummaryRepository {
    private Map<PriceLevelSummaryId, PriceLevelSummary> priceLevelSummaries = new ConcurrentHashMap<>();

    @Override
    public void store(PriceLevelSummary priceLevelSummary) {
        priceLevelSummaries.put(priceLevelSummary.id(), priceLevelSummary);
    }

    @Override
    public void delete(PriceLevelSummaryId id) {
        priceLevelSummaries.remove(id);
    }

    @Override
    public Optional<PriceLevelSummary> load(PriceLevelSummaryId priceLevelSummaryId) {
        return Optional.ofNullable(priceLevelSummaries.get(priceLevelSummaryId));
    }

    @Override
    public List<PriceLevelSummary> all() {
        return Lists.newArrayList(priceLevelSummaries.values());
    }
}
