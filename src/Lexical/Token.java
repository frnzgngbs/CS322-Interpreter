package Lexical;

public class Token {
    enum TokenType {
        // ARITHMETIC OPERATORS
        PLUS, MINUS, DIVIDE, MULTIPLY, MODULO,

        // STRING OPERATOR
        CONCAT,


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
        IDENTIFIER, CHARACTER, NUMBER, TRUE, FALSE,

        // KEYWORDS
        IF, ELSE_IF, ELSE, DISPLAY,BEGIN_CODE, END_CODE, BEGINIF, ENDIF,

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
