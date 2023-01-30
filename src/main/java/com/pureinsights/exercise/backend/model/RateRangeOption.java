package com.pureinsights.exercise.backend.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enum that represents the different rate range options of the endpoints in the API.
 * @author Joseph Tenorio
 */
public enum RateRangeOption {

    ABOVE("gte8", 0),
    BETWEEN6AND8("gte6-lt8", 1),
    BETWEEN4AND6("gte4-lt6", 2),
    BETWEEN2AND4("gte2-lt4", 3),
    BELLOW("lw2", 4);
    /** List of valid codes of rate range options */
    public static final List<Integer> VALID_CODES =
            Arrays.stream(RateRangeOption.class.getEnumConstants())
                    .map(RateRangeOption::getCode).collect( Collectors.toList() );
    /** List of valid representations of rate range options */
    public static final List<String> VALID_REPRESENTATIONS =
            Arrays.stream(RateRangeOption.class.getEnumConstants())
                    .map(RateRangeOption::getRepresentation).collect( Collectors.toList() );
    /** The string representation of a rate range option */
    private final String representation;
    /** The integer code associated with a rate range option */
    private final Integer code;

     RateRangeOption(final String representation, final Integer code) {
        this.representation = representation;
        this.code = code;
     }

    public String getRepresentation() { return representation; }
    public Integer getCode() { return code; }

    /**
     * @param n The code whose {@link RateRangeOption} will be returned
     * @return The {@link RateRangeOption} that corresponds to the given code
     */
    public static RateRangeOption retrieveByCode(int n) {
        return switch (n) {
            case 0 -> ABOVE;
            case 1 -> BETWEEN6AND8;
            case 2 -> BETWEEN4AND6;
            case 3 -> BETWEEN2AND4;
            case 4 -> BELLOW;
            default -> null;
        };
    }
}