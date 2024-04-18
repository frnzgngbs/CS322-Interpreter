package Lexical;

import ErrorHandling.Error;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private boolean reachEndCode = false;
    private static Stack<Character> valEscape = new Stack<>();
    private static final Map<String, Token.TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("AND", Token.TokenType.AND);
        keywords.put("OR", Token.TokenType.OR);
        keywords.put("NOT", Token.TokenType.NOT);
        keywords.put("IF", Token.TokenType.IF);
        keywords.put("ELSE IF", Token.TokenType.ELSE_IF);
        keywords.put("ELSE", Token.TokenType.ELSE);
        keywords.put("DISPLAY", Token.TokenType.DISPLAY);
        keywords.put("SCAN", Token.TokenType.SCAN);
        keywords.put("BEGIN CODE:", Token.TokenType.BEGIN_CODE);
        keywords.put("END CODE:", Token.TokenType.END_CODE);

        keywords.put("CHAR", Token.TokenType.CHAR);
        keywords.put("INT", Token.TokenType.INT);
        keywords.put("FLOAT", Token.TokenType.FLOAT);
        keywords.put("BOOL", Token.TokenType.BOOL);
    }

    public Lexer(String source) {
        this.source = source;
    }

    public static Stack<Character> stackEscape() {
        return valEscape;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();

        }
        tokens.add(new Token(Token.TokenType.EOF, "", null, line));
        return tokens;
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(Token.TokenType type) {
        addToken(type, null);
    }

    private void addToken(Token.TokenType type, Object literal) {
        String text = source.substring(start, current);
        if (type == Token.TokenType.CHARACTER) {
            text = String.valueOf(literal);
        }
        tokens.add(new Token(type, text, literal, line));
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(Token.TokenType.LEFT_PAREN);
                break;
            case ')':
                addToken(Token.TokenType.RIGHT_PAREN);
                break;
            case '[':
                // initially push [ since first time encountered
                // valEscape.push(c);
                // // System.err.println("pushed");
                // int i = 0;
                //
                // while (!isAtEnd()) {
                // // System.out.println("CURRENT VAL " + getCurrentValue());
                // if (getCurrentValue() == ']') {
                // if (!valEscape.isEmpty()) {
                // valEscape.pop();
                // // System.out.println("was popped");
                // }
                // }
                // advance();
                // }
                // if (!valEscape.isEmpty()) {
                // Error.report(line, " at line " + line, "Encountered '[' but ']' is not
                // found.");
                // }
                addToken(Token.TokenType.LEFT_SQUARE);
                break;
            case ']':
                addToken(Token.TokenType.RIGHT_SQUARE);
                break;
            case ',':
                addToken(Token.TokenType.COMMA);
                break;

            case ':':
                addToken(Token.TokenType.SEPARATOR);
                break;
            case '-':
                addToken(Token.TokenType.MINUS);
                break;
            case '+':
                addToken(Token.TokenType.PLUS);
                break;
            case '*':
                addToken(Token.TokenType.MULTIPLY);
                break;
            case '$':
                addToken(Token.TokenType.NEW_LINE);
                break;
            case '\n':
                line++;
                break;
            case '\t', ' ':
                break;
            case '&':
                addToken(Token.TokenType.CONCAT);
                break;
            case '/':
                addToken(Token.TokenType.DIVIDE);
                break;
            case '#':
                // addToken(Token.TokenType.COMMENT);
                // keep consuming until reaching new line
                // addToken(Token.TokenType.COMMENT);
                while (getCurrentValue() != '\n' && !isAtEnd()) {
                    advance();
                }
                return;
            case '%':
                addToken(Token.TokenType.MODULO);
                break;
            case '"':
                getLogicalValue();
                break;
            case '\'':
                getCharacterValue();
                break;
            case '=':
                addToken(match('=') ? Token.TokenType.ISEQUAL : Token.TokenType.EQUAL);
                break;
            case '<':
                // System.out.println(current);
                if (match('>'))
                    addToken(Token.TokenType.NOTEQUAL);
                else if (match('='))
                    addToken(Token.TokenType.LESSER_EQUAL);
                else
                    addToken(Token.TokenType.LESSER);
                break;
            case '>':
                addToken(match('=') ? Token.TokenType.GREATER_EQUAL : Token.TokenType.GREATER);
                break;
            default:
                if (isDigit(c))
                    getNumberValue();
                else if (isAlpha(c)) {
                    getIdentifier();
                } else {
                    Error.error(line, "Unexpected character");
                }
                break;

        }
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private boolean match(char expected) {
        if (isAtEnd())
            return false;
        if (source.charAt(current) != expected)
            return false;

        current++;
        return true;
    }

    public int getLine() {
        return this.line;
    }

    // PEEK
    private char getCurrentValue() {
        if (isAtEnd())
            return '\0';
        return source.charAt(current);
    }

    private char getNextValue() {
        if (isAtEnd())
            return '\0';
        return source.charAt(current + 1);
    }

    private char getPreviousValue() {
        if (isAtEnd())
            return '\0';
        return source.charAt(current - 1);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        if (reachEndCode) {
            Error.error(line, "Outside of the END CODE BEGIN CODE block.");
        }
        return isAlpha(c) || isDigit(c);
    }

    private void getCharacterValue() {
        if (reachEndCode) {
            Error.error(line, "Outside of the END CODE BEGIN CODE block.");
        }

        int error = 0;

        char character = getCurrentValue();

        if (character == '\'') {
            Error.error(line, "Character literal cannot be empty.");
        } else {
            if (getNextValue() != '\'') {
                error = 2;
            }
            while (getCurrentValue() != '\'') {
                if (getCurrentValue() == '\n') {
                    Error.error(line, "Missing ' right character literal");
                }
                if (error == 1) {
                    Error.error(line, "Character length issue.");
                }
                error = 1;
                advance();
            }
            addToken(Token.TokenType.CHARACTER, character);
            if (!isAtEnd()) {
                advance();
            }
        }

    }

    private void getLogicalValue() {
        if (reachEndCode) {
            Error.error(line, "Outside of the END CODE BEGIN CODE block.");
        }
        while (getCurrentValue() != '"' && !isAtEnd()) {
            if (getCurrentValue() == '\n')
                line++;
            advance();
        }

        if (isAtEnd()) {
            Error.error(line, "Unterminated string caught at line " + line + ".");
            return;
        }
        // The closing ".
        advance();

        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);

        if (value.equals("TRUE")) {
            addToken(Token.TokenType.TRUE, true);
        } else if (value.equals("FALSE")) {
            addToken(Token.TokenType.FALSE, false);
        } else {
            addToken(Token.TokenType.STRING, value);
        }
    }

    private void getNumberValue() {
        // Continue iterating through the string, and update the current index if we are
        // encountering a number
        if (reachEndCode) {
            Error.error(line, "Outside of the END CODE BEGIN CODE block.");
        }
        while (isDigit(getCurrentValue())) {
            advance();
        }
        // Special case, if it is a float.
        // If we encounter '.', check the next character. if it is a number, continue.

        if (isAlpha(getCurrentValue())) {
            Error.error(line, "Unsupported identifier");
        }

        if (getCurrentValue() == '.' && isDigit(getNextValue())) {
            // Example 12.1212, since we are still in the '.' at this part, we need to
            // increment our current index.
            advance();
            // After calling advance() method, then we are now in '1'. Continue iterating.
            while (isDigit(getCurrentValue())) {
                advance();
            }

            if (isAlpha(getCurrentValue())) {
                Error.error(line, "Unsupported literal");
            }

            // Add token as Float.
            addToken(Token.TokenType.NUMBER,
                    Float.parseFloat(source.substring(start, current)));
            return;
        }
        // Add token as integer
        addToken(Token.TokenType.NUMBER,
                Integer.parseInt(source.substring(start, current)));
    }

    private void getIdentifier() {
        if (reachEndCode) {
            Error.error(line, "Outside of the END CODE BEGIN CODE block.");
        }
        while (isAlphaNumeric(getCurrentValue())) {
            advance();
        }
        String text = source.substring(start, current);

        if (text.equalsIgnoreCase("int") || text.equalsIgnoreCase("float") || text.equalsIgnoreCase("char")
                || text.equalsIgnoreCase("bool")) {
            if (!(text.equals("INT") || text.equals("FLOAT") || text.equals("CHAR") || text.equals("BOOL"))) {
                Error.error(line, "Expected " + text.toUpperCase() + " but found " + text + ".");
            }
        }

        // 'ab'
        if (getCurrentValue() == '\'')
            Error.error(line, "Missing left ' character literal");

        Token.TokenType type = keywords.get(text);
        if (type == null) {
            // Check if it's BEGIN CODE or END CODE
            if (text.equals("BEGIN") && match(' ')) {
                if (match('C') && match('O') && match('D') && match('E')) {
                    type = Token.TokenType.BEGIN_CODE;
                    addToken(type);
                    return;
                }
                current--;
            }
            if (text.equals("BEGIN") && match(' ') && match('I') && match('F')) {
                type = Token.TokenType.BEGINIF;
                addToken(type);
                return;
            }
            if (text.equals("END") && match(' ')) {
                if(match('C') && match('O') && match('D') && match('E')) {
                    type = Token.TokenType.END_CODE;
                    addToken(type);
                    reachEndCode = true;
                    return;
                }
                if (match('I') && match('F')) {
                    type = Token.TokenType.ENDIF;
                    addToken(type);
                    return;
                }
            }

            if (text.equals("DISPLAY")) {
                type = Token.TokenType.DISPLAY;
                addToken(type);
                return;
            }
            if (text.equals("SCAN")) {
                type = Token.TokenType.SCAN;
                addToken(type);
                return;
            }
            type = Token.TokenType.IDENTIFIER;
        }

        addToken(type);
    }

    // private void checkString() {
    // while (getCurrentValue() != '"' && !isAtEnd()) {
    // if (getCurrentValue() == '\n') line++;
    // advance();
    // }
    //
    // if (isAtEnd()) {
    // Error.error(line, "Unterminated string.");
    // return;
    // }
    //
    // // The closing ".
    // advance();
    //
    // // Trim the surrounding quotes.
    // String value = source.substring(start + 1, current - 1);
    // addToken(STRING, value);
    // }
}
