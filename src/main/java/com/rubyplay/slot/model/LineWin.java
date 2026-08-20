package com.rubyplay.slot.model;

import lombok.Builder;

/**
 * Details of a winning combination on a specific payline.
 */
@Builder
public record LineWin(
        int paylineId,
        Symbol winningSymbol,
        int matchCount,
        long payout,
        boolean wildSubstituted
) {
}
