package com.rubyplay.slot.stats

import com.rubyplay.slot.model.Symbol
import spock.lang.Specification

class ReportFormatterSpec extends Specification {

    def "should format symbol probability in readable decimal format instead of scientific notation"() {
        given: "a report with a low-probability symbol stat"
        def stat = SymbolStat.builder()
                .symbol(Symbol.W1)
                .hitCount(5)
                .totalPayout(10000)
                .probability(5.0 / 191052.0) // ~2.6170885e-05
                .returnPercentage(0.52)
                .build()

        def report = SimulationReport.builder()
                .totalRounds(191052)
                .totalWagerAmount(1910520)
                .totalWinAmount(10000)
                .winningRoundsCount(5)
                .hitFrequencyPercentage(0.0026)
                .rtpPercentage(0.52)
                .symbolBreakdowns(List.of(stat))
                .build()

        when: "formatting the report"
        def formatted = ReportFormatter.format(report, "TEST REPORT")

        then: "probability is formatted as fixed decimal and does not contain scientific exponent notation"
        formatted.contains("0.0000262")
        !formatted.contains("2.6170885e-05")
        formatted.contains("W1")
        formatted.contains("Wild")
    }
}
