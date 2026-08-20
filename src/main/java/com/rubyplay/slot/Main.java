package com.rubyplay.slot;

import com.rubyplay.slot.cli.SlotSimulationCommand;
import picocli.CommandLine;

/**
 * Application Entry Point.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new SlotSimulationCommand()).execute(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }
}
