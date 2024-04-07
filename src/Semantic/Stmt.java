package Semantic;

import Lexical.Token;

public abstract class Stmt {
    public static class VariableDeclaration extends Stmt {
        public final Token name;
        public final Token type;
        public final Expr initializer;

        public VariableDeclaration(Token name, Token type, Expr initializer) {
            this.name = name;
            this.type = type;
            this.initializer = initializer;
        }
    }
}
