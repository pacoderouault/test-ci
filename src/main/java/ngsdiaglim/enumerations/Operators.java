package ngsdiaglim.enumerations;

import org.apache.commons.lang3.builder.ToStringBuilder;

public enum Operators {
    EQUALS("="),
    NOT_EQUALS("!="),
    STARTS_WITH("Starts with"),
    ENDS_WITH("Ends with"),
    CONTAINS("Contains"),
    GREATER_THAN(">"),
    LOWER_THAN("<"),
    GREATER_OR_EQUALS_THAN(">="),
    LOWER_OR_EQUALS_THAN("<=")
    ;

    private final String symbol;

    Operators(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {return symbol;}

    @Override
    public String toString() {
        return symbol;
    }
}
