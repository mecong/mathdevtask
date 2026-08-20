package com.rubyplay.slot.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Result of a single spin outcome including line wins, scatter payouts, and total win.
 */
@Getter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SpinOutcome {

    long totalWin;
    List<LineWin> lineWins;
    ScatterWin scatterWin;
    Grid grid;
    int[] stopPositions;

    public List<LineWin> getLineWins() {
        return lineWins != null ? Collections.unmodifiableList(lineWins) : Collections.emptyList();
    }

    public Optional<ScatterWin> getScatterWin() {
        return Optional.ofNullable(scatterWin);
    }

    public boolean isWin() {
        return totalWin > 0L;
    }
}
