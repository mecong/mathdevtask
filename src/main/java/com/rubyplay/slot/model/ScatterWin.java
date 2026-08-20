package com.rubyplay.slot.model;

import lombok.Builder;

/**
 * Details of a scatter win payout.
 */
@Builder
public record ScatterWin(
        Symbol symbol,
        int count,
        long payout
) {
}
