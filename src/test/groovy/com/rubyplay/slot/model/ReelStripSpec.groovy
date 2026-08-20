package com.rubyplay.slot.model

import spock.lang.Specification

class ReelStripSpec extends Specification {

    def "should correctly wrap around circular indices"() {
        given: "a reel strip with 5 symbols"
        def symbols = [Symbol.H1, Symbol.H2, Symbol.H3, Symbol.L1, Symbol.L2]
        def reelStrip = new ReelStrip(symbols)

        when: "accessing symbols at in-range and wrapped indices"
        def at0 = reelStrip.getSymbolAt(0)
        def at4 = reelStrip.getSymbolAt(4)
        def at5 = reelStrip.getSymbolAt(5)
        def at12 = reelStrip.getSymbolAt(12)
        def atNegative1 = reelStrip.getSymbolAt(-1)

        then: "proper wrapped symbols are returned"
        at0 == Symbol.H1
        at4 == Symbol.L2
        at5 == Symbol.H1
        at12 == Symbol.H3
        atNegative1 == Symbol.L2
    }

    def "should extract correct visible symbols window"() {
        given: "a reel strip"
        def symbols = [Symbol.H1, Symbol.H2, Symbol.H3, Symbol.L1, Symbol.L2]
        def reelStrip = new ReelStrip(symbols)

        when: "extracting a 3-row visible window starting from index 3"
        def visible = reelStrip.getVisibleSymbols(3, 3)

        then: "symbols at indices 3, 4, and 0 (wrapped) are returned"
        visible.length == 3
        visible[0] == Symbol.L1
        visible[1] == Symbol.L2
        visible[2] == Symbol.H1
    }

    def "should reject empty symbol collection"() {
        when: "creating reel strip with empty list"
        new ReelStrip([] as List<Symbol>)

        then: "IllegalArgumentException is thrown"
        thrown(IllegalArgumentException)
    }
}
