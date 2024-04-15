package Semantic;

import ErrorHandling.Error;
import ErrorHandling.RuntimeError;
import Lexical.Token;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, Object> values = new HashMap<>();
    private final Map<String, Object> dataType = new HashMap<>();
    final Environment enclosing;

    Environment() {
        enclosing = null;
        // values.put("sample", null);
    }

    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    void defineDataType(String name, Token.TokenType dataType) {
        this.dataType.put(name, dataType);
    }

    Object getDataType(Token name) {
        if (dataType.containsKey(name.lexeme)) {
            return dataType.get(name.lexeme);
        }

        if (enclosing != null)
            return enclosing.getDataType(name);

        throw new RuntimeError(name,
                "'" + name.lexeme + "' is undefined.");
    }

    void define(String name, Object value) {
        values.put(name, value);
    }

    public boolean isDefined(String name) {
        return values.containsKey(name);
    }

    Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        if (enclosing != null)
            return enclosing.get(name);

        throw new RuntimeError(name,
                "Attempting to access '" + name.lexeme + "', but was undefined.");
    }

    Object get(String key) {
        // search
        if (values.containsKey(key)) {
            return values.get(key);
        }

        // if (enclosing != null)
        // return enclosing.get(key);

        // Error.report("No values");
        return "0";
    }

    public String printValues() {
        for (Object value : values.values()) {
            System.out.println("Value: " + value);
        }

        return "";
    }

    public int countValues() {
        return values.size();
    }

    void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name,
                "Cannot found " + name.lexeme + "variable, undefined variable '" + name.lexeme + "'.");
    }
}
