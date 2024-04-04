package Lexical;

import ErrorHandling.Error;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

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
                else if (match('=')) addToken(Token.TokenType.GREATER_EQUAL);
                else addToken(Token.TokenType.GREATER);
                break;
            case '>':
                addToken(match('=') ? Token.TokenType.GREATER_EQUAL : Token.TokenType.GREATER);
                break;
            case '"':
                getLogicalValue();
            default:
                if (isDigit(c))
                    // TODO: Figure out how you can get the number literals.
                    getNumberLiteral();
                Error.error(line, "Unexpected character.");

        }
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    // PEEK
    private char getCurrentValue() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void getLogicalValue() {
        System.out.println(source.charAt(current) + "" + source.charAt(current+1) + "" + source.charAt(current+2) + "" + source.charAt(current+3));
        while (getCurrentValue() != '"' && isAtEnd()) {
            if(source.charAt(current) == 'T' && source.charAt(current + 1) == 'R'
                && source.charAt(current + 2) == 'U' && source.charAt(current + 3) == 'E') {
                break;
            }
        }

        String logical_val = source.substring(start, current+3);
        current = -3;
        advance();
        advance();
        advance();
        addToken(Token.TokenType.LOGICAL, logical_val);
    }

    private void getNumberLiteral() {
        // Continue iterating through the string, and update the current index if we are encountering a number
        while(isDigit(getCurrentValue())) {
            advance();
        }
        // Special case, if it is a float.
        // If we encounter '.', check the next character. if it is a number, continue.
        if(getCurrentValue() == '.' && isDigit(source.charAt(current + 1))) {
            // Example 12.1212, since we are still in the '.' at this part, we need to increment our current index.
            advance();
            // After calling advance() method, then we are now in '1'. Continue iterating.
            while(isDigit(getCurrentValue())) {
                advance();
            }
            // Add token as double
            addToken(Token.TokenType.NUMBER,
                    Double.parseDouble(source.substring(start, current)));
        }

        // Add token as integer
        addToken(Token.TokenType.NUMBER,
                Integer.parseInt(source.substring(start, current)));
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
