package Lexical;

import ErrorHandling.Error;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, Token.TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("AND", Token.TokenType.AND);
        keywords.put("OR",  Token.TokenType.OR);
        keywords.put("NOT",   Token.TokenType.NOT);
        keywords.put("IF",   Token.TokenType.IF);
        keywords.put("ELSE IF",   Token.TokenType.ELSEIF);
        keywords.put("ELSE",    Token.TokenType.ELSE);
        keywords.put("DISPLAY:",  Token.TokenType.DISPLAY);

    }

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while(!isAtEnd()) {
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
                addToken(Token.TokenType.LEFT_SQUARE);
                break;
            case ']':
                addToken(Token.TokenType.RIGHT_SQUARE);
                break;
            case ',':
                addToken(Token.TokenType.COMMA);
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
                line++;
                break;
            case '&':
                addToken(Token.TokenType.CONCAT);
                break;
            case ' ':
                break;
            case '/':
                addToken(Token.TokenType.DIVIDE);
                break;
            case '#':
                addToken(Token.TokenType.COMMENT);
                while(getCurrentValue() != '\n') advance();
                break;
            case '%':
                addToken(Token.TokenType.MODULO);
                break;
            case '=':
                addToken(match('=') ? Token.TokenType.ISEQUAL : Token.TokenType.EQUAL);
                break;
            case '<':
                if (match('>')) addToken(Token.TokenType.NOTEQUAL);
                else if (match('=')) addToken(Token.TokenType.LESSER_EQUAL);
                else addToken(Token.TokenType.LESSER);
                break;
            case '>':
                addToken(match('=') ? Token.TokenType.GREATER_EQUAL : Token.TokenType.GREATER);
                break;
            case '"':
                getLogicalValue();
                break;
            default:
                if (isDigit(c)) getNumberValue();
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
        if (isAtEnd()) return false;
        System.out.println(current);
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    // PEEK
    private char getCurrentValue() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char getNextValue() {
        if (isAtEnd()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_' || c == ':';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private void getLogicalValue() {
        while (getCurrentValue() != '"' && !isAtEnd()) {
            if (getCurrentValue() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Error.error(line, "Unterminated string.");
            return;
        }
        // The closing ".
        advance();

        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(Token.TokenType.LOGICAL, value);
    }

    private void getNumberValue() {
        // Continue iterating through the string, and update the current index if we are encountering a number
        while(isDigit(getCurrentValue())) {
            advance();
        }
        // Special case, if it is a float.
        // If we encounter '.', check the next character. if it is a number, continue.
        if(getCurrentValue() == '.' && isDigit(getNextValue())) {
            // Example 12.1212, since we are still in the '.' at this part, we need to increment our current index.
            advance();
            // After calling advance() method, then we are now in '1'. Continue iterating.
            while(isDigit(getCurrentValue())) {
                advance();
            }
            // Add token as double
            addToken(Token.TokenType.NUMBER,
                    Double.parseDouble(source.substring(start, current)));
            return;
        }
        // Add token as integer
        addToken(Token.TokenType.NUMBER,
                Integer.parseInt(source.substring(start, current)));
    }

    private void getIdentifier() {
        while (isAlphaNumeric(getCurrentValue())) {
            advance();
        }
        String text = source.substring(start, current);


        Token.TokenType type = keywords.get(text);
        if(type == null) {
            if(text.equals("INT")) type = Token.TokenType.INT;
            else if (text.equals("BOOL")) type = Token.TokenType.BOOL;
            else if (text.equals("FLOAT")) type = Token.TokenType.FLOAT;
            else if (text.equals("CHAR")) type = Token.TokenType.CHAR;
            else type = Token.TokenType.IDENTIFIER;

        }
        addToken(type);
    }


//    private void checkString() {
//        while (getCurrentValue() != '"' && !isAtEnd()) {
//            if (getCurrentValue() == '\n') line++;
//            advance();
//        }
//
//        if (isAtEnd()) {
//            Error.error(line, "Unterminated string.");
//            return;
//        }
//
//        // The closing ".
//        advance();
//
//        // Trim the surrounding quotes.
//        String value = source.substring(start + 1, current - 1);
//        addToken(STRING, value);
//    }
}
