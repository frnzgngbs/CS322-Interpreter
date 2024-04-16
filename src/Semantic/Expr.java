package Semantic;

import Lexical.Token;

import java.util.List;

public abstract class Expr {
    interface Visitor<R> {
        R visitAssignExpr(Assign expr);

        R visitBinaryExpr(Binary expr);

        R visitGroupingExpr(Grouping expr);

        R visitLiteralExpr(Literal expr);

        R visitLogicalExpr(Logical expr);

        R visitUnaryExpr(Unary expr);

        R visitVariableExpr(Variable expr);

        R visitEscapeCodeExpr(EscapeCode expr);
    }

    // Nested Expr classes here...
    // > expr-assign
    static class Assign extends Expr {
        Assign(Token name, Expr value) {
            this.name = name;
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }

        final Token name;
        final Expr value;
    }

    // < expr-assign
    // > expr-binary
    static class Binary extends Expr {
        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }

        final Expr left;
        final Token operator;
        final Expr right;
    }

    static class Grouping extends Expr {
        Grouping(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        final Expr expression;
    }

    // < expr-grouping
    // > expr-literal
    static class Literal extends Expr {
        Literal(Object value) {
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        final Object value;
    }

    // < expr-literal
    // > expr-logical
    static class Logical extends Expr {
        Logical(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
        }

        final Expr left;
        final Token operator;
        final Expr right;
    }
    // < expr-logical

    static class Unary extends Expr {
        Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }

        final Token operator;
        final Expr right;
    }

    // < expr-unary
    // > expr-variable
    static class Variable extends Expr {
        Variable(Token name) {
            this.name = name;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }

        final Token name;
    }
    // < expr-variable

    // // > escape-variable
    // static class EscapeCode extends Expr {
    // EscapeCode(Token name) {
    // this.name = name;
    // }

    // @Override
    // <R> R accept(Visitor<R> visitor) {
    // return visitor.visitEscapeCodeExpr(this);
    // }

    // final Token name;
    // }
    // // < escape-variable

    // > escape-variable
    static class EscapeCode extends Expr {
        EscapeCode(Object name, Object whatever) {
            this.name = name;
            this.whatever = whatever;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitEscapeCodeExpr(this);
        }

        final Object name;
        final Object whatever;
    }
    // < escape-variable

    abstract <R> R accept(Visitor<R> visitor);
}