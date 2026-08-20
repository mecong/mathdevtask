package com.rubyplay.slot.simulation;

import com.rubyplay.slot.engine.SlotEngine;
import com.rubyplay.slot.model.GameConfig;
import com.rubyplay.slot.model.ReelStrip;
import com.rubyplay.slot.model.SpinOutcome;
import com.rubyplay.slot.stats.SimulationReport;
import com.rubyplay.slot.stats.SimulationStatsAccumulator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

/**
 * Validates the complete combinatorial outcome space (all stop position permutations).
 * Computes exact theoretical RTP, exact hit distributions, and mathematical variance.
 */
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExhaustiveCombinatorialValidator {

    SlotEngine engine;

    /**
     * Executes an exhaustive evaluation of all combinatorial reel stop combinations.
     *
     * @return exact mathematical SimulationReport
     */
    public SimulationReport validateAllCombinations() {
        GameConfig config = engine.getConfig();
        ReelStrip[] reels = config.getReelsArray();

        int len0 = reels[0].getLength();
        int len1 = reels[1].getLength();
        int len2 = reels[2].getLength();
        long totalCombinations = (long) len0 * len1 * len2;
        long betPerRound = config.getDefaultBet();

        log.info("Running exhaustive validation over all {} possible stop combinations ({}x{}x{})",
                totalCombinations, len0, len1, len2);

        long startNanos = System.nanoTime();
        SimulationStatsAccumulator accumulator = new SimulationStatsAccumulator();

        int[] stops = new int[3];
        for (int s0 = 0; s0 < len0; s0++) {
            stops[0] = s0;
            for (int s1 = 0; s1 < len1; s1++) {
                stops[1] = s1;
                for (int s2 = 0; s2 < len2; s2++) {
                    stops[2] = s2;
                    SpinOutcome outcome = engine.spin(stops);
                    accumulator.recordOutcome(outcome, betPerRound);
                }
            }
        }

        long durationNanos = System.nanoTime() - startNanos;
        log.info("Exhaustive validation completed in {} ms", durationNanos / 1_000_000L);

        return accumulator.buildReport(betPerRound, durationNanos);
    }
}
