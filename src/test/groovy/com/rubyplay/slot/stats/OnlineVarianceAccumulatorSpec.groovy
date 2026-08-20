package com.rubyplay.slot.stats

import spock.lang.Specification

class OnlineVarianceAccumulatorSpec extends Specification {

    def "should correctly calculate mean and sample variance using Welford's algorithm"() {
        given: "an online accumulator and a known dataset"
        def accumulator = new OnlineVarianceAccumulator()
        def values = [10.0, 20.0, 30.0, 40.0, 50.0]

        when: "adding observations one by one"
        values.each { accumulator.add(it) }

        then: "mean, sample variance, and sample std dev match mathematical definitions"
        accumulator.getCount() == 5
        accumulator.getMean() == 30.0
        // Sample variance for [10, 20, 30, 40, 50] is ((10-30)^2 + (20-30)^2 + (30-30)^2 + (40-30)^2 + (50-30)^2) / 4 = 1000 / 4 = 250
        accumulator.getSampleVariance() == 250.0
        Math.abs(accumulator.getSampleStandardDeviation() - Math.sqrt(250.0)) < 1e-9
        accumulator.getPopulationVariance() == 200.0
    }

    def "should correctly merge two accumulators in parallel"() {
        given: "two separate accumulators receiving disjoint subsets"
        def acc1 = new OnlineVarianceAccumulator()
        def acc2 = new OnlineVarianceAccumulator()

        def set1 = [10.0, 20.0, 30.0]
        def set2 = [40.0, 50.0]

        set1.each { acc1.add(it) }
        set2.each { acc2.add(it) }

        when: "combining acc2 into acc1"
        acc1.combine(acc2)

        then: "combined statistics equal sequential accumulation of all 5 values"
        acc1.getCount() == 5
        acc1.getMean() == 30.0
        acc1.getSampleVariance() == 250.0
    }
}
