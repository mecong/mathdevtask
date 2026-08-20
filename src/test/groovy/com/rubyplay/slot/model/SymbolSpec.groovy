package com.rubyplay.slot.model

import spock.lang.Specification
import spock.lang.Unroll

class SymbolSpec extends Specification {

    @Unroll
    def "should correctly parse symbol code '#code' to #expected"() {
        given: "a symbol code string"

        when: "parsing the symbol code"
        def result = Symbol.fromCode(code)

        then: "the expected symbol is returned"
        result.isPresent() == isPresent
        if (isPresent) {
            result.get() == expected
        }

        where:
        code   | isPresent | expected
        "W1"   | true      | Symbol.W1
        "h1"   | true      | Symbol.H1
        "H2"   | true      | Symbol.H2
        "H3"   | true      | Symbol.H3
        "L1"   | true      | Symbol.L1
        "l2"   | true      | Symbol.L2
        "L3"   | true      | Symbol.L3
        "L4"   | true      | Symbol.L4
        "SCA"  | true      | Symbol.SCA
        "xyz"  | false     | null
        ""     | false     | null
        null   | false     | null
    }

    def "should verify Wild symbol properties and substitution rules"() {
        given: "a Wild symbol and various target symbols"
        def wild = Symbol.W1

        when: "checking properties"
        def isWild = wild.isWild()
        def isScatter = wild.isScatter()

        then: "wild is flagged properly"
        isWild
        !isScatter

        and: "wild substitutes for all non-scatter symbols"
        wild.matches(Symbol.H1)
        wild.matches(Symbol.H2)
        wild.matches(Symbol.H3)
        wild.matches(Symbol.L1)
        wild.matches(Symbol.L2)
        wild.matches(Symbol.L3)
        wild.matches(Symbol.L4)
        wild.matches(Symbol.W1)

        and: "wild does not substitute for Scatter"
        !wild.matches(Symbol.SCA)
    }

    def "should verify Scatter symbol properties"() {
        given: "a Scatter symbol"
        def scatter = Symbol.SCA

        when: "checking properties"
        def isScatter = scatter.isScatter()
        def isWild = scatter.isWild()

        then: "scatter is flagged properly"
        isScatter
        !isWild

        and: "scatter does not substitute for other symbols"
        !scatter.matches(Symbol.H1)
        scatter.matches(Symbol.SCA)
    }
}
