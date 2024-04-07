package ErrorHandling;

import Lexical.Token;

import static Lexical.Token.TokenType.*;

public class Error {

    static boolean isError = false;
    static boolean hadRuntimeError = false;


    public static void error(int line, String message) {
        report(line, "", message);
    }

    public static void report(int line, String where,
                              String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
        isError = true;

        if(isError) System.exit(65);

    }

    public static void error(Token token, String message) {
        if (token.type == Token.TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    public static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() +
                "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
        if (hadRuntimeError) System.exit(70);
    }


}
