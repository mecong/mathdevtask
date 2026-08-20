package com.rubyplay.slot.model

import spock.lang.Specification

class PaylineSpec extends Specification {

    def "should correctly store and return row offsets for payline"() {
        given: "a payline configuration"
        def offsets = [0, 1, 2] as int[]
        def payline = new Payline(4, "Diagonal Line", offsets)

        when: "querying row offsets per reel index"
        def rowReel0 = payline.getRowOffset(0)
        def rowReel1 = payline.getRowOffset(1)
        def rowReel2 = payline.getRowOffset(2)

        then: "correct row offsets are retrieved"
        payline.getId() == 4
        payline.getName() == "Diagonal Line"
        rowReel0 == 0
        rowReel1 == 1
        rowReel2 == 2
    }

    def "should reject invalid row offsets"() {
        when: "creating payline with null offsets"
        new Payline(1, "Invalid", null)

        then: "IllegalArgumentException is thrown"
        thrown(IllegalArgumentException)
    }
}
