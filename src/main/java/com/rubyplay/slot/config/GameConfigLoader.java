package com.rubyplay.slot.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rubyplay.slot.model.GameConfig;
import com.rubyplay.slot.model.PayTable;
import com.rubyplay.slot.model.Payline;
import com.rubyplay.slot.model.ReelStrip;
import com.rubyplay.slot.model.Symbol;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Loads and parses GameConfig instances from JSON resources or external files.
 */
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GameConfigLoader {

    ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Loads GameConfig from a classpath resource path.
     *
     * @param resourcePath classpath path (e.g. "/config/rubyplay_3x3_game.json")
     * @return parsed GameConfig
     */
    public GameConfig loadFromClasspath(String resourcePath) {
        log.debug("Loading game configuration from classpath resource: {}", resourcePath);
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                log.warn("Resource not found at '{}', falling back to DefaultGameConfigFactory", resourcePath);
                return DefaultGameConfigFactory.createDefaultConfig();
            }
            JsonNode root = objectMapper.readTree(is);
            return parseJsonNode(root);
        } catch (Exception e) {
            log.error("Failed to load config from resource '{}': {}", resourcePath, e.getMessage(), e);
            throw new IllegalStateException("Failed to parse game config from classpath: " + resourcePath, e);
        }
    }

    /**
     * Loads GameConfig from an external JSON file.
     *
     * @param filePath path to JSON file
     * @return parsed GameConfig
     */
    public GameConfig loadFromFile(Path filePath) {
        log.debug("Loading game configuration from file: {}", filePath);
        try {
            File file = filePath.toFile();
            if (!file.exists()) {
                throw new IllegalArgumentException("Configuration file does not exist: " + filePath);
            }
            JsonNode root = objectMapper.readTree(file);
            return parseJsonNode(root);
        } catch (Exception e) {
            log.error("Failed to load config from file '{}': {}", filePath, e.getMessage(), e);
            throw new IllegalStateException("Failed to parse game config from file: " + filePath, e);
        }
    }

    private GameConfig parseJsonNode(JsonNode root) {
        String gameId = root.path("gameId").asText("slot-game");
        String name = root.path("name").asText("Slot Game");
        int reelsCount = root.path("reelsCount").asInt(3);
        int rowsCount = root.path("rowsCount").asInt(3);
        long defaultBet = root.path("defaultBet").asLong(10L);

        // 1. Parse Reels
        List<ReelStrip> reels = new ArrayList<>();
        JsonNode reelsNode = root.path("reels");
        if (reelsNode.isArray()) {
            for (JsonNode reelArray : reelsNode) {
                List<Symbol> symbols = new ArrayList<>();
                for (JsonNode symCodeNode : reelArray) {
                    String code = symCodeNode.asText();
                    Symbol symbol = Symbol.fromCode(code)
                            .orElseThrow(() -> new IllegalArgumentException("Unknown symbol code: " + code));
                    symbols.add(symbol);
                }
                reels.add(new ReelStrip(symbols));
            }
        }

        // 2. Parse Paylines
        List<Payline> paylines = new ArrayList<>();
        JsonNode paylinesNode = root.path("paylines");
        if (paylinesNode.isArray()) {
            for (JsonNode lineNode : paylinesNode) {
                int id = lineNode.path("id").asInt();
                String lineName = lineNode.path("name").asText("Line " + id);
                JsonNode offsetsNode = lineNode.path("rowOffsets");
                int[] offsets = new int[offsetsNode.size()];
                for (int i = 0; i < offsetsNode.size(); i++) {
                    offsets[i] = offsetsNode.get(i).asInt();
                }
                paylines.add(new Payline(id, lineName, offsets));
            }
        }

        // 3. Parse Paytable
        Map<Symbol, Map<Integer, Long>> payTableMap = new EnumMap<>(Symbol.class);
        JsonNode paytableNode = root.path("paytable");
        Iterator<Map.Entry<String, JsonNode>> fields = paytableNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String symCode = entry.getKey();
            Symbol symbol = Symbol.fromCode(symCode)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown paytable symbol: " + symCode));
            JsonNode payoutDef = entry.getValue();

            int count = payoutDef.path("count").asInt(3);
            long payout = payoutDef.path("payout").asLong();

            payTableMap.computeIfAbsent(symbol, k -> new HashMap<>()).put(count, payout);
        }

        return GameConfig.builder()
                .gameId(gameId)
                .name(name)
                .reelsCount(reelsCount)
                .rowsCount(rowsCount)
                .defaultBet(defaultBet)
                .reels(reels)
                .paylines(paylines)
                .payTable(new PayTable(payTableMap))
                .build();
    }
}
