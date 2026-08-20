package com.rubyplay.slot.config;

import com.rubyplay.slot.model.GameConfig;
import com.rubyplay.slot.model.PayTable;
import com.rubyplay.slot.model.Payline;
import com.rubyplay.slot.model.ReelStrip;
import com.rubyplay.slot.model.Symbol;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory creating the standard 3x3 Fruit Slot game configuration
 * matching the exact Rubyplay technical assessment math model.
 */
public final class DefaultGameConfigFactory {

    private static final String[] REEL_0_CODES = {
            "L4", "L3", "L2", "SCA", "H2", "H2", "H2", "L1", "L1", "L1",
            "H3", "H1", "H2", "L2", "L2", "L2", "W1", "L3", "L3", "L3",
            "H3", "H3", "H3", "L4", "L4", "L4", "H3", "L1", "L2", "L4",
            "SCA", "L3", "L3", "L3", "H3", "L1", "L2", "L3", "SCA", "L2",
            "L2", "L2", "L2", "H3", "H3", "H3", "L4", "L4", "L4", "SCA",
            "L1", "L1", "L1", "H1", "L2", "L2", "L2", "SCA", "L3", "L3",
            "L3"
    };

    private static final String[] REEL_1_CODES = {
            "L4", "L3", "L2", "SCA", "H2", "H2", "H2", "L1", "L1", "L1",
            "H3", "H1", "H2", "L2", "L2", "L2", "W1", "L3", "L3", "L3",
            "H3", "H3", "H3", "L4", "L4", "L4", "H3", "L1", "L2", "L4",
            "SCA", "L3", "L3", "L3", "H3", "L1", "L2", "L3", "SCA", "L2",
            "L4", "H3", "H3", "H3", "SCA", "L2", "L2", "L2", "L2", "SCA",
            "L3", "L3", "L3", "H1", "L4", "L4", "L4", "L3"
    };

    private static final String[] REEL_2_CODES = {
            "L4", "L3", "L2", "SCA", "H2", "H2", "H2", "L1", "L1", "L1",
            "H3", "H1", "H2", "L2", "L2", "L2", "W1", "L3", "L3", "L3",
            "H3", "H3", "H3", "L4", "L4", "L4", "H3", "L1", "L2", "L4",
            "H3", "L3", "L3", "L3", "H3", "L1", "L2", "L3", "SCA", "L2",
            "H2", "H3", "H3", "H3", "L2", "L2", "L2", "L1", "L1", "SCA",
            "L2", "L2", "L2", "L1"
    };

    private DefaultGameConfigFactory() {
    }

    /**
     * Builds the default 3x3 game configuration.
     *
     * @return immutable GameConfig
     */
    public static GameConfig createDefaultConfig() {
        List<ReelStrip> reels = List.of(
                createReelStrip(REEL_0_CODES),
                createReelStrip(REEL_1_CODES),
                createReelStrip(REEL_2_CODES)
        );

        List<Payline> paylines = List.of(
                new Payline(1, "Top Horizontal", new int[]{0, 0, 0}),
                new Payline(2, "Middle Horizontal", new int[]{1, 1, 1}),
                new Payline(3, "Bottom Horizontal", new int[]{2, 2, 2}),
                new Payline(4, "Diagonal Top-Left to Bottom-Right", new int[]{0, 1, 2}),
                new Payline(5, "Diagonal Bottom-Left to Top-Right", new int[]{2, 1, 0})
        );

        PayTable payTable = createPayTable();

        return GameConfig.builder()
                .gameId("rubyplay-3x3-classic")
                .name("Rubyplay 3x3 Classic Fruit Slot")
                .reelsCount(3)
                .rowsCount(3)
                .defaultBet(10L)
                .reels(reels)
                .paylines(paylines)
                .payTable(payTable)
                .build();
    }

    private static ReelStrip createReelStrip(String[] codes) {
        List<Symbol> symbols = new ArrayList<>(codes.length);
        for (String code : codes) {
            Symbol sym = Symbol.fromCode(code)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown symbol code: " + code));
            symbols.add(sym);
        }
        return new ReelStrip(symbols);
    }

    private static PayTable createPayTable() {
        Map<Symbol, Map<Integer, Long>> map = new EnumMap<>(Symbol.class);

        addPayout(map, Symbol.W1, 3, 2000L);
        addPayout(map, Symbol.H1, 3, 800L);
        addPayout(map, Symbol.H2, 3, 500L);
        addPayout(map, Symbol.H3, 3, 80L);
        addPayout(map, Symbol.L1, 3, 50L);
        addPayout(map, Symbol.L2, 3, 20L);
        addPayout(map, Symbol.L3, 3, 15L);
        addPayout(map, Symbol.L4, 3, 10L);
        addPayout(map, Symbol.SCA, 3, 200L);

        return new PayTable(map);
    }

    private static void addPayout(Map<Symbol, Map<Integer, Long>> map, Symbol sym, int count, long payout) {
        map.computeIfAbsent(sym, k -> new HashMap<>()).put(count, payout);
    }
}
