package com.rubyplay.slot.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.List;

/**
 * Immutable representation of a physical or virtual reel strip.
 * Supports efficient circular indexing and visible symbol extraction.
 */
@Getter
@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReelStrip {

    Symbol[] symbols;
    int length;

    public ReelStrip(List<Symbol> symbolsList) {
        if (symbolsList == null || symbolsList.isEmpty()) {
            throw new IllegalArgumentException("Reel strip symbols list must not be empty");
        }
        this.symbols = symbolsList.toArray(new Symbol[0]);
        this.length = this.symbols.length;
    }

    public ReelStrip(Symbol[] symbolsArray) {
        if (symbolsArray == null || symbolsArray.length == 0) {
            throw new IllegalArgumentException("Reel strip symbols array must not be empty");
        }
        this.symbols = Arrays.copyOf(symbolsArray, symbolsArray.length);
        this.length = this.symbols.length;
    }

    /**
     * Retrieves the symbol at the given index with circular wrap-around.
     *
     * @param index raw index (can be &gt;= length)
     * @return Symbol at the wrapped position
     */
    public Symbol getSymbolAt(int index) {
        int normalizedIndex = ((index % length) + length) % length;
        return symbols[normalizedIndex];
    }

    /**
     * Extracts a window of visible symbols starting from a stop index.
     *
     * @param stopPosition the starting stop index
     * @param rowCount     number of visible rows (e.g. 3)
     * @return array of visible symbols in top-to-bottom order
     */
    public Symbol[] getVisibleSymbols(int stopPosition, int rowCount) {
        Symbol[] result = new Symbol[rowCount];
        for (int r = 0; r < rowCount; r++) {
            result[r] = getSymbolAt(stopPosition + r);
        }
        return result;
    }
}
