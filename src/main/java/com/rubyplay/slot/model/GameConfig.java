package com.rubyplay.slot.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.List;

/**
 * Immutable master configuration for a slot game.
 */
@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GameConfig {

    String gameId;
    String name;
    int reelsCount;
    int rowsCount;
    long defaultBet;
    List<ReelStrip> reels;
    List<Payline> paylines;
    PayTable payTable;

    // Pre-cached arrays for zero-allocation performance in hot loops
    ReelStrip[] reelsArray;
    Payline[] paylinesArray;

    @Builder
    public GameConfig(String gameId,
                      String name,
                      int reelsCount,
                      int rowsCount,
                      long defaultBet,
                      List<ReelStrip> reels,
                      List<Payline> paylines,
                      PayTable payTable) {
        this.gameId = gameId;
        this.name = name;
        this.reelsCount = reelsCount;
        this.rowsCount = rowsCount;
        this.defaultBet = defaultBet;
        this.reels = reels != null ? List.copyOf(reels) : Collections.emptyList();
        this.paylines = paylines != null ? List.copyOf(paylines) : Collections.emptyList();
        this.payTable = payTable;
        this.reelsArray = this.reels.toArray(new ReelStrip[0]);
        this.paylinesArray = this.paylines.toArray(new Payline[0]);
    }
}
