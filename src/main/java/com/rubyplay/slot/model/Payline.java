package com.rubyplay.slot.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

/**
 * Defines a payline trajectory across the reel columns.
 * For a standard 3-reel game, specifies the row offset for each reel (reel 0, 1, 2).
 */
@Getter
@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Payline {

    int id;
    String name;
    int[] rowOffsets;

    public Payline(int id, String name, int[] rowOffsets) {
        if (rowOffsets == null || rowOffsets.length == 0) {
            throw new IllegalArgumentException("Payline row offsets must not be empty");
        }
        this.id = id;
        this.name = name != null ? name : "Line " + id;
        this.rowOffsets = Arrays.copyOf(rowOffsets, rowOffsets.length);
    }

    /**
     * Returns the row index on the given reel column for this payline.
     *
     * @param reelIndex the column index (0, 1, 2)
     * @return the row offset
     */
    public int getRowOffset(int reelIndex) {
        return rowOffsets[reelIndex];
    }
}
