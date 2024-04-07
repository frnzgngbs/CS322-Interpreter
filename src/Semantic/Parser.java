package Semantic;

import Lexical.Token;
import ErrorHandling.Error;
import ASTree.Node;

import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;
    private Node rootNode;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Node getRootNode() {
        return rootNode;
    }

    public void parse() {
        try {
            // Start parsing the program
            rootNode = program();
        } catch (ParseException e) {
            // Print error message and exit
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    // Grammar rules

    private Node program() {
        Node programNode = new Node("PROGRAM", "");
        while (!isAtEnd()) {
            Node statementNode = statement();
            if (statementNode != null) {
                programNode.addChild(statementNode);
            }
        }
        return programNode;
    }

    private Node statement() {
        if (match(Token.TokenType.DISPLAY)) {
            return displayStatement();
        } else if (match(Token.TokenType.INT) || match(Token.TokenType.CHAR) || match(Token.TokenType.BOOL)
                || match(Token.TokenType.FLOAT)) {
            return declarationStatement();
        } else if (match(Token.TokenType.END_CODE)) {
            // Skip the END CODE token without creating a statement node
            advance();
            return null; // Return null for empty statement
        } else {
            // Consume tokens until encountering the END_CODE token
            while (!isAtEnd() && !match(Token.TokenType.END_CODE)) {
                advance();
            }
            // Consume the END_CODE token
            consume(Token.TokenType.END_CODE, "Expect 'END CODE'");
            return null; // Return null for empty statement
        }
    }

    private Node displayStatement() {
        consume(Token.TokenType.DISPLAY, "Expect 'DISPLAY'");
        Node displayNode = new Node("DISPLAY_STATEMENT", "");
        displayNode.addChild(expression());
        return displayNode;
    }

    private Node declarationStatement() {
        Node declarationNode = new Node("DECLARATION_STATEMENT", "");
        consumeAnyOfTypes("Expect data type", Token.TokenType.INT, Token.TokenType.CHAR, Token.TokenType.BOOL,
                Token.TokenType.FLOAT);
        declarationNode.addChild(new Node("DATA_TYPE", advance().lexeme));
        declarationNode.addChild(new Node("IDENTIFIER", advance().lexeme));
        if (match(Token.TokenType.EQUAL)) {
            consume(Token.TokenType.EQUAL, "Expect '='");
            declarationNode.addChild(expression());
        }
        return declarationNode;
    }

    private Node expression() {
        // Implement the grammar rules for expressions
        return new Node("EXPRESSION", "");
    }

    // Utility methods

    private boolean isAtEnd() {
        return current >= tokens.size() || peek().type == Token.TokenType.EOF;
    }

    private boolean match(Token.TokenType type) {
        if (isAtEnd()) {
            return false;
        }
        return peek().type == type;
    }

    private void consume(Token.TokenType type, String message) {
        Token token = advance();
        if (token != null && token.type == type) {
            return;
        }
        error(message);
    }

    private void consumeAnyOfTypes(String message, Token.TokenType... types) {
        for (Token.TokenType type : types) {
            if (match(type)) {
                advance();
                return;
            }
        }
        error(message);
    }

    private Token peek() {
        return tokens.get(current);
    }

    private void synchronize() {
        while (!isAtEnd() && peek().type != Token.TokenType.EOF) {
            advance();
        }
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return isAtEnd() ? tokens.get(current - 1) : null; // Return null if at end
    }

    private void error(String message) {
        int line = peek().line;
        Error.error(line, message);
    }

    // Custom exception class for parse errors
    private static class ParseException extends RuntimeException {
        public ParseException(String message) {
            super(message);
        }
    }
}
