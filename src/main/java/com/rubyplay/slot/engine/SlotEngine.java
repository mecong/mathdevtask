package com.rubyplay.slot.engine;

import com.rubyplay.slot.evaluator.StandardWinEvaluator;
import com.rubyplay.slot.evaluator.WinEvaluator;
import com.rubyplay.slot.model.GameConfig;
import com.rubyplay.slot.model.Grid;
import com.rubyplay.slot.model.SpinOutcome;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.random.RandomGenerator;

/**
 * Core Slot Engine orchestrating reel spins and win evaluation.
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SlotEngine {

    GameConfig config;
    WinEvaluator evaluator;
    int reelsCount;
    int rowsCount;
    int[] reelLengths;

    public SlotEngine(GameConfig config) {
        this(config, new StandardWinEvaluator());
    }

    public SlotEngine(GameConfig config, WinEvaluator evaluator) {
        if (config == null) {
            throw new IllegalArgumentException("GameConfig must not be null");
        }
        this.config = config;
        this.evaluator = evaluator != null ? evaluator : new StandardWinEvaluator();
        this.reelsCount = config.getReelsCount();
        this.rowsCount = config.getRowsCount();
        this.reelLengths = new int[reelsCount];
        for (int i = 0; i < reelsCount; i++) {
            this.reelLengths[i] = config.getReelsArray()[i].getLength();
        }
    }

    /**
     * Executes a spin with specified reel stop positions.
     *
     * @param stopPositions array of reel stop indices
     * @return evaluated SpinOutcome
     */
    public SpinOutcome spin(int[] stopPositions) {
        Grid grid = Grid.fromStopPositions(config.getReelsArray(), stopPositions, rowsCount);
        return evaluator.evaluate(grid, config, stopPositions);
    }

    /**
     * Executes a single random spin using the provided random generator.
     *
     * @param random source of randomness
     * @return evaluated SpinOutcome
     */
    public SpinOutcome spinRandom(RandomGenerator random) {
        int[] stops = generateRandomStops(random);
        return spin(stops);
    }

    /**
     * High-speed spin evaluation returning only the win amount.
     *
     * @param random source of randomness
     * @return win amount in credits
     */
    public long spinRandomFast(RandomGenerator random) {
        int[] stops = generateRandomStops(random);
        Grid grid = Grid.fromStopPositions(config.getReelsArray(), stops, rowsCount);
        return evaluator.evaluateFastPayout(grid, config);
    }

    /**
     * High-speed evaluation for deterministic stop positions.
     *
     * @param stops reel stop indices
     * @return win amount in credits
     */
    public long evaluateStopsFast(int[] stops) {
        Grid grid = Grid.fromStopPositions(config.getReelsArray(), stops, rowsCount);
        return evaluator.evaluateFastPayout(grid, config);
    }

    private int[] generateRandomStops(RandomGenerator random) {
        int[] stops = new int[reelsCount];
        for (int i = 0; i < reelsCount; i++) {
            stops[i] = random.nextInt(reelLengths[i]);
        }
        return stops;
    }
}
