package Semantic;

import Lexical.Token;

import java.util.List;

public abstract class Stmt {
    interface Visitor<R> {
        R visitExpressionStmt(Expression stmt);
//        R visitIfStmt(If stmt);
        R visitDisplayStmt(Display stmt);
        R visitVariableStmt(Variable stmt);
        R visitIfStmt(If stmt);
        R visitCodeStructureStmt(CodeStructure stmt);


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
//    static class If extends Stmt {
//        If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
//            this.condition = condition;
//            this.thenBranch = thenBranch;
//            this.elseBranch = elseBranch;
//        }
//
//        @Override
//        <R> R accept(Visitor<R> visitor) {
//            return visitor.visitIfStmt(this);
//        }
//
//        final Expr condition;
//        final Stmt thenBranch;
//        final Stmt elseBranch;
//    }
    //< stmt-if
//> stmt-print
    static class Display extends Stmt {
        Display(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitDisplayStmt(this);
        }

        final Expr expression;
    }

    static class If extends Stmt {
        If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }

        final Expr condition;
        final Stmt thenBranch;
        final Stmt elseBranch;
    }

    static class Variable extends Stmt {
        Variable(Token name, Expr initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableStmt(this);
        }

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

    abstract <R> R accept(Visitor<R> visitor);
}