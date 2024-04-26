package Lexical;

import ErrorHandling.Error;
import ErrorHandling.RuntimeError;

public class Token {
    public enum TokenType {
        // ARITHMETIC OPERATORS
        PLUS, MINUS, DIVIDE, MULTIPLY, MODULO,

        // STRING OPERATOR
        CONCAT, NEW_LINE,

        // LOGICAL OPERATOR
        AND, OR, NOT,

        // GROUPINGS
        LEFT_PAREN, RIGHT_PAREN, LEFT_SQUARE, RIGHT_SQUARE, SEPARATOR,
        COMMA, COMMENT, EQUAL,

        // RELATIONAL OPERATOR
        ISEQUAL, NOTEQUAL, GREATER, LESSER, GREATER_EQUAL, LESSER_EQUAL,

        // LOOP
        WHILE, BEGIN_WHILE, END_WHILE,

        // DATA TYPES
        INT, FLOAT, CHAR, BOOL,

        // Literals
        IDENTIFIER, CHARACTER, NUMBER, TRUE, FALSE, STRING,

        // KEYWORDS
        IF, ELSE_IF, ELSE, DISPLAY, BEGIN_CODE, END_CODE, BEGINIF, ENDIF, SCAN,

        EOF
    }

    public final TokenType type;
    public String lexeme;
    public final Object literal;
    public final int line;

    public Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        return "\u001B[32m"+type + "\u001B[0m " + lexeme + " " + literal;
    }

    public void setLexeme(String lex) {
        this.lexeme = lex;
    }

//    public String defineEscape() {
//        // Check if the string has at least two characters
//        if (lexeme.length() >= 2) {
//            // System.out.println("lex length" + lexeme.length());
//            // Remove the first and last characters
//            String result = lexeme.substring(1, lexeme.length() - 1);
//            // Remove occurrences of special characters $, &, and "
//            result = result.replace("$", "").replace("&", "").replace("\"", "");
//            // Replace consecutive spaces with a single space
//            result = result.replaceAll("\\s+", " ");
//            // Trim leading and trailing spaces
//            result = result.trim();
//
//            // Check if the string is sandwiched between brackets
//            // System.out.println("res = " + result);
//            // System.out.println("reslength = " + result.length());
//
//            if (result.length() == 0) {
//                Error.report("No logic implemented with \"[]\".");
//                // throw new RuntimeException("No logic implemented in []");
//            }
//
//            if (result.length() > 5) {
//                int countL = 0;
//                int countR = 0;
//                int lastLIndex = 0;
//                int firstRIndex = 0;
//                boolean isRVisited = false;
//                for (int i = 0; i < result.length(); i++) {
//                    // System.out.println("charat = " + result.charAt(i) + " " + i);
//                    if (result.charAt(i) == '[') {
//                        countL++;
//                        lastLIndex = i;
//                    } else if (result.charAt(i) == ']') {
//                        countR++;
//                        if (!isRVisited) {
//                            firstRIndex = i;
//                            isRVisited = true;
//                        }
//
//                    }
//                }
//
//                // sandwich
//                // System.out.println("COUNT L = " + countL);
//                // System.out.println("COUNT R = " + countR);
//                if (countL == countR && (countL > 0 && countR > 0)) {
//                    // System.out.println("COUNT L = COUNT R");
//                    result = result.substring(firstRIndex + 1, lastLIndex).trim();
//                    result = "[" + result + "]";
//                    // System.out
//                    // .println("last l = " + lastLIndex + "first R = " + firstRIndex + "isrvisited
//                    // = "
//                    // + isRVisited);
//                    // System.out.println("here");
//                }
//            }
//
//            // System.out.println("reach");
//
//            // if (result.indexOf('[') != -1 && result.indexOf(']') != -1) {
//            // System.out.println("here");
//            // // result = "[" + result + "]";
//            // }
//
//            return result;
//        } else {
//            // If the string has less than two characters, return it as is
//            // System.out.println("length is 1");
//            return lexeme;
//        }
//    }

}
