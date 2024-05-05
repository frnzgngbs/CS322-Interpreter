package Semantic;

import Lexical.Token;

import java.util.List;

public abstract class Stmt {
    interface Visitor<R> {
        R visitExpressionStmt(Expression stmt);

        R visitDisplayStmt(Display stmt);

        R visitVariableStmt(Variable stmt);

        R visitIfStmt(If stmt);

        R visitCodeStructureStmt(CodeStructure stmt);

        R visitCodeEscapeStmt(EscapeCode stmt);

        R visitScanStmt(Scan stmt); // Add this method for handling Scan statement

        R visitWhileStmt(While stmt);
    }

    static class Expression extends Stmt {
        Expression(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }

        final Expr expression;
    }
    

    // INT a = (1+2) * 3 (2/2)
    // static class If extends Stmt {
    // If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
    // this.condition = condition;
    // this.thenBranch = thenBranch;
    // this.elseBranch = elseBranch;
    // }
    //
    // @Override
    // <R> R accept(Visitor<R> visitor) {
    // return visitor.visitIfStmt(this);
    // }
    //
    // final Expr condition;
    // final Stmt thenBranch;
    // final Stmt elseBranch;
    // }
    // < stmt-if
    // > stmt-print
    static class Display extends Stmt {
        Display(List<Expr> expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitDisplayStmt(this);
        }

        final List<Expr> expression;
    }

    static class Scan extends Stmt {
        Scan(List<Expr> variableExpressions) {
            this.variableExpressions = variableExpressions;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitScanStmt(this);
        }

        final List<Expr> variableExpressions;
    }

    static class Variable extends Stmt {
        Variable(Token.TokenType dataType, Token name, Expr initializer) {
            this.dataType = dataType;
            this.name = name;
            this.initializer = initializer;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableStmt(this);
        }

        final Token.TokenType dataType;
        final Token name;
        final Expr initializer;
    }

    static class CodeStructure extends Stmt {

        CodeStructure(List<Stmt> statements) {
            this.statements = statements;
        }

        final List<Stmt> statements;

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitCodeStructureStmt(this);
        }
    }

    static class EscapeCode extends Stmt {

        EscapeCode(List<Expr> statements) {
            this.statements = statements;
        }

        final List<Expr> statements;

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitCodeEscapeStmt(this);
        }
    }

    static class If extends Stmt {
        If(List<Expr> conditions, List<List<Stmt>> thenBranch, List<Stmt> elseBranch) {
            this.conditions = conditions;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }

        final List<Expr> conditions;
        final List<List<Stmt>> thenBranch;
        final List<Stmt> elseBranch;
    }

    static class While extends Stmt {
        final List<Expr> condition;
        final List<List<Stmt>> body;

        public While(List<Expr> condition, List<List<Stmt>> body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }
    }


    abstract <R> R accept(Visitor<R> visitor);
}