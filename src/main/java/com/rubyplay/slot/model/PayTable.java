package com.rubyplay.slot.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Payout lookup table supporting fast array-indexed payout queries.
 */
@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PayTable {

    // Fast array-indexed lookup: [symbol.ordinal()][count]
    long[][] payouts;
    Map<Symbol, Map<Integer, Long>> payoutMap;

    public PayTable(Map<Symbol, Map<Integer, Long>> map) {
        this.payoutMap = new EnumMap<>(Symbol.class);
        int maxSymbols = Symbol.values().length;
        int maxCount = 10; // supports up to 10 of a kind
        this.payouts = new long[maxSymbols][maxCount];

        if (map != null) {
            for (Map.Entry<Symbol, Map<Integer, Long>> entry : map.entrySet()) {
                Symbol sym = entry.getKey();
                Map<Integer, Long> countMap = entry.getValue();
                this.payoutMap.put(sym, Collections.unmodifiableMap(new HashMap<>(countMap)));
                for (Map.Entry<Integer, Long> countEntry : countMap.entrySet()) {
                    int count = countEntry.getKey();
                    long payout = countEntry.getValue();
                    if (count < maxCount) {
                        this.payouts[sym.ordinal()][count] = payout;
                    }
                }
            }
        }
    }

    /**
     * Gets the payout for landing {@code count} occurrences of {@code symbol}.
     *
     * @param symbol the winning symbol
     * @param count  the number of matched symbols (e.g. 3)
     * @return the payout amount in credits, or 0 if non-winning
     */
    public long getPayout(Symbol symbol, int count) {
        if (symbol == null || count < 0 || count >= payouts[0].length) {
            return 0L;
        }
        return payouts[symbol.ordinal()][count];
    }

    /**
     * Returns an unmodifiable view of all defined payouts.
     */
    public Map<Symbol, Map<Integer, Long>> getPayoutMap() {
        return Collections.unmodifiableMap(payoutMap);
    }
}
