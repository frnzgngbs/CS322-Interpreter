package Semantic;

import Lexical.Token;

import java.util.List;

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

    public static class Block extends Stmt {
        public final List<Stmt> statements;  // List of statements in the block

        public Block(List<Stmt> statements) {
            this.statements = statements;
        }

        // Method to add a statement to the block
        public void addStatement(Stmt statement) {
            statements.add(statement);
        }

        // Method to get the list of statements in the block
        public List<Stmt> getStatements() {
            return statements;
        }
    }

}
