package com.rubyplay.slot.cli;

import com.rubyplay.slot.config.AppProperties;
import com.rubyplay.slot.config.GameConfigLoader;
import com.rubyplay.slot.engine.SlotEngine;
import com.rubyplay.slot.model.GameConfig;
import com.rubyplay.slot.simulation.ExhaustiveCombinatorialValidator;
import com.rubyplay.slot.simulation.VirtualThreadMonteCarloSimulator;
import com.rubyplay.slot.stats.ReportFormatter;
import com.rubyplay.slot.stats.SimulationReport;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Path;
import java.util.concurrent.Callable;

/**
 * Command-line interface for executing slot game simulations and mathematical validations.
 */
@Slf4j
@Command(
        name = "slot-simulator",
        mixinStandardHelpOptions = true,
        version = "1.0.0",
        description = "High-performance 3x3 slot game simulator and statistical validator."
)
public class SlotSimulationCommand implements Callable<Integer> {

    @Option(
            names = {"-r", "--rounds"},
            description = "Number of rounds to simulate (default: 10,000,000).",
            defaultValue = "10000000"
    )
    private long rounds = 10_000_000L;

    @Option(
            names = {"-t", "--threads"},
            description = "Number of worker threads (default: available CPU cores)."
    )
    private Integer threads;

    @Option(
            names = {"-v", "--virtual-threads"},
            negatable = true,
            description = "Use Java Virtual Threads (default: true). Use --no-virtual-threads for platform threads.",
            defaultValue = "true"
    )
    private boolean virtualThreads = true;

    @Option(
            names = {"-e", "--exact"},
            description = "Execute exhaustive combinatorial verification over all 191,052 outcomes."
    )
    private boolean exactValidation;

    @Option(
            names = {"-c", "--config"},
            description = "Path to custom JSON game configuration file."
    )
    private Path configPath;

    @Option(
            names = {"-d", "--detailed"},
            negatable = true,
            description = "Track detailed per-symbol hit and payout statistics (default: true).",
            defaultValue = "true"
    )
    private boolean detailed = true;

    @Override
    public Integer call() {
        try {
            AppProperties properties = new AppProperties();
            properties.getSimulationSettings().setDefaultRounds(rounds);
            properties.getSimulationSettings().setUseVirtualThreads(virtualThreads);
            properties.getReportSettings().setShowDetailedSymbolBreakdown(detailed);

            if (threads != null && threads > 0) {
                properties.getSimulationSettings().setThreadPoolSize(threads);
            }

            // Load Game Configuration
            GameConfigLoader loader = new GameConfigLoader();
            GameConfig gameConfig;
            if (configPath != null) {
                gameConfig = loader.loadFromFile(configPath);
            } else {
                gameConfig = loader.loadFromClasspath(properties.getGameSettings().getDefaultConfigPath());
            }

            SlotEngine engine = new SlotEngine(gameConfig);

            if (exactValidation) {
                System.out.println("\nRunning Exhaustive Mathematical Validation...");
                ExhaustiveCombinatorialValidator validator = new ExhaustiveCombinatorialValidator(engine);
                SimulationReport exactReport = validator.validateAllCombinations();
                System.out.println(ReportFormatter.format(exactReport, "EXHAUSTIVE THEORETICAL MATHEMATICAL REPORT"));
            }

            if (rounds > 0) {
                System.out.printf("\nExecuting Monte Carlo Simulation (%s rounds)...%n", String.format("%,d", rounds));
                VirtualThreadMonteCarloSimulator simulator = new VirtualThreadMonteCarloSimulator(engine, properties);
                SimulationReport simReport = simulator.runSimulation(rounds);
                System.out.println(ReportFormatter.format(simReport, "MONTE CARLO SIMULATION STATISTICAL REPORT"));
            }

            return 0;
        } catch (Exception e) {
            log.error("Simulation run error: {}", e.getMessage(), e);
            System.err.println("Error: " + e.getMessage());
            return 1;
        }
    }
}
