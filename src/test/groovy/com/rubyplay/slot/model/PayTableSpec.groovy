package com.rubyplay.slot.model

import spock.lang.Specification

class PayTableSpec extends Specification {

    def "should correctly return payouts for defined combinations"() {
        given: "a paytable with payouts"
        Map<Symbol, Map<Integer, Long>> map = [
                (Symbol.W1): [(3): 2000L],
                (Symbol.H1): [(3): 800L],
                (Symbol.L4): [(3): 10L]
        ]
        def payTable = new PayTable(map)

        when: "querying valid and invalid payouts"
        def w1Payout = payTable.getPayout(Symbol.W1, 3)
        def h1Payout = payTable.getPayout(Symbol.H1, 3)
        def l4Payout = payTable.getPayout(Symbol.L4, 3)
        def undefinedCountPayout = payTable.getPayout(Symbol.H1, 2)
        def undefinedSymbolPayout = payTable.getPayout(Symbol.L2, 3)
        def nullSymbolPayout = payTable.getPayout(null, 3)

        then: "correct payouts are returned"
        w1Payout == 2000L
        h1Payout == 800L
        l4Payout == 10L
        undefinedCountPayout == 0L
        undefinedSymbolPayout == 0L
        nullSymbolPayout == 0L
    }
}
