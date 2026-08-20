package com.rubyplay.slot.evaluator;

import com.rubyplay.slot.model.GameConfig;
import com.rubyplay.slot.model.Grid;
import com.rubyplay.slot.model.SpinOutcome;

/**
 * Interface for evaluating slot win combinations (paylines, scatters, etc.).
 */
public interface WinEvaluator {

    /**
     * Evaluates a spin grid and produces a detailed outcome report.
     *
     * @param grid          the visible symbol matrix
     * @param config        the game configuration
     * @param stopPositions the reel stop indices
     * @return detailed SpinOutcome
     */
    SpinOutcome evaluate(Grid grid, GameConfig config, int[] stopPositions);

    /**
     * High-speed evaluation that computes only the total win amount.
     * Avoids intermediate object allocations for multi-million iteration simulations.
     *
     * @param grid   the visible symbol matrix
     * @param config the game configuration
     * @return total win amount in credits
     */
    long evaluateFastPayout(Grid grid, GameConfig config);
}
