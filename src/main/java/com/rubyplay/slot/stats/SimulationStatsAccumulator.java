package com.rubyplay.slot.stats;

import com.rubyplay.slot.model.LineWin;
import com.rubyplay.slot.model.SpinOutcome;
import com.rubyplay.slot.model.Symbol;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

/**
 * Mutable statistical accumulator that collects spin data and computes comprehensive metrics.
 * Designed for efficient thread-local accumulation and subsequent reduction.
 */
@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SimulationStatsAccumulator {

    private static final double Z_95 = 1.959963984540054; // 95% Confidence Interval Z-score
    private static final double Z_99 = 2.5758293035489004; // 99% Confidence Interval Z-score

    long totalRounds;
    long totalWagerAmount;
    long totalWinAmount;
    long winningRoundsCount;
    final long[] symbolHits;
    final long[] symbolPayouts;
    final OnlineVarianceAccumulator varianceAccumulator;

    public SimulationStatsAccumulator() {
        int symbolCount = Symbol.values().length;
        this.symbolHits = new long[symbolCount];
        this.symbolPayouts = new long[symbolCount];
        this.varianceAccumulator = new OnlineVarianceAccumulator();
    }

    /**
     * Fast record for high-speed simulation loop where only round payout is collected.
     *
     * @param winAmount payout in credits for the round
     * @param betAmount wager amount in credits
     */
    public void recordRound(long winAmount, long betAmount) {
        totalRounds++;
        totalWagerAmount += betAmount;
        totalWinAmount += winAmount;
        if (winAmount > 0) {
            winningRoundsCount++;
        }
        varianceAccumulator.add(winAmount);
    }

    /**
     * Detailed record including individual symbol breakdown.
     *
     * @param outcome   evaluated spin outcome
     * @param betAmount wager amount in credits
     */
    public void recordOutcome(SpinOutcome outcome, long betAmount) {
        long winAmount = outcome.getTotalWin();
        recordRound(winAmount, betAmount);

        for (LineWin lineWin : outcome.getLineWins()) {
            int ordinal = lineWin.getWinningSymbol().ordinal();
            symbolHits[ordinal]++;
            symbolPayouts[ordinal] += lineWin.getPayout();
        }

        outcome.getScatterWin().ifPresent(scatterWin -> {
            int ordinal = scatterWin.getSymbol().ordinal();
            symbolHits[ordinal]++;
            symbolPayouts[ordinal] += scatterWin.getPayout();
        });
    }

    /**
     * Merges another accumulator into this instance.
     *
     * @param other the accumulator to combine
     */
    public void combine(SimulationStatsAccumulator other) {
        if (other == null || other.totalRounds == 0) {
            return;
        }
        this.totalRounds += other.totalRounds;
        this.totalWagerAmount += other.totalWagerAmount;
        this.totalWinAmount += other.totalWinAmount;
        this.winningRoundsCount += other.winningRoundsCount;

        for (int i = 0; i < symbolHits.length; i++) {
            this.symbolHits[i] += other.symbolHits[i];
            this.symbolPayouts[i] += other.symbolPayouts[i];
        }

        this.varianceAccumulator.combine(other.varianceAccumulator);
    }

    /**
     * Builds an immutable SimulationReport summarizing the statistics.
     *
     * @param betPerRound   cost of a single round
     * @param durationNanos execution time in nanoseconds
     * @return populated SimulationReport
     */
    public SimulationReport buildReport(long betPerRound, long durationNanos) {
        if (totalRounds == 0) {
            return SimulationReport.builder().build();
        }

        double rtpPercentage = totalWagerAmount > 0
                ? ((double) totalWinAmount / totalWagerAmount) * 100.0
                : 0.0;

        double meanWinPerRound = (double) totalWinAmount / totalRounds;
        double hitFrequencyPercentage = ((double) winningRoundsCount / totalRounds) * 100.0;

        double sampleVariance = varianceAccumulator.getSampleVariance();
        double sampleStdDevCredits = Math.sqrt(sampleVariance);
        double sampleStdDevBetUnits = betPerRound > 0 ? sampleStdDevCredits / betPerRound : 0.0;

        // Standard Error of Mean Win = s / sqrt(N)
        double standardError = sampleStdDevCredits / Math.sqrt(totalRounds);
        // Standard Error of RTP (%) = (SE / Bet) * 100
        double seRtp = betPerRound > 0 ? (standardError / betPerRound) * 100.0 : 0.0;

        double ci95Lower = rtpPercentage - (Z_95 * seRtp);
        double ci95Upper = rtpPercentage + (Z_95 * seRtp);

        double ci99Lower = rtpPercentage - (Z_99 * seRtp);
        double ci99Upper = rtpPercentage + (Z_99 * seRtp);

        long durationMillis = durationNanos / 1_000_000L;
        double seconds = durationNanos / 1_000_000_000.0;
        double spinsPerSecond = seconds > 0 ? totalRounds / seconds : 0.0;

        List<SymbolStat> symbolBreakdowns = new ArrayList<>();
        Symbol[] allSymbols = Symbol.values();
        for (Symbol sym : allSymbols) {
            int ordinal = sym.ordinal();
            long hits = symbolHits[ordinal];
            long payout = symbolPayouts[ordinal];
            double prob = (double) hits / totalRounds;
            double retPct = totalWagerAmount > 0 ? ((double) payout / totalWagerAmount) * 100.0 : 0.0;

            symbolBreakdowns.add(SymbolStat.builder()
                    .symbol(sym)
                    .hitCount(hits)
                    .totalPayout(payout)
                    .probability(prob)
                    .returnPercentage(retPct)
                    .build());
        }

        return SimulationReport.builder()
                .totalRounds(totalRounds)
                .totalWagerAmount(totalWagerAmount)
                .totalWinAmount(totalWinAmount)
                .rtpPercentage(rtpPercentage)
                .meanWinPerRound(meanWinPerRound)
                .winningRoundsCount(winningRoundsCount)
                .hitFrequencyPercentage(hitFrequencyPercentage)
                .variance(sampleVariance)
                .standardDeviationCredits(sampleStdDevCredits)
                .standardDeviationBetUnits(sampleStdDevBetUnits)
                .standardError(standardError)
                .ci95Lower(ci95Lower)
                .ci95Upper(ci95Upper)
                .ci99Lower(ci99Lower)
                .ci99Upper(ci99Upper)
                .durationMillis(durationMillis)
                .spinsPerSecond(spinsPerSecond)
                .symbolBreakdowns(symbolBreakdowns)
                .build();
    }
}
