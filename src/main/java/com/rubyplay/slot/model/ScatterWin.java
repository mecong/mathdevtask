package com.rubyplay.slot.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Details of a scatter win payout.
 */
@Getter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScatterWin {

    Symbol symbol;
    int count;
    long payout;
}
