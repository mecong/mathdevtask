package com.rubyplay.slot.evaluator;

import com.rubyplay.slot.model.GameConfig;
import com.rubyplay.slot.model.Grid;
import com.rubyplay.slot.model.LineWin;
import com.rubyplay.slot.model.PayTable;
import com.rubyplay.slot.model.Payline;
import com.rubyplay.slot.model.ScatterWin;
import com.rubyplay.slot.model.SpinOutcome;
import com.rubyplay.slot.model.Symbol;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

/**
 * Standard win evaluator for classic 3x3 slot games.
 * Evaluates left-to-right 3-of-a-kind payline combinations with wild substitutions,
 * plus scatter triggers anywhere on the visible grid.
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StandardWinEvaluator implements WinEvaluator {

    /**
     * Payout evaluation priority order for 3-of-a-kind line wins.
     * Evaluated in descending order of payout value to guarantee maximum award.
     */
    private static final Symbol[] LINE_CANDIDATE_ORDER = {
            Symbol.W1, Symbol.H1, Symbol.H2, Symbol.H3,
            Symbol.L1, Symbol.L2, Symbol.L3, Symbol.L4
    };

    @Override
    public SpinOutcome evaluate(Grid grid, GameConfig config, int[] stopPositions) {
        long totalWin = 0L;
        List<LineWin> lineWins = new ArrayList<>();
        PayTable payTable = config.getPayTable();

        // 1. Evaluate Paylines
        for (Payline payline : config.getPaylinesArray()) {
            Symbol s0 = grid.getSymbol(payline.getRowOffset(0), 0);
            Symbol s1 = grid.getSymbol(payline.getRowOffset(1), 1);
            Symbol s2 = grid.getSymbol(payline.getRowOffset(2), 2);

            LineWin win = evaluateLine(payline.getId(), s0, s1, s2, payTable);
            if (win != null) {
                lineWins.add(win);
                totalWin += win.payout();
            }
        }

        // 2. Evaluate Scatter
        ScatterWin scatterWin = null;
        int scatterCount = grid.countSymbol(Symbol.SCA);
        if (scatterCount >= 3) {
            long scatterPayout = payTable.getPayout(Symbol.SCA, 3);
            if (scatterPayout > 0) {
                scatterWin = ScatterWin.builder()
                        .symbol(Symbol.SCA)
                        .count(scatterCount)
                        .payout(scatterPayout)
                        .build();
                totalWin += scatterPayout;
            }
        }

        return SpinOutcome.builder()
                .totalWin(totalWin)
                .lineWins(lineWins)
                .scatterWin(scatterWin)
                .grid(grid)
                .stopPositions(stopPositions)
                .build();
    }

    @Override
    public long evaluateFastPayout(Grid grid, GameConfig config) {
        long totalWin = 0L;
        PayTable payTable = config.getPayTable();
        Symbol[][] matrix = grid.getMatrix();

        // Fast payline evaluation using direct matrix array access
        for (Payline payline : config.getPaylinesArray()) {
            Symbol s0 = matrix[payline.getRowOffset(0)][0];
            Symbol s1 = matrix[payline.getRowOffset(1)][1];
            Symbol s2 = matrix[payline.getRowOffset(2)][2];

            totalWin += getLinePayout(s0, s1, s2, payTable);
        }

        // Fast scatter count evaluation
        int scatterCount = 0;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (matrix[r][c] == Symbol.SCA) {
                    scatterCount++;
                }
            }
        }

        if (scatterCount >= 3) {
            totalWin += payTable.getPayout(Symbol.SCA, 3);
        }

        return totalWin;
    }

    private LineWin evaluateLine(int lineId, Symbol s0, Symbol s1, Symbol s2, PayTable payTable) {
        for (Symbol candidate : LINE_CANDIDATE_ORDER) {
            if (s0.matches(candidate) && s1.matches(candidate) && s2.matches(candidate)) {
                long payout = payTable.getPayout(candidate, 3);
                if (payout > 0) {
                    boolean wildSubstituted = (candidate != Symbol.W1) &&
                            (s0.isWild() || s1.isWild() || s2.isWild());
                    return LineWin.builder()
                            .paylineId(lineId)
                            .winningSymbol(candidate)
                            .matchCount(3)
                            .payout(payout)
                            .wildSubstituted(wildSubstituted)
                            .build();
                }
            }
        }
        return null;
    }

    private long getLinePayout(Symbol s0, Symbol s1, Symbol s2, PayTable payTable) {
        for (Symbol candidate : LINE_CANDIDATE_ORDER) {
            if (s0.matches(candidate) && s1.matches(candidate) && s2.matches(candidate)) {
                return payTable.getPayout(candidate, 3);
            }
        }
        return 0L;
    }
}
