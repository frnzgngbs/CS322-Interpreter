package Semantic;

import ErrorHandling.Error;
import ErrorHandling.RuntimeError;
import Lexical.Token;
import Semantic.Stmt.EscapeCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

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
        Object variableType = environment.getDataType(expr.name);

////            System.out.println("NAA DIRI ANG LINE 2");
//
//            System.out.println(value);
//            System.out.println(variableType);

        if (variableType == Token.TokenType.FLOAT && value instanceof Integer) {
            value = ((Integer) value).floatValue();
        } else if (variableType == Token.TokenType.INT && value instanceof Float) {
            value = ((Float) value).intValue();
        } else if(variableType == Token.TokenType.CHAR && (value instanceof Float || value instanceof Boolean || value instanceof Integer)) {
            Error.error(expr.name,  "'" + value + "' is an invalid value for CHAR type.");
        } else if(variableType == Token.TokenType.BOOL && (value instanceof Float || value instanceof  Boolean || value instanceof Character || value instanceof String)) {
            if(!(value.equals("TRUE") || value.equals("FALSE"))) {
                Error.error(expr.name, "'"  + value + "' is an invalid value for BOOL type.");
            }
        } else {
            if ((value.equals("TRUE") || value.equals("FALSE"))) {
                Error.error(expr.name, "'"  + value + "' is an invalid value for BOOL type.");
            }
        }

        environment.assign(expr.name, value);
        return value;

    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        if (expr.operator.type == Token.TokenType.CONCAT && left instanceof String && right instanceof String) {
            return (String) left + (String) right;
        }

        switch (expr.operator.type) {
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                if (left instanceof Float && right instanceof Float)
                    return (float) left > (float) right;
                if (left instanceof Integer && right instanceof Integer)
                    return (int) left > (int) right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                if (left instanceof Float && right instanceof Float)
                    return (float) left >= (float) right;
                if (left instanceof Integer && right instanceof Integer)
                    return (int) left >= (int) right;
            case LESSER:
                checkNumberOperands(expr.operator, left, right);
                if (left instanceof Float && right instanceof Float)
                    return (float) left < (float) right;
                if (left instanceof Integer && right instanceof Integer)
                    return (int) left < (int) right;
            case LESSER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                if (left instanceof Float && right instanceof Float)
                    return (float) left <= (float) right;
                if (left instanceof Integer && right instanceof Integer)
                    return (int) left <= (int) right;
            case NOTEQUAL:
                return !isEqual(left, right);
            case ISEQUAL:
                return isEqual(left, right);
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                if (left instanceof Float && right instanceof Float)
                    return (float) left - (float) right;
                if (left instanceof Integer && right instanceof Integer)
                    return (int) left - (int) right;
            case PLUS:
                if (left instanceof Float && right instanceof Float)
                    return (float) left + (float) right;
                if (left instanceof Integer && right instanceof Integer)
                    return (int) left + (int) right;
                if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }
                throw new RuntimeError(expr.operator,
                        "Operands must be two numbers or two strings.");
            case DIVIDE:
                checkNumberOperands(expr.operator, left, right);
                if (left instanceof Float && right instanceof Float)
                    return (float) left / (float) right;
                if (left instanceof Integer && right instanceof Integer)
                    return (int) left / (int) right;
            case MULTIPLY:
                checkNumberOperands(expr.operator, left, right);
                if (left instanceof Float && right instanceof Float)
                    return (float) left * (float) right;
                if (left instanceof Integer && right instanceof Integer)
                    return (int) left * (int) right;
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
    public Void visitCodeEscapeStmt(EscapeCode stmt) {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method
        // 'visitCodeEscapeStmt'");
        System.err.println("is it here?");
        return null;
    }

    @Override
    public Object visitEscapeCodeExpr(Semantic.Expr.EscapeCode expr) {
//        String sentence = ((Token) expr.whatever).lexeme;
//        StringBuilder replacedSentence = new StringBuilder(sentence);
//        // System.err.println("ESCAPE: " + ((Token) expr.whatever).defineEscape());
//        String[] wordsArray = sentence.split("\\s+"); // Split the sentence by whitespace
//        List<String> wordsList = Arrays.asList(wordsArray); // Convert array to list
//        String newSentence = "";
//
//        int maybeVar = 0;
//        boolean unInitialized = false;
//        List<Object> uninitializedValues = new ArrayList<>();
//        for (String word : wordsList) {
//            Object valueObj = environment.get(word);
//            if (valueObj != null) {
//                String value = valueObj.toString();
//                // System.out.println("word = " + word + ", return = " + value);
//                if (!value.equals("0")) {
//                    maybeVar++;
//                    int index = replacedSentence.indexOf(word);
//                    while (index != -1) {
//                        replacedSentence.replace(index, index + word.length(), value);
//                        index = replacedSentence.indexOf(word, index + value.length());
//                    }
//                    // System.out.println("value associated with key = " + value);
//                }
//            } else if (valueObj == null) {
//                uninitializedValues.add(word);
//                unInitialized = true;
//                // System.out.println("valueobj is null");
//            }
//
//            // Convert StringBuilder back to String
//            newSentence = replacedSentence.toString();
//            // System.out.println("maybevar = " + maybeVar);
//            if (unInitialized) {
//                String finVal = "";
//                for (Object str : uninitializedValues) {
//                    finVal = finVal + str;
//                }
//                Error.report("Unable to print uninitialized variable inside [] => " + finVal + " ...");
//            }
//
//        }
//
//        // mutate
//        ((Token) expr.whatever).lexeme = newSentence;
//        return ((Token) expr.whatever).defineEscape();
        return null;
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
                return -(float) right;
        }

        // Unreachable.
        return null;
    }

    private void checkNumberOperands(Token operator,
            Object left, Object right) {
        if (left instanceof Float && right instanceof Float)
            return;
        if (left instanceof Integer && right instanceof Integer)
            return;

        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private boolean isTruthy(Object object) {
        if (object == null)
            return false;
        if (object instanceof Boolean)
            return (boolean) object;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null)
            return true;
        if (a == null)
            return false;

        return a.equals(b);
    }

    private String stringify(Object object) {
        if (object == null)
            return "null";

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

    @Override
    public Void visitScanStmt(Stmt.Scan stmt) {
        Scanner scanner = new Scanner(System.in);
        String inputLine = scanner.nextLine();
        String[] inputValues = inputLine.split("\\s+");

        if (inputValues.length != stmt.variableExpressions.size()) {
            Error.scanError("ERROR: Number of input values does not match the number of specified variables.");
        }

        int i = 0;
        for (Expr variableExpression : stmt.variableExpressions) {
            if (!(variableExpression instanceof Expr.Variable)) {
                throw new RuntimeException("Invalid variable expression in scan statement.");
            }
            Expr.Variable variable = (Expr.Variable) variableExpression;

            Object dataType = environment.getDataType(variable.name);

            Object value;
            String inputValue = inputValues[i++];
            if (dataType == Token.TokenType.INT) {
                value = Integer.parseInt(inputValue);
            } else if (dataType == Token.TokenType.FLOAT) {
                value = Float.parseFloat(inputValue);
            } else if (dataType == Token.TokenType.CHAR) {
                if (inputValue.length() != 1) {
                    Error.error(variable.name, "'" + inputValue + "' is an invalid character input.");
                }
                value = inputValue.charAt(0);
            } else if (dataType == Token.TokenType.BOOL) {
                value = inputValue;
                if (!(value.equals("TRUE") || value.equals("FALSE"))) {
                    Error.error(variable.name, "'" + value + "' is an invalid value for BOOL type.");
                }

            } else {
                throw new RuntimeException("Unsupported variable type in scan statement.");
            }

            environment.define(variable.name.lexeme, value);
        }
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
        StringBuilder builder = new StringBuilder();

        for (Expr expression : stmt.expression) {
            Object value = evaluate(expression);

            System.out.println(value);
            if (value != null && value.equals("+"))
                continue;

            // Check if value is not null before using it
            builder.append(stringify(value));
        }

        System.out.print(builder.toString());
        return null;
    }

    @Override
    public Void visitVariableStmt(Stmt.Variable stmt) {

        if (environment.isDefined(stmt.name.lexeme)) {
            // Check if the existing variable has the same data type
            Token.TokenType existingDataType = (Token.TokenType) environment.getDataType(stmt.name);

            if (existingDataType != stmt.dataType) {
                throw new RuntimeError(stmt.name,
                        "Variable '" + stmt.name.lexeme + "' already declared with a different data type.");
            } else {
                Error.error(stmt.name, "Variable " + "'" + stmt.name.lexeme + "' has already been defined.");
            }

        }

        environment.defineDataType(stmt.name.lexeme, stmt.dataType);

        Object value = null;
        if (stmt.initializer != null) {
            Object initialValue = evaluate(stmt.initializer);
            Object dataType = environment.getDataType(stmt.name);
            // System.out.println(dataType);
            if (dataType == Token.TokenType.FLOAT) {
                if (initialValue instanceof Integer) {
                    value = (float) ((Integer) initialValue);
                } else if (initialValue instanceof Float) {
                    value = initialValue; // No need for conversion
                } else {
                    Error.error(stmt.name, "Invalid value for FLOAT type.");
                }
            } else if (dataType == Token.TokenType.INT) {
                if (initialValue instanceof Float) {
                    value = (int) Math.floor((Float) initialValue);
                } else if (initialValue instanceof Integer) {
                    value = initialValue; // No need for conversion
                } else {
                    Error.error(stmt.name, "Invalid value for INT type.");
                }
            } else if (dataType == Token.TokenType.CHAR) {
                value =initialValue;
                if (initialValue instanceof Boolean || initialValue instanceof Integer || initialValue instanceof Float
                        || initialValue instanceof String) {
                    Error.error(stmt.name, "Invalid value for CHAR type.");
                }
            } else {
                    value = initialValue;
                    if (initialValue instanceof Character || initialValue instanceof Integer || initialValue instanceof Float) {
                        Error.error(stmt.name, "Invalid value for BOOL type.");
                    }
                    if ((value.equals("TRUE") || value.equals("FALSE"))) {
                    } else {
                        Error.error(stmt.name, value + " is an invalid value for a BOOL type");
                    }
                }
        }
        environment.define(stmt.name.lexeme, value);
        return null;

    }


    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }

}