package com.tiduswr;

public record Token(
    TokenType type,
    String lexema
) {
    @Override
    public final String toString() {
        return String.format("<%s, %s>", type.name(), lexema);
    }
}
