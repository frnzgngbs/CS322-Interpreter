package ErrorHandling;

import Lexical.Token;

import static Lexical.Token.TokenType.*;

public class Error {

    static boolean isError = false;
    static boolean hadRuntimeError = false;
    static final String filePath = "C:\\Users\\ardon\\Documents\\CS322-Interpreter\\testcase.txt";


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
            report(token.line, " at line " + token.line, message);
        }
    }

    public static void runtimeError(RuntimeError error) {
        error(error.token, error.getMessage());
        hadRuntimeError = true;
        if (hadRuntimeError) System.exit(70);
    }

    public static void scanError(String message) {
        System.err.println(message);
        System.exit(65);
    }


}
