package Lexical;

public class Token {
    public enum TokenType {
        // ARITHMETIC OPERATORS
        PLUS, MINUS, DIVIDE, MULTIPLY, MODULO,

        // STRING OPERATOR
        CONCAT, NEW_LINE,


        // LOGICAL OPERATOR
        AND, OR, NOT,


        // GROUPINGS
        LEFT_PAREN, RIGHT_PAREN, LEFT_SQUARE, RIGHT_SQUARE,
        COMMA, COMMENT, EQUAL,

        // RELATIONAL OPERATOR
        ISEQUAL, NOTEQUAL, GREATER, LESSER, GREATER_EQUAL, LESSER_EQUAL,

        // DATA TYPES
        INT, FLOAT, CHAR, BOOL,

        // Literals
        IDENTIFIER, CHARACTER, NUMBER, TRUE, FALSE, STRING,

        // KEYWORDS
        IF, ELSE_IF, ELSE, DISPLAY,BEGIN_CODE, END_CODE, BEGINIF, ENDIF, SCAN,

        EOF,
    }

    public final TokenType type;
    public final String lexeme;
    public final Object literal;
    public final int line;


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
