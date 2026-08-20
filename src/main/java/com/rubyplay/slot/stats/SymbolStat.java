package com.rubyplay.slot.stats;

import com.rubyplay.slot.model.Symbol;
import lombok.Builder;

/**
 * Statistical summary for an individual winning symbol.
 */
@Builder
public record SymbolStat(
        Symbol symbol,
        long hitCount,
        long totalPayout,
        double probability,
        double returnPercentage
) {
}
