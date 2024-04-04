package Lexical;

public class Token {
    enum TokenType {
        // Single-character tokens.
        LEFT_PAREN, RIGHT_PAREN, LEFT_SQUARE, RIGHT_SQUARE, MODULO,
        COMMA, PLUS, MINUS, DIVIDE, MULTIPLY, COMMENT, EQUAL, CONCAT,

        // Two-character tokens.
        ISEQUAL, NOTEQUAL, GREATER, LESSER, GREATER_EQUAL, LESSER_EQUAL,

        // DATA TYPES
        INT, FLOAT, CHAR, BOOL,

        // Literals
        IDENTIFIER, CHARACTER, NUMBER, LOGICAL, START, END,

        // KEYWORDS
        IF, ELSEIF, ELSE, NOT, AND, OR, DISPLAY,

        EOF,
    }

    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line;


    public Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
