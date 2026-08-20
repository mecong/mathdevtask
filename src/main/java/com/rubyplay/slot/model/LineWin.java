package com.rubyplay.slot.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Details of a winning combination on a specific payline.
 */
@Getter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LineWin {

    int paylineId;
    Symbol winningSymbol;
    int matchCount;
    long payout;
    boolean wildSubstituted;
}
