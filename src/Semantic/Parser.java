package Semantic;

import java.util.ArrayList;
import java.util.List;

import ErrorHandling.Error;
import Lexical.Token;

import static Lexical.Token.TokenType.*;


public class Parser {
    private final List<Token> tokens;
    private int current = 0;
    private Token.TokenType lastDataType;


    private static class ParseError extends RuntimeException {
    }

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }

    private Expr expression() {
        return assignment();
    }


    private Expr assignment() {
        Expr expr = equality();

        if (match(EQUAL)) {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;

                return new Expr.Assign(name, value);
            }

            Error.error(equals, "Invalid assignment target.");
        }
        return expr;
    }

    private Stmt declaration() {
        try {
            if (match(INT)) return varDeclaration(INT);
            else if (match(FLOAT)) return varDeclaration(FLOAT);
            else if (match(CHAR)) return varDeclaration(CHAR);
            else if (match(BOOL)) return varDeclaration(BOOL);
            else if (match(COMMA)) return varDeclaration(lastDataType);
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt varDeclaration(Token.TokenType dataType) {

        Token name = consume(IDENTIFIER, "Expect variable name.");

        Expr initializer = null;
        if (match(EQUAL)) {
            initializer = expression();
        }

        return new Stmt.Variable(dataType, name, initializer);
    }
    private Stmt statement() {
        if (match(DISPLAY)) return displayStatement();
        if (match(BEGIN_CODE)) {
            return new Stmt.CodeStructure(codeStructure());
        }

        return expressionStatement();
    }

    private Stmt displayStatement() {
        Expr value = expression();
        return new Stmt.Display(value);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        return new Stmt.Expression(expr);
    }

    private List<Stmt> codeStructure() {
        List<Stmt> statements = new ArrayList<>();

        while (!check(END_CODE) && !isAtEnd()) {
            statements.add(declaration());
        }

        consume(END_CODE, "Expect 'END CODE' after BEGIN CODE.");
        return statements;
    }


    private Expr equality() {
        Expr expr = comparison();

        while (match(Token.TokenType.NOTEQUAL, Token.TokenType.ISEQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private boolean match(Token.TokenType... types) {
        for (Token.TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(Token.TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Expr comparison() {
        Expr expr = term();

        while (match(GREATER, GREATER_EQUAL, LESSER, LESSER_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr term() {
        Expr expr = factor();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factor() {
        Expr expr = unary();

        while (match(DIVIDE, MULTIPLY)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(PLUS, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }


    private Expr primary() {
        if (match(FALSE)) return new Expr.Literal("FALSE");
        if (match(TRUE)) return new Expr.Literal("TRUE");

        if (match(NUMBER, CHARACTER, TRUE, FALSE)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        if (match(IDENTIFIER)) {
            return new Expr.Variable(previous());
        }


        throw error(peek(), "Expect expression.");

    }

    private Token consume(Token.TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        Error.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {

            switch (peek().type) {
                case IF:

                case DISPLAY:
                    return;
            }

            advance();
        }
    }

}
