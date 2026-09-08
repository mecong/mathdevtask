package com.rubyplay.slot.simulation;

import com.rubyplay.slot.config.AppProperties;
import com.rubyplay.slot.engine.SlotEngine;
import com.rubyplay.slot.model.GameConfig;
import com.rubyplay.slot.model.SpinOutcome;
import com.rubyplay.slot.stats.SimulationReport;
import com.rubyplay.slot.stats.SimulationStatsAccumulator;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

/**
 * High-performance Monte Carlo simulation runner.
 * Leverages Java Virtual Threads for scalable, lock-free parallel execution
 * and thread-local statistical accumulation.
 */
@Slf4j
public record VirtualThreadMonteCarloSimulator(SlotEngine engine, AppProperties properties) implements Simulator {

    private static final RandomGeneratorFactory<RandomGenerator.SplittableGenerator> RNG_FACTORY =
            RandomGeneratorFactory.of("L64X128MixRandom");

    public VirtualThreadMonteCarloSimulator(SlotEngine engine) {
        this(engine, new AppProperties());
    }

    @Override
    public SimulationReport runSimulation(long totalRounds) {
        long seed = System.currentTimeMillis() ^ System.nanoTime();
        return runSimulation(totalRounds, seed);
    }

    /**
     * Executes the simulation with a specified seed for reproducibility.
     *
     * @param totalRounds total number of rounds to simulate
     * @param seed        seed to initialize the root SplittableGenerator
     * @return SimulationReport with aggregated results
     */
    public SimulationReport runSimulation(long totalRounds, long seed) {
        if (totalRounds <= 0) {
            throw new IllegalArgumentException("Total rounds must be positive: " + totalRounds);
        }

        GameConfig config = engine.getConfig();
        long betPerRound = config.getDefaultBet();
        boolean useVirtualThreads = properties.getSimulationSettings().isUseVirtualThreads();
        int parallelism = properties.getSimulationSettings().getThreadPoolSize();
        int batchSize = properties.getSimulationSettings().getBatchSize();

        log.info(
                "Starting Monte Carlo simulation of {} rounds (VirtualThreads={}, Parallelism={}, BatchSize={}, Seed={})",
                totalRounds, useVirtualThreads, parallelism, batchSize, seed);

        ExecutorService executor = useVirtualThreads
                                   ? Executors.newVirtualThreadPerTaskExecutor()
                                   : Executors.newFixedThreadPool(parallelism);

        long startNanos = System.nanoTime();

        try (executor) {
            long remaining = totalRounds;
            List<Future<SimulationStatsAccumulator>> futures = new ArrayList<>();
            RandomGenerator.SplittableGenerator rootRng = RNG_FACTORY.create(seed);

            while (remaining > 0) {
                long chunkRounds = Math.min(remaining, batchSize);
                remaining -= chunkRounds;
                RandomGenerator chunkRng = rootRng.split();

                Callable<SimulationStatsAccumulator> task = () -> executeChunk(chunkRounds, chunkRng, betPerRound);
                futures.add(executor.submit(task));
            }

            // Aggregate all chunk results
            SimulationStatsAccumulator globalAccumulator = new SimulationStatsAccumulator();
            for (Future<SimulationStatsAccumulator> future : futures) {
                globalAccumulator.combine(future.get());
            }

            long durationNanos = System.nanoTime() - startNanos;
            log.info("Simulation completed in {} ms", durationNanos / 1_000_000L);

            return globalAccumulator.buildReport(betPerRound, durationNanos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Simulation interrupted: {}", e.getMessage(), e);
            throw new RuntimeException("Simulation execution interrupted", e);
        } catch (Exception e) {
            log.error("Simulation failed: {}", e.getMessage(), e);
            throw new RuntimeException("Simulation execution failed", e);
        }
    }

    /**
     * Executes a single batch chunk of spins.
     */
    private SimulationStatsAccumulator executeChunk(long chunkRounds, RandomGenerator rng, long betPerRound) {
        SimulationStatsAccumulator accumulator = new SimulationStatsAccumulator();

        boolean detailedTracking = properties.getReportSettings().isShowDetailedSymbolBreakdown();

        if (detailedTracking) {
            for (long i = 0; i < chunkRounds; i++) {
                SpinOutcome outcome = engine.spinRandom(rng);
                accumulator.recordOutcome(outcome, betPerRound);
            }
        } else {
            for (long i = 0; i < chunkRounds; i++) {
                long win = engine.spinRandomFast(rng);
                accumulator.recordRound(win, betPerRound);
            }
        }

        return accumulator;
    }
}
