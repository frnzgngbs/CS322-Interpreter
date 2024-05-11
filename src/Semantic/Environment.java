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

//    public void removeValue(String identifier){
//        values.remove(identifier);
//    }
//
//    public void removeDataType(String dataType) {
//        this.dataType.remove(dataType);
//    }
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
                "Cannot find symbol \"" + name.lexeme + "\".");
    }

    Object get(String key) {
        // search
        if (values.containsKey(key)) {
            return values.get(key);
        }

        return "0";
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
