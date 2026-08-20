package com.rubyplay.slot.model

import spock.lang.Specification

class GridSpec extends Specification {

    def "should correctly construct grid from stop positions and count symbols"() {
        given: "three reel strips"
        def r0 = new ReelStrip([Symbol.H1, Symbol.H2, Symbol.H3, Symbol.SCA] as Symbol[])
        def r1 = new ReelStrip([Symbol.L1, Symbol.L2, Symbol.SCA, Symbol.W1] as Symbol[])
        def r2 = new ReelStrip([Symbol.SCA, Symbol.L3, Symbol.L4, Symbol.H3] as Symbol[])
        def reels = [r0, r1, r2] as ReelStrip[]
        def stops = [1, 2, 0] as int[] // r0 from index 1 (H2, H3, SCA), r1 from index 2 (SCA, W1, L1), r2 from index 0 (SCA, L3, L4)

        when: "creating Grid from stop positions"
        def grid = Grid.fromStopPositions(reels, stops, 3)

        then: "matrix matches expected positions"
        grid.getSymbol(0, 0) == Symbol.H2
        grid.getSymbol(1, 0) == Symbol.H3
        grid.getSymbol(2, 0) == Symbol.SCA

        grid.getSymbol(0, 1) == Symbol.SCA
        grid.getSymbol(1, 1) == Symbol.W1
        grid.getSymbol(2, 1) == Symbol.L1

        grid.getSymbol(0, 2) == Symbol.SCA
        grid.getSymbol(1, 2) == Symbol.L3
        grid.getSymbol(2, 2) == Symbol.L4

        and: "scatter counting works correctly"
        grid.countSymbol(Symbol.SCA) == 3
        grid.countSymbol(Symbol.W1) == 1
        grid.countSymbol(Symbol.H1) == 0
    }
}
