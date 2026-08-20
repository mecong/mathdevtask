package com.rubyplay.slot.config

import com.rubyplay.slot.model.Symbol
import spock.lang.Specification

class GameConfigLoaderSpec extends Specification {

    def "should correctly parse game configuration from classpath JSON"() {
        given: "a GameConfigLoader"
        def loader = new GameConfigLoader()

        when: "loading from classpath"
        def config = loader.loadFromClasspath("/config/rubyplay_3x3_game.json")

        then: "configuration is properly parsed"
        config != null
        config.getGameId() == "rubyplay-3x3-classic"
        config.getName() == "Rubyplay 3x3 Classic Fruit Slot"
        config.getReelsCount() == 3
        config.getRowsCount() == 3
        config.getDefaultBet() == 10L

        and: "reel lengths match PAR sheet"
        config.getReels().size() == 3
        config.getReels().get(0).getLength() == 61
        config.getReels().get(1).getLength() == 58
        config.getReels().get(2).getLength() == 54

        and: "paylines are loaded"
        config.getPaylines().size() == 5
        config.getPaylines().get(0).getRowOffsets() == [0, 0, 0] as int[]
        config.getPaylines().get(4).getRowOffsets() == [2, 1, 0] as int[]

        and: "paytable is populated"
        config.getPayTable().getPayout(Symbol.W1, 3) == 2000L
        config.getPayTable().getPayout(Symbol.H1, 3) == 800L
        config.getPayTable().getPayout(Symbol.SCA, 3) == 200L
    }
}
