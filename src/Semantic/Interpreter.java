    package Semantic;

    import ErrorHandling.Error;
    import ErrorHandling.RuntimeError;
    import Lexical.Token;

    import java.util.List;

    public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
        final Environment globals = new Environment();
        private Environment environment = globals;



        public void interpret(List<Stmt> statements) {
            try {
                for (Stmt statement : statements) {
                    execute(statement);
                }
            } catch (RuntimeError error) {
                Error.runtimeError(error);
            }
        }

        @Override
        public Object visitAssignExpr(Expr.Assign expr) {
            Object value = evaluate(expr.value);
            environment.assign(expr.name, value);
            return value;
        }

        @Override
        public Object visitBinaryExpr(Expr.Binary expr) {
            Object left = evaluate(expr.left);
            Object right = evaluate(expr.right);


            if (expr.operator.type == Token.TokenType.CONCAT && left instanceof String && right instanceof String) {
                return (String)left + (String)right;
            }

            switch (expr.operator.type) {
                case GREATER:
                    checkNumberOperands(expr.operator, left, right);
                    if (left instanceof Double && right instanceof Double) return (float) left > (float) right;
                    if (left instanceof Integer && right instanceof Integer) return (int) left > (int) right;
                case GREATER_EQUAL:
                    checkNumberOperands(expr.operator, left, right);
                    if (left instanceof Float && right instanceof Float) return (float) left >= (float) right;
                    if (left instanceof Integer && right instanceof Integer) return (int) left >= (int) right;
                case LESSER:
                    checkNumberOperands(expr.operator, left, right);
                    if (left instanceof Float && right instanceof Float) return (float) left < (float) right;
                    if (left instanceof Integer && right instanceof Integer) return (int) left < (int) right;
                case LESSER_EQUAL:
                    checkNumberOperands(expr.operator, left, right);
                    if (left instanceof Float && right instanceof Float) return (float) left <= (float) right;
                    if (left instanceof Integer && right instanceof Integer) return (int) left <= (int) right;
                case NOTEQUAL: return !isEqual(left, right);
                case ISEQUAL: return isEqual(left, right);
                case MINUS:
                    checkNumberOperands(expr.operator, left, right);
                    if (left instanceof Float && right instanceof Float) return (float)left - (float)right;
                    if (left instanceof Integer && right instanceof  Integer) return (int)left - (int)right;
                case PLUS:
                    if (left instanceof Float && right instanceof Float) return (float)left + (float)right;
                    if (left instanceof Integer && right instanceof  Integer) return (int)left + (int)right;
                    if (left instanceof String && right instanceof String) {
                        return (String)left + (String)right;
                    }
                    throw new RuntimeError(expr.operator,
                            "Operands must be two numbers or two strings.");
                case DIVIDE:
                    checkNumberOperands(expr.operator, left, right);
                    return (float)left / (float)right;
                case MULTIPLY:
                    checkNumberOperands(expr.operator, left, right);
                    return (float)left * (float)right;
            }

            // Unreachable.
            return null;
        }

        @Override
        public Object visitGroupingExpr(Expr.Grouping expr) {
            return evaluate(expr.expression);
        }

        @Override
        public Object visitLiteralExpr(Expr.Literal expr) {
            return expr.value;
        }

        @Override
        public Object visitLogicalExpr(Expr.Logical expr) {
            return null;
        }

        @Override
        public Object visitUnaryExpr(Expr.Unary expr) {
            Object right = evaluate(expr.right);

            switch (expr.operator.type) {
                case NOT:
                    return !isTruthy(right);
                case MINUS:
                    return -(float)right;
            }

            // Unreachable.
            return null;
        }

        private void checkNumberOperands(Token operator,
                                         Object left, Object right) {
            if (left instanceof Float && right instanceof Float) return;
            if (left instanceof Integer && right instanceof Integer) return;

            throw new RuntimeError(operator, "Operands must be numbers.");
        }


        private boolean isTruthy(Object object) {
            if (object == null) return false;
            if (object instanceof Boolean) return (boolean)object;
            return true;
        }

        private boolean isEqual(Object a, Object b) {
            if (a == null && b == null) return true;
            if (a == null) return false;

            return a.equals(b);
        }

        private String stringify(Object object) {
            if (object == null) return "null";

            if (object instanceof Double) {
                String text = object.toString();
                if (text.endsWith(".0")) {
                    text = text.substring(0, text.length() - 2);
                }
                return text;
            }

            return object.toString();
        }

        @Override
        public Object visitVariableExpr(Expr.Variable expr) {
            return environment.get(expr.name);
        }
        private Object evaluate(Expr expr) {
            return expr.accept(this);
        }

        private void execute(Stmt stmt) {
            stmt.accept(this);
        }

        @Override
        public Void visitCodeStructureStmt(Stmt.CodeStructure stmt) {
            executeCodeStructure(stmt.statements, new Environment(environment));
            return null;
        }

        void executeCodeStructure(List<Stmt> statements,
                          Environment environment) {
            Environment previous = this.environment;
            try {
                this.environment = environment;

                for (Stmt statement : statements) {
                    execute(statement);
                }
            } finally {
                this.environment = previous;
            }
        }
        @Override
        public Void visitExpressionStmt(Stmt.Expression stmt) {
            evaluate(stmt.expression);
            return null;
        }

        @Override
        public Void visitDisplayStmt(Stmt.Display stmt) {
            Object value = evaluate(stmt.expression);
            System.out.println(stringify(value));
            return null;
        }

        @Override
        public Void visitVariableStmt(Stmt.Variable stmt) {
            Object value = null;
            if (stmt.initializer != null) {
                Object initialValue = evaluate(stmt.initializer);
                if (stmt.dataType == Token.TokenType.FLOAT) {
                    if (initialValue instanceof Integer) {
                        value = (float) ((Integer) initialValue);
                    } else {
                        value = initialValue; // No need for conversion
                    }
                } else if (stmt.dataType == Token.TokenType.INT) {
                    if (initialValue instanceof Float) {
                        value = (int) Math.floor((Float) initialValue);
                    } else {
                        value = initialValue; // No need for conversion
                    }
                }
            }
            environment.define(stmt.name.lexeme, value);
            return null;
        }


        @Override
        public Void visitIfStmt(Stmt.If stmt) {
            return null;
        }

    }