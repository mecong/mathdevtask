package com.rubyplay.slot.evaluator

import com.rubyplay.slot.config.DefaultGameConfigFactory
import com.rubyplay.slot.model.GameConfig
import com.rubyplay.slot.model.Grid
import com.rubyplay.slot.model.Symbol
import spock.lang.Specification

class StandardWinEvaluatorSpec extends Specification {

    GameConfig config
    StandardWinEvaluator evaluator

    def setup() {
        config = DefaultGameConfigFactory.createDefaultConfig()
        evaluator = new StandardWinEvaluator()
    }

    def "should evaluate Example 1 (Non-winning game play) to 0 win"() {
        given: "Example 1 grid from PAR sheet"
        Symbol[][] matrix = [
                [Symbol.H3, Symbol.L2, Symbol.L4],
                [Symbol.H1, Symbol.W1, Symbol.L4],
                [Symbol.H2, Symbol.L3, Symbol.L4]
        ]
        def grid = new Grid(matrix)
        def stops = [10, 15, 23] as int[]

        when: "evaluating the spin"
        def outcome = evaluator.evaluate(grid, config, stops)
        def fastPayout = evaluator.evaluateFastPayout(grid, config)

        then: "outcome is non-winning with 0 total payout"
        !outcome.isWin()
        outcome.getTotalWin() == 0L
        outcome.getLineWins().isEmpty()
        outcome.getScatterWin().isEmpty()
        fastPayout == 0L
    }

    def "should evaluate Example 2 (One line win on payline 3 for 3x L3) to 15 credits"() {
        given: "Example 2 grid from PAR sheet"
        Symbol[][] matrix = [
                [Symbol.L3, Symbol.L4, Symbol.L1],
                [Symbol.L2, Symbol.L4, Symbol.L4],
                [Symbol.L3, Symbol.L3, Symbol.L3]
        ]
        def grid = new Grid(matrix)
        def stops = [56, 51, 49] as int[]

        when: "evaluating the spin"
        def outcome = evaluator.evaluate(grid, config, stops)
        def fastPayout = evaluator.evaluateFastPayout(grid, config)

        then: "Line 3 wins 15 credits for 3x L3"
        outcome.isWin()
        outcome.getTotalWin() == 15L
        outcome.getLineWins().size() == 1
        with(outcome.getLineWins().get(0)) {
            paylineId == 3
            winningSymbol == Symbol.L3
            matchCount == 3
            payout == 15L
            !wildSubstituted
        }
        outcome.getScatterWin().isEmpty()
        fastPayout == 15L
    }

    def "should evaluate Example 3 (3 lines win on paylines 2, 4, and 5 for 3x H3) to 240 credits"() {
        given: "Example 3 grid from PAR sheet"
        Symbol[][] matrix = [
                [Symbol.H3, Symbol.L4, Symbol.H3],
                [Symbol.H3, Symbol.H3, Symbol.H3],
                [Symbol.H3, Symbol.L1, Symbol.H3]
        ]
        def grid = new Grid(matrix)
        def stops = [39, 25, 20] as int[]

        when: "evaluating the spin"
        def outcome = evaluator.evaluate(grid, config, stops)
        def fastPayout = evaluator.evaluateFastPayout(grid, config)

        then: "paylines 2, 4, and 5 each win 80 credits for total of 240"
        outcome.isWin()
        outcome.getTotalWin() == 240L
        outcome.getLineWins().size() == 3
        outcome.getLineWins().collect { it.paylineId }.sort() == [2, 4, 5]
        outcome.getLineWins().every { it.winningSymbol == Symbol.H3 && it.payout == 80L }
        outcome.getScatterWin().isEmpty()
        fastPayout == 240L
    }

    def "should evaluate Example 4 (Bonus Trigger + Line Win) to 220 credits"() {
        given: "Example 4 grid from PAR sheet"
        Symbol[][] matrix = [
                [Symbol.SCA, Symbol.H3, Symbol.L1],
                [Symbol.L2,  Symbol.SCA, Symbol.SCA],
                [Symbol.L2,  Symbol.L2,  Symbol.L2]
        ]
        def grid = new Grid(matrix)
        def stops = [34, 39, 44] as int[]

        when: "evaluating the spin"
        def outcome = evaluator.evaluate(grid, config, stops)
        def fastPayout = evaluator.evaluateFastPayout(grid, config)

        then: "Line 3 pays 20 (3x L2) and 3 Scatters pay 200 for total of 220"
        outcome.isWin()
        outcome.getTotalWin() == 220L
        outcome.getLineWins().size() == 1
        outcome.getLineWins().get(0).paylineId == 3
        outcome.getLineWins().get(0).winningSymbol == Symbol.L2
        outcome.getLineWins().get(0).payout == 20L

        outcome.getScatterWin().isPresent()
        with(outcome.getScatterWin().get()) {
            symbol == Symbol.SCA
            count == 3
            payout == 200L
        }
        fastPayout == 220L
    }

    def "should correctly substitute Wilds on paylines with priority to highest paying symbol"() {
        given: "a grid with Wild on payline 1 (top row) with H1 symbols"
        Symbol[][] matrix = [
                [Symbol.W1, Symbol.H1, Symbol.H1],
                [Symbol.L1, Symbol.L2, Symbol.L3],
                [Symbol.L4, Symbol.L4, Symbol.L4]
        ]
        def grid = new Grid(matrix)

        when: "evaluating"
        def outcome = evaluator.evaluate(grid, config, [0, 0, 0] as int[])

        then: "top line awards 3x H1 payout (800) and bottom line awards 3x L4 (10)"
        outcome.getTotalWin() == 810L
        def line1Win = outcome.getLineWins().find { it.paylineId == 1 }
        line1Win != null
        line1Win.winningSymbol == Symbol.H1
        line1Win.payout == 800L
        line1Win.wildSubstituted
    }

    def "should award 3x W1 payout (2000 credits) when 3 Wilds land on a payline"() {
        given: "a grid with 3 Wilds on payline 2 (middle row)"
        Symbol[][] matrix = [
                [Symbol.L1, Symbol.L2, Symbol.L3],
                [Symbol.W1, Symbol.W1, Symbol.W1],
                [Symbol.L4, Symbol.L4, Symbol.L4]
        ]
        def grid = new Grid(matrix)

        when: "evaluating"
        def outcome = evaluator.evaluate(grid, config, [0, 0, 0] as int[])

        then: "middle line awards 2000 credits for 3x W1"
        def line2Win = outcome.getLineWins().find { it.paylineId == 2 }
        line2Win != null
        line2Win.winningSymbol == Symbol.W1
        line2Win.payout == 2000L
        !line2Win.wildSubstituted
    }
}
