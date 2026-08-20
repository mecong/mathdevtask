package com.rubyplay.slot.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

/**
 * Represents the visible symbol matrix on the slot game screen.
 * Dimensions are [rowsCount][reelsCount].
 */
@Getter
@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Grid {

    int rowsCount;
    int reelsCount;
    Symbol[][] matrix;

    public Grid(Symbol[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            throw new IllegalArgumentException("Grid matrix cannot be null or empty");
        }
        this.rowsCount = matrix.length;
        this.reelsCount = matrix[0].length;
        this.matrix = new Symbol[rowsCount][reelsCount];
        for (int r = 0; r < rowsCount; r++) {
            this.matrix[r] = Arrays.copyOf(matrix[r], reelsCount);
        }
    }

    /**
     * Constructs a Grid from reel strips and stop positions.
     *
     * @param reels          array of ReelStrip objects
     * @param stopPositions  array of stop indices for each reel
     * @param rowCount       number of visible rows
     */
    public static Grid fromStopPositions(ReelStrip[] reels, int[] stopPositions, int rowCount) {
        int reelsCount = reels.length;
        Symbol[][] matrix = new Symbol[rowCount][reelsCount];
        for (int c = 0; c < reelsCount; c++) {
            Symbol[] visible = reels[c].getVisibleSymbols(stopPositions[c], rowCount);
            for (int r = 0; r < rowCount; r++) {
                matrix[r][c] = visible[r];
            }
        }
        return new Grid(matrix);
    }

    /**
     * Retrieves the symbol at the given row and reel column.
     *
     * @param row  row index (0 = top)
     * @param reel reel index (0 = leftmost)
     * @return Symbol at the position
     */
    public Symbol getSymbol(int row, int reel) {
        return matrix[row][reel];
    }

    /**
     * Counts the total occurrences of a symbol anywhere on the grid (e.g. for Scatter).
     *
     * @param symbol target symbol to count
     * @return occurrence count
     */
    public int countSymbol(Symbol symbol) {
        int count = 0;
        for (int r = 0; r < rowsCount; r++) {
            for (int c = 0; c < reelsCount; c++) {
                if (matrix[r][c] == symbol) {
                    count++;
                }
            }
        }
        return count;
    }
}
