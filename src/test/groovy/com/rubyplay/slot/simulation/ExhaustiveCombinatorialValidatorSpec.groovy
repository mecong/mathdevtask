package com.rubyplay.slot.simulation

import com.rubyplay.slot.config.DefaultGameConfigFactory
import com.rubyplay.slot.engine.SlotEngine
import com.rubyplay.slot.model.Symbol
import spock.lang.Specification

class ExhaustiveCombinatorialValidatorSpec extends Specification {

    def "should execute exhaustive validation and verify exact mathematical PAR sheet expectations"() {
        given: "game engine initialized with standard Rubyplay 3x3 config"
        def config = DefaultGameConfigFactory.createDefaultConfig()
        def engine = new SlotEngine(config)
        def validator = new ExhaustiveCombinatorialValidator(engine)

        when: "evaluating all 191,052 stop permutations"
        def report = validator.validateAllCombinations()

        then: "total combinations and bets match PAR sheet exactly"
        report.getTotalRounds() == 191052L
        report.getTotalWagerAmount() == 1910520L
        report.getTotalWinAmount() == 1783625L

        and: "overall RTP equals 93.358091% (rounds to 93.36%)"
        Math.abs(report.getRtpPercentage() - 93.358091) < 0.0001

        and: "mathematical standard deviation matches theoretical dispersion"
        Math.abs(report.getStandardDeviationCredits() - 48.67627) < 0.001
        Math.abs(report.getStandardDeviationBetUnits() - 4.867627) < 0.0001

        and: "exact symbol hits match theoretical combinations"
        def symbolStats = report.getSymbolBreakdowns().collectEntries { [it.symbol(), it] }

        // Line hits across 5 paylines (matches sheet hits * 5)
        symbolStats[Symbol.W1].hitCount() == 5L
        symbolStats[Symbol.H1].hitCount() == 85L
        symbolStats[Symbol.H2].hitCount() == 745L
        symbolStats[Symbol.H3].hitCount() == 5495L
        symbolStats[Symbol.L1].hitCount() == 2425L
        symbolStats[Symbol.L2].hitCount() == 11755L
        symbolStats[Symbol.L3].hitCount() == 7015L
        symbolStats[Symbol.L4].hitCount() == 2695L

        // Scatter hits: 75 combinations * 27 positions = 2025
        symbolStats[Symbol.SCA].hitCount() == 2025L
        Math.abs(symbolStats[Symbol.SCA].returnPercentage() - 21.20) < 0.01
    }
}
