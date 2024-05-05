package Semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ErrorHandling.Error;
import Lexical.Token;

import static Lexical.Token.TokenType.*;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;
    private Token currentTokenBeingParsed;
    private Token.TokenType lastDataType;
    private boolean reachEndCode = false;

//    debugging tools
    private int debugger = 0;
    private static int once = 0;
    private static int tokenviewerreset = 0;

    private static class ParseError extends RuntimeException {
    }

    public Parser(List<Token> tokens, int debug) {
        this.debugger = debug;
        this.tokens = tokens;
        if (tokens.get(0).type != BEGIN_CODE) {
            for (Token tok : tokens) {
                if (tok.type == END_CODE) {
                    Error.error(tok, "Encountered END CODE but \"BEGIN CODE\" is not found\n");
                }
                if(tok.type == COMMENT) {
                    continue;
                }
            }
            Error.error(tokens.get(0), "Amawa jud o, asa naman na imong begin code? Basa2 lage docs.");
        }
    }

    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
//        debug("List<Stmt> parse(");

        while (!isAtEnd()) {

            statements.add(declaration());
        }
        return statements;
    }

    private Expr expression() {
//        debug("Expr expression()");
        return assignment();
    }

    private Expr assignment() {
//        expressiondebug("Expr assignment()");
        Expr expr = OR();

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

    private Expr OR() {
//        expressiondebug("Expr OR()");
        Expr expr = AND();

        while (match(OR)) {
            Token operator = previous();
            Expr right = AND();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr AND() {
//        expressiondebug("Expr AND()");
        Expr expr = equality();

        while (match(AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    private Stmt declaration() {
//        debug("Stmt declaration()");
        try {

            // System.out.println("DID WE COME IN HERE?");
            if (match(INT))
                return varDeclaration(INT);
            else if (match(FLOAT))
                return varDeclaration(FLOAT);
            else if (match(CHAR))
                return varDeclaration(CHAR);
            else if (match(BOOL))
                return varDeclaration(BOOL);
            else if (match(COMMA))
                return varDeclaration(lastDataType);
            else if(match(ELSE_IF)) Error.error(previous(), "Found '" + previous().lexeme + "' without 'IF'");
            else if(match(ELSE)) Error.error(previous(), "Found '" + previous().lexeme + "' without 'IF'");
//            else if((peek().lexeme.equalsIgnoreCase("int") || peek().lexeme.equalsIgnoreCase("bool")
//                    || peek().lexeme.equalsIgnoreCase("float") || peek().lexeme.equalsIgnoreCase("char"))
//                    && match(IDENTIFIER)) {
//                if(!previous().lexeme.equals("INT") || !previous().lexeme.equals("FLOAT")
//                || !previous().lexeme.equals("CHAR") || !previous().lexeme.equals("BOOL")){
//                    Error.error(previous(), "Expected " + previous().lexeme.toUpperCase() + " but found " + previous().lexeme + ".");
//                } //            } //            else if((peek().lexeme.equalsIgnoreCase("if") || peek().lexeme.equalsIgnoreCase("else if") //                    || peek().lexeme.equalsIgnoreCase("else")) && match(IDENTIFIER)) {
//                if(!previous().lexeme.equals("IF") || !previous().lexeme.equals("ELSE IF")
//                        || !previous().lexeme.equals("ELSE")) {
//                    Error.error(previous(), "Expected " + previous().lexeme.toUpperCase() + " but found " + previous().lexeme + ".");
//                }
//            }




            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt varDeclaration(Token.TokenType dataType) {
//        debug("Stmt varDeclaration()");
        lastDataType = dataType;

        Token type = peek();
        switch (type.type) {
            case SCAN:
                Error.error(type, "Cannot use keyword SCAN as a variable name.");
                break;
            case DISPLAY:
                Error.error(type, "Cannot use keyword DISPLAY as a variable name.");
                break;
            case INT:
                Error.error(type, "Cannot use INT type as a variable name.");
                break;
            case FLOAT:
                Error.error(type, "Cannot use FLOAT type as a variable name.");
                break;
            case BOOL:
                Error.error(type, "Cannot use BOOL type as a variable name");
                break;
            case CHAR:
                Error.error(type, "Cannot use CHAR type as a variable name.");
                break;
            case AND:
                Error.error(type, "Cannot use AND keyword as a variable name.");
                break;
            case OR:

                Error.error(type, "Cannot use OR keyword as a variable name.");
                break;
            case NOT:
                Error.error(type, "Cannot use NOT keyword as a variable name.");
                break;
            case IF:
                Error.error(type, "Cannot use IF keyword as a variable name.");
                break;
            case ELSE_IF:
                Error.error(type, "Cannot use ELSE IF keyword as a variable name.");
                break;
            case ELSE:
                Error.error(type, "Cannot use ELSE keyword as a variable name.");
                break;
            case NUMBER:
                Error.error(type, "Identifiers starts with " + type.literal + " and is not supported.");
                break;
            case SEPARATOR,
                    COMMA,
                    CONCAT,
                    NEW_LINE:
                Error.error(type, "Invalid statement.");
                break;

        }

        Token name = consume(IDENTIFIER, "Expect variable name.");

        Expr initializer = null;
        if (match(EQUAL)) {
            initializer = expression();
        }

        return new Stmt.Variable(lastDataType, name, initializer);
    }

    private Stmt statement() {
//        debug("Stmt statement()");
        if (match(DISPLAY)) {
            return displayStatement();
        }
        if (match(BEGIN_CODE)) {
            return new Stmt.CodeStructure(codeStructure());
        }
        if (match(SCAN)) {
            return scanStatement();
        }
        if (match(IF))
            return ifStatement();

        if(match(WHILE)) return whileStatement();
        return expressionStatement();
    }


    private Stmt scanStatement() {
//        debug("Stmt scanStatement()");
        if (peek().type != SEPARATOR) {
            Error.error(peek(), "Missing separator ':' for SCAN keyword.");
        }

        advance();

        List<Expr> expressions = new ArrayList<>();

        do {
            expressions.add(expression());
        } while (match(COMMA));

        // No need to advance here

        return new Stmt.Scan(expressions);
    }

    private Stmt displayStatement() {
//        debug("Stmt displayStatement()");
        if (peek().type != SEPARATOR) {
            Error.error(peek(), "Missing separator ':' for DISPLAY keyword.");
        }

        // STATEMENTS OF OUR DISPLAY
        List<Expr> expressions = new ArrayList<>();

        // Consume the colon
        advance();

        boolean doAdvance = true;
        while (!match(EOD)) {
            if (peek().type != LEFT_SQUARE) {
//                System.out.println("PEEK: " + peek().type);
                expressions.add(expression());
                if(peek().type != EOD) {
//                    System.out.println(previous().lexeme);
                    consume(
                            CONCAT,
                            "Expect \"&\" after \"" + previous().lexeme + "\" but found \"" + peek().lexeme + "\".");
                }
            }
            else if (check(LEFT_SQUARE)) {
                expressions.add(expression());

            }
        }
        // do {
        // if(!(peek().type == LEFT_SQUARE)) {
        // expressions.add(expression());
        // } else {
        // while(!match(RIGHT_SQUARE)) {
        // expressions.add(expression());
        // }
        // consume(RIGHT_SQUARE, "Missing ']' after '['");
        //
        // }
        // expressions.add(expression());
        // } while(match(IDENTIFIER) || match(NEW_LINE) || match(CONCAT)
        // || match(STRING) || match(LEFT_SQUARE));

        return new Stmt.Display(expressions);
    }

    private List<Stmt> codeStructure() {
//        debug("List<Stmt> codeStructure()");
        List<Stmt> statements = new ArrayList<>();

        while (!check(END_CODE) && !isAtEnd()) {
            statements.add(declaration());
        }

        consume(END_CODE, "Expect 'END CODE' after BEGIN CODE.");

        if (previous().type == END_CODE) {
            reachEndCode = true;
        }

        return statements;
    }

    private Stmt ifStatement() {
//        System.out.println("HERE");
//        debug("Stmt ifStatement()");
        List<Expr> condition = new ArrayList<>();
        List<List<Stmt>> body_statement = new ArrayList<>();
        List<Stmt> else_body_statement = new ArrayList<>();

        while(!check(ELSE) && !(check(END_CODE) || check(IDENTIFIER)
            || check(SCAN) || check(DISPLAY) || check(INT)
            || check(FLOAT) || check(CHAR) || check(BOOL))) {
            // RESET THE BODY STATEMENT OF UR CONDITIONAL STATEMENT
//            System.out.println("PEEK VALUE: " + peek());
            List<Stmt> if_body_statement = new ArrayList<>();
            match(ELSE_IF);
//            if(match(ELSE_IF)) System.err.println("Current exp: " + previous().type);
            consume(LEFT_PAREN, "Expect '(' after 'IF'.");
            condition.add(expression());
            consume(RIGHT_PAREN, "Expect ')' after if condition.");
            consume(BEGINIF, "Expect \"BEGIN IF\".");
            while(!check(ENDIF)) {
                if(match(IF)) {
                    List<Stmt> inside_if = new ArrayList<>();
                    consume(LEFT_PAREN, "Expect '(' after 'IF'.");
                    condition.add(expression());
                    consume(RIGHT_PAREN, "Expect ')' after if condition.");
                    consume(BEGINIF, "Expect \"BEGIN IF\".");
                    while(!check(ENDIF)) {
                        inside_if.add(declaration());
                    }
                    consume(ENDIF, "Expect \"END IF after BEGIN IF.");
                    if_body_statement.addAll(inside_if);
                }
                else {
                    if_body_statement.add(declaration());
                }

            }
            body_statement.add(if_body_statement);
            consume(ENDIF, "Expect \"END IF after BEGIN IF.");

            if(check(IF)) {
                break;
            }
        }

        if (match(ELSE)) {
            consume(BEGINIF, "Expect \"BEGIN IF\" else IF.");
            while(!check(ENDIF)) {
                else_body_statement.add(declaration());
            }
            consume(ENDIF, "Expect \"END IF after BEGIN IF.");
        }

        return new Stmt.If(condition, body_statement, else_body_statement);
    }

    private Stmt whileStatement() {
        List<Stmt> while_body = new ArrayList<>();
        Expr condition = null;
        while(!check(END_CODE)) {
            consume(LEFT_PAREN, "Expect '(' after 'WHILE'.");
            condition = expression();
            consume(RIGHT_PAREN, "Expect ')' after 'condition'.");
            consume(BEGIN_WHILE, "Expect \"BEGIN WHILE\". before the body statement");
            while_body.add(declaration());
            consume(END_WHILE, "Expect \"END WHILE\" at end.");
        }
        return new Stmt.While(condition, while_body);
    }

    private Stmt expressionStatement() {
//        debug("Stmt expressionStatement()");
        Expr expr = expression();
        return new Stmt.Expression(expr);
    }

    private Expr equality() {
//        expressiondebug("Expr equality()");
        Expr expr = comparison();

        while (match(Token.TokenType.NOTEQUAL, Token.TokenType.ISEQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private boolean match(Token.TokenType... types) {
//        smalldebug("match()");
        for (Token.TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(Token.TokenType type) {
//        smalldebug("check()");
        if (isAtEnd())
            return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()){
            current++;
        }
//        if(debugger >= 3){
//            System.out.println("\u001B[32m Current Token: \u001B[0m"+this.peek());
//        }
        return previous();
    }



    private boolean isAtEnd() {
//        smalldebug("isAtEnd()");
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Expr comparison() {
//        expressiondebug("Expr comparison()");
        Expr expr = term();

        while (match(GREATER, GREATER_EQUAL, LESSER, LESSER_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr term() {
//        expressiondebug("Expr term()");
        Expr expr = factor();

        while (match(MINUS, PLUS, CONCAT)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factor() {
//        expressiondebug("Expr factor()");
        Expr expr = unary();

        while (match(DIVIDE, MULTIPLY)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
//        expressiondebug("Expr unary()");
        if (match(NOT, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return primary();
    }

    private Expr primary() {
//        expressiondebug("Expr primary()");
        if (match(RIGHT_SQUARE)) {
            return new Expr.Literal(']');
        }

//        System.err.println("Expresion: " + peek().type);
        if (match(FALSE))
            return new Expr.Literal(false);
        if (match(TRUE))
            return new Expr.Literal(true);

        if (match(NUMBER, CHARACTER, TRUE, FALSE, STRING)) {
            return new Expr.Literal(previous().literal);
        }


        if(match(LEFT_SQUARE)) {
            if (match(RIGHT_SQUARE)) {
                return new Expr.Literal(""); // Or throw an error
            } else {
                String value = (String) peek().literal;
                System.out.println(value);
                advance();
                consume(RIGHT_SQUARE, "Expect ']' after expression.");
                return new Expr.Literal(value);
            }
        }

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        if (match(NEW_LINE)) {
            return new Expr.Literal('\n');
        }

        if (match(CONCAT)) {
            return new Expr.Literal("&");
        }

        if (match(IDENTIFIER)) {
            return new Expr.Variable(previous());
        }

//        System.out.println(peek());

        throw error(peek(), "Expect expression after \"" + peek().lexeme + "\".");

    }

    private Token consume(Token.TokenType type, String message) {
//        smalldebug("Token consume()");
        if (check(type))
            return advance();

        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
//        smalldebug("ParseError error()");
        Error.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
//        smalldebug("synchronize()");
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

    public void expressiondebug(String message){
        if(debugger >=4 ){
            System.out.println("\u001B[31m Current Function: \u001B[0m"+message);
        }
    }
    public void smalldebug(String message){
        if(debugger >= 5){
            System.out.println("\u001B[31m Current Function: \u001B[0m"+message);
        }
        return;
    }
    public void debug(String message){
        // something is modified in the advance method
        if(debugger == 4){
            System.out.println("\u001B[31m Current Function: \u001B[0m"+message);
        }else if(debugger == 5){
            System.out.println("\u001B[31m Current Function: \u001B[0m"+message);
        }else if(debugger == 3){
            System.out.println("\u001B[31m Current Function: \u001B[0m"+message);
        }

        // print all tokens
        else if(debugger == 1 && once == 0){
            once = 1;
            this.tokens.forEach(token -> System.out.println(token.toString()  ));

            // print all function transition
        }else if(debugger == 2){
            System.out.println("\u001B[31m Current Function: \u001B[0m"+message);
            // print all function transition and tokens
        }
    }

}
