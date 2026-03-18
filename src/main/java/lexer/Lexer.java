package lexer;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private final String source;
    private int pos;
    private int line;

    public Lexer(String sourceCode) {
        this.source = sourceCode;
        this.pos = 0;
        this.line = 1;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (pos < source.length()) {
            char c = source.charAt(pos);

            if (c == '\n') {
                tokens.add(new Token(TokenType.NEWLINE, "\n", line));
                line++;
                pos++;
            } else if (Character.isWhitespace(c)) {
                tokens.add(new Token(TokenType.WHITESPACE, String.valueOf(c), line));
                pos++;
            } else if (Character.isDigit(c)) {
                tokens.add(readNumber());
            } else if (Character.isLetter(c) || c == '_') {
                tokens.add(readIdentifier());
            } else if (c == '"') {
                tokens.add(readString());
            } else if (isOperator(c)) {
                tokens.add(new Token(TokenType.OPERATOR, String.valueOf(c), line));
                pos++;
            } else if (isPunctuation(c)) {
                tokens.add(new Token(TokenType.PUNCTUATION, String.valueOf(c), line));
                pos++;
            } else {
                tokens.add(new Token(TokenType.UNKNOWN, String.valueOf(c), line));
                pos++;
            }
        }

        tokens.add(new Token(TokenType.EOF, "", line));
        return tokens;
    }

    private Token readNumber() {
        int start = pos;
        while (pos < source.length() && Character.isDigit(source.charAt(pos))) {
            pos++;
        }
        return new Token(TokenType.NUMBER, source.substring(start, pos), line);
    }

    private Token readIdentifier() {
        int start = pos;
        while (pos < source.length() && (Character.isLetterOrDigit(source.charAt(pos)) || source.charAt(pos) == '_')) {
            pos++;
        }
        String value = source.substring(start, pos);
        TokenType type = isKeyword(value) ? TokenType.KEYWORD : TokenType.IDENTIFIER;
        return new Token(type, value, line);
    }

    private Token readString() {
        pos++; // Salta las comillas del inicio
        int start = pos;
        while (pos < source.length() && source.charAt(pos) != '"') {
            if (source.charAt(pos) == '\n') line++;
            pos++;
        }
        String value = source.substring(start, pos);
        if (pos < source.length()) pos++; // skip closing quote
        return new Token(TokenType.STRING, value, line);
    }

    private boolean isOperator(char c) {
        return "+-*/=<>!&|".indexOf(c) >= 0;
    }

    private boolean isPunctuation(char c) {
        return "(){}[];,.:".indexOf(c) >= 0;
    }

    // Extend this set once the professor defines the target language
    private boolean isKeyword(String word) {
        return switch (word) {
            case "if", "else", "while", "for", "return", "int", "float",
                 "boolean", "true", "false", "null", "void", "class", "new" -> true;
            default -> false;
        };
    }
}
