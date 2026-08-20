package com.rubyplay.slot.simulation;

import com.rubyplay.slot.stats.SimulationReport;

/**
 * Interface for simulation engines.
 */
public interface Simulator {

    /**
     * Executes the simulation for the requested number of rounds.
     *
     * @param rounds total number of rounds to simulate
     * @return SimulationReport with aggregated results
     */
    SimulationReport runSimulation(long rounds);
}
