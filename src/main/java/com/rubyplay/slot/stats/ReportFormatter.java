package com.rubyplay.slot.stats;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Formats SimulationReport instances into human-readable ASCII tables and statistical summaries.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportFormatter {

    private static final String LINE = "================================================================================";
    private static final String THIN_LINE = "--------------------------------------------------------------------------------";

    /**
     * Renders a complete simulation summary report.
     *
     * @param report the generated simulation report
     * @param title  header title
     * @return formatted string
     */
    public String format(SimulationReport report, String title) {
        NumberFormat intFormat = NumberFormat.getNumberInstance(Locale.US);
        StringBuilder sb = new StringBuilder();

        sb.append("\n").append(LINE).append("\n");
        sb.append(String.format(" %-78s \n", title));
        sb.append(LINE).append("\n");

        sb.append(String.format(" %-30s : %s\n", "Total Rounds Simulated", intFormat.format(report.getTotalRounds())));
        sb.append(String.format(" %-30s : %s credits\n", "Total Bet / Wager", intFormat.format(report.getTotalWagerAmount())));
        sb.append(String.format(" %-30s : %s credits\n", "Total Payout / Win", intFormat.format(report.getTotalWinAmount())));
        sb.append(String.format(" %-30s : %s (%.2f%%)\n", "Winning Rounds (Hits)",
                intFormat.format(report.getWinningRoundsCount()), report.getHitFrequencyPercentage()));
        sb.append(THIN_LINE).append("\n");

        sb.append(String.format(" %-30s : %.6f%%\n", "Actual Return To Player (RTP)", report.getRtpPercentage()));
        sb.append(String.format(" %-30s : %.6f credits\n", "Mean Payout per Round", report.getMeanWinPerRound()));
        sb.append(String.format(" %-30s : %.6f credits\n", "Sample Variance (s^2)", report.getVariance()));
        sb.append(String.format(" %-30s : %.6f credits\n", "Standard Deviation (Credits)", report.getStandardDeviationCredits()));
        sb.append(String.format(" %-30s : %.6f bet units\n", "Standard Deviation (Bet Units)", report.getStandardDeviationBetUnits()));
        sb.append(String.format(" %-30s : %.6f credits\n", "Standard Error of Mean (SE)", report.getStandardError()));
        sb.append(THIN_LINE).append("\n");

        sb.append(" Statistical Confidence Intervals for RTP:\n");
        sb.append(String.format("   95%% Confidence Interval     : [%.4f%%  -  %.4f%%]\n",
                report.getCi95Lower(), report.getCi95Upper()));
        sb.append(String.format("   99%% Confidence Interval     : [%.4f%%  -  %.4f%%]\n",
                report.getCi99Lower(), report.getCi99Upper()));
        sb.append(THIN_LINE).append("\n");

        sb.append(String.format(" %-30s : %d ms (%.2f s)\n", "Simulation Wall Time",
                report.getDurationMillis(), report.getDurationMillis() / 1000.0));
        sb.append(String.format(" %-30s : %s spins/sec\n", "Simulation Throughput",
                intFormat.format((long) report.getSpinsPerSecond())));
        sb.append(THIN_LINE).append("\n");

        if (report.getSymbolBreakdowns() != null && !report.getSymbolBreakdowns().isEmpty()) {
            boolean hasNonZeroHits = report.getSymbolBreakdowns().stream().anyMatch(s -> s.getHitCount() > 0);
            if (hasNonZeroHits) {
                sb.append(String.format(" %-6s | %-12s | %-12s | %-15s | %-12s | %-10s\n",
                        "Symbol", "Display Name", "Hits", "Probability", "Total Payout", "Return %"));
                sb.append(THIN_LINE).append("\n");

                for (SymbolStat stat : report.getSymbolBreakdowns()) {
                    sb.append(String.format(" %-6s | %-12s | %-12s | %-15.7e | %-12s | %6.2f%%\n",
                            stat.getSymbol().getCode(),
                            stat.getSymbol().getDisplayName(),
                            intFormat.format(stat.getHitCount()),
                            stat.getProbability(),
                            intFormat.format(stat.getTotalPayout()),
                            stat.getReturnPercentage()));
                }
                sb.append(THIN_LINE).append("\n");
            }
        }

        sb.append(LINE).append("\n");
        return sb.toString();
    }
}
