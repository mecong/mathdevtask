package com.rubyplay.slot.stats;

import com.rubyplay.slot.model.Symbol;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Statistical summary for an individual winning symbol.
 */
@Getter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SymbolStat {

    Symbol symbol;
    long hitCount;
    long totalPayout;
    double probability;
    double returnPercentage;
}
