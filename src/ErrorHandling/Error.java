package ErrorHandling;

import Lexical.Token;

import static Lexical.Token.TokenType.*;

public class Error {

    static boolean isError = false;

    public static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where,
                               String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
        isError = true;

        if(isError) System.exit(65);

    }

    public static void error(Token token, String message) {
        if (token.type == EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }
}
