package com.rubyplay.slot.simulation

import com.rubyplay.slot.config.AppProperties
import com.rubyplay.slot.config.DefaultGameConfigFactory
import com.rubyplay.slot.engine.SlotEngine
import spock.lang.Specification

class VirtualThreadMonteCarloSimulatorSpec extends Specification {

    def "should execute parallel Monte Carlo simulation using virtual threads and converge to theoretical RTP"() {
        given: "game engine and virtual thread simulator"
        def config = DefaultGameConfigFactory.createDefaultConfig()
        def engine = new SlotEngine(config)
        def properties = new AppProperties()
        properties.getSimulationSettings().setUseVirtualThreads(true)
        properties.getSimulationSettings().setBatchSize(50_000)
        def simulator = new VirtualThreadMonteCarloSimulator(engine, properties)

        when: "simulating 1,000,000 rounds"
        def report = simulator.runSimulation(1_000_000L)

        then: "simulation metrics are successfully calculated"
        report.getTotalRounds() == 1_000_000L
        report.getTotalWagerAmount() == 10_000_000L
        report.getTotalWinAmount() > 0L

        and: "simulated RTP is close to theoretical 93.36% (within reasonable margin)"
        Math.abs(report.getRtpPercentage() - 93.36) < 1.5

        and: "simulated Standard Deviation is close to theoretical ~4.87 bet units"
        Math.abs(report.getStandardDeviationBetUnits() - 4.87) < 0.5

        and: "theoretical RTP lies within the calculated 99% confidence interval"
        double theoreticalRtp = 93.358
        theoreticalRtp >= report.getCi99Lower()
        theoreticalRtp <= report.getCi99Upper()
    }
}
