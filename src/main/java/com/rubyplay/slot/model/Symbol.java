package com.rubyplay.slot.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents slot machine symbols with their properties (Wild, Scatter, Display Names).
 */
@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Symbol {

    W1("W1", "Wild", true, false),
    H1("H1", "Seven", false, false),
    H2("H2", "Bell", false, false),
    H3("H3", "Bar", false, false),
    L1("L1", "Banana", false, false),
    L2("L2", "Orange", false, false),
    L3("L3", "Plum", false, false),
    L4("L4", "Cherry", false, false),
    SCA("SCA", "Scatter", false, true);

    private static final Map<String, Symbol> CODE_MAP = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(Symbol::getCode, Function.identity()));

    String code;
    String displayName;
    boolean wild;
    boolean scatter;

    /**
     * Finds a symbol by its short code (e.g., "W1", "H1").
     *
     * @param code symbol string code
     * @return Optional containing the Symbol if found, or empty Optional
     */
    public static Optional<Symbol> fromCode(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(CODE_MAP.get(code.trim().toUpperCase()));
    }

    /**
     * Checks if this symbol can substitute or match another target symbol on a payline.
     * Wild substitutes for all non-scatter symbols.
     *
     * @param target the target symbol to match
     * @return true if this symbol matches the target or is a wild substituting for a non-scatter
     */
    public boolean matches(Symbol target) {
        if (this == target) {
            return true;
        }
        if (this.wild && !target.isScatter()) {
            return true;
        }
        return false;
    }
}
