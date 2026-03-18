package lexer;

public record Token(TokenType type, String value, int line) {
    @Override
    public String toString() {
        return String.format("Token{type=%-12s value=%-15s line=%d}", type, "\"" + value + "\"", line);
    }
}
