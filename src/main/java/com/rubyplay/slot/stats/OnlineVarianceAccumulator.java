package com.rubyplay.slot.stats;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Numerically stable online accumulator for sample mean, variance, and standard deviation.
 * Implements Welford's algorithm for sequential accumulation and Chan et al.'s parallel
 * reduction algorithm for thread-safe aggregation.
 */
@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OnlineVarianceAccumulator {

    long count;
    double mean;
    double m2; // Sum of squared differences from the mean (sum((x - mean)^2))

    public OnlineVarianceAccumulator() {
        this.count = 0L;
        this.mean = 0.0;
        this.m2 = 0.0;
    }

    public OnlineVarianceAccumulator(long count, double mean, double m2) {
        this.count = count;
        this.mean = mean;
        this.m2 = m2;
    }

    /**
     * Adds a single observation using Welford's online algorithm.
     *
     * @param value sample value (e.g. round payout in credits)
     */
    public void add(double value) {
        count++;
        double delta = value - mean;
        mean += delta / count;
        double delta2 = value - mean;
        m2 += delta * delta2;
    }

    /**
     * Combines another accumulator into this one using Chan et al.'s parallel variance merge formula.
     *
     * @param other accumulator to merge
     */
    public void combine(OnlineVarianceAccumulator other) {
        if (other == null || other.count == 0) {
            return;
        }
        if (this.count == 0) {
            this.count = other.count;
            this.mean = other.mean;
            this.m2 = other.m2;
            return;
        }

        long newCount = this.count + other.count;
        double delta = other.mean - this.mean;
        double newMean = this.mean + delta * ((double) other.count / newCount);
        double newM2 = this.m2 + other.m2 + (delta * delta * ((double) this.count * other.count / newCount));

        this.count = newCount;
        this.mean = newMean;
        this.m2 = newM2;
    }

    /**
     * Calculates the sample variance (s^2, with Bessel's correction n-1).
     *
     * @return sample variance, or 0 if count &lt; 2
     */
    public double getSampleVariance() {
        if (count < 2) {
            return 0.0;
        }
        return m2 / (count - 1);
    }

    /**
     * Calculates the population variance (sigma^2, divided by n).
     *
     * @return population variance
     */
    public double getPopulationVariance() {
        if (count == 0) {
            return 0.0;
        }
        return m2 / count;
    }

    /**
     * Calculates sample standard deviation (s = sqrt(sample_variance)).
     *
     * @return sample standard deviation
     */
    public double getSampleStandardDeviation() {
        return Math.sqrt(getSampleVariance());
    }

    /**
     * Calculates population standard deviation.
     *
     * @return population standard deviation
     */
    public double getPopulationStandardDeviation() {
        return Math.sqrt(getPopulationVariance());
    }
}
