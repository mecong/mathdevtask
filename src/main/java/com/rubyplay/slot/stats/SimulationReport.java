package com.rubyplay.slot.stats;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.List;

/**
 * Immutable report containing all statistical results of a simulation or exact validation.
 */
@Getter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SimulationReport {

    long totalRounds;
    long totalWagerAmount;
    long totalWinAmount;
    double rtpPercentage;
    double meanWinPerRound;
    long winningRoundsCount;
    double hitFrequencyPercentage;
    double variance;
    double standardDeviationCredits;
    double standardDeviationBetUnits;
    double standardError;
    double ci95Lower;
    double ci95Upper;
    double ci99Lower;
    double ci99Upper;
    long durationMillis;
    double spinsPerSecond;
    List<SymbolStat> symbolBreakdowns;

    public List<SymbolStat> getSymbolBreakdowns() {
        return symbolBreakdowns != null ? Collections.unmodifiableList(symbolBreakdowns) : Collections.emptyList();
    }
}
