package lexer;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String code = """
                int x = 42;
                if (x > 10) {
                    x += 2;
                    return x;
                }
                """;

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();

        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
