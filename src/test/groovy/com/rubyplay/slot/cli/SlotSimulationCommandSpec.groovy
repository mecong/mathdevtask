package com.rubyplay.slot.cli

import picocli.CommandLine
import spock.lang.Specification

class SlotSimulationCommandSpec extends Specification {

    def "should have detailed breakdown and virtual threads enabled by default"() {
        given: "a new command instance without arguments"
        def cmd = new SlotSimulationCommand()
        def cli = new CommandLine(cmd)

        when: "parsing empty arguments"
        cli.parseArgs()

        then: "flags are true by default"
        cmd.detailed
        cmd.virtualThreads
    }

    def "should keep detailed flag true when explicitly passed --detailed or -d"() {
        given: "a new command instance"
        def cmd = new SlotSimulationCommand()
        def cli = new CommandLine(cmd)

        when: "parsing --detailed"
        cli.parseArgs(flag)

        then: "detailed remains true"
        cmd.detailed

        where:
        flag << ["--detailed", "-d"]
    }

    def "should disable detailed breakdown when --no-detailed is passed"() {
        given: "a new command instance"
        def cmd = new SlotSimulationCommand()
        def cli = new CommandLine(cmd)

        when: "parsing --no-detailed"
        cli.parseArgs("--no-detailed")

        then: "detailed is false"
        !cmd.detailed
    }

    def "should handle virtual threads flags correctly"() {
        given: "a command instance"
        def cmd = new SlotSimulationCommand()
        def cli = new CommandLine(cmd)

        when: "parsing arguments"
        cli.parseArgs(args as String[])

        then: "virtualThreads matches expected state"
        cmd.virtualThreads == expected

        where:
        args                   | expected
        ["--virtual-threads"]  | true
        ["-v"]                 | true
        ["--no-virtual-threads"] | false
    }
}
