package com.rubyplay.slot.config;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Centralized application properties adhering to the project configuration standard.
 * Contains nested settings for simulation parameters, game mechanics, and reporting.
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppProperties {

    SimulationSettings simulationSettings = new SimulationSettings();
    GameSettings gameSettings = new GameSettings();
    ReportSettings reportSettings = new ReportSettings();

    @Getter
    @Setter
    @ToString
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SimulationSettings {
        long defaultRounds = 10_000_000L;
        int threadPoolSize = Runtime.getRuntime().availableProcessors();
        boolean useVirtualThreads = true;
        int batchSize = 100_000;
        int warmupRounds = 100_000;
    }

    @Getter
    @Setter
    @ToString
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class GameSettings {
        String defaultConfigPath = "/config/rubyplay_3x3_game.json";
        long betPerRound = 10L;
        int reelsCount = 3;
        int rowsCount = 3;
    }

    @Getter
    @Setter
    @ToString
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ReportSettings {
        boolean showDetailedSymbolBreakdown = true;
        boolean showConfidenceIntervals = true;
        double confidenceLevel = 0.95;
    }
}
