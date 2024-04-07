package Semantic;

import ASTree.Node;
import Lexical.Token;
import ErrorHandling.Error;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SemanticAnalyzer {
    private Map<String, String> symbolTable; // Store variable names and types

    public SemanticAnalyzer() {
        symbolTable = new HashMap<>();
    }

    public void analyze(Node rootNode) {
        // Initialize the symbol table
        symbolTable.clear();

        // Perform semantic analysis starting from the root node
        traverse(rootNode, 1); // Start with line 1
    }

    private void traverse(Node node, int line) {
        switch (node.getType()) {
            case "PROGRAM":
                analyzeProgram(node, line);
                break;
            case "DECLARATION_STATEMENT":
                analyzeDeclaration(node, line);
                break;
            case "ASSIGNMENT_STATEMENT":
                analyzeAssignment(node, line);
                break;
            case "DISPLAY_STATEMENT":
                analyzeDisplay(node, line);
                break;
            // Add cases for other node types as needed
        }

        // Continue traversing the children nodes
        for (Node child : node.getChildren()) {
            traverse(child, line); // Pass line number to child nodes
        }
    }

    private void analyzeProgram(Node node, int line) {
        // Perform semantic analysis specific to program node
        // For example, check for duplicate declarations, etc.
    }

    private void analyzeDeclaration(Node node, int line) {
        Node dataTypeNode = node.getChildren().get(0);
        Node identifierNode = node.getChildren().get(1);

        String dataType = dataTypeNode.getValue();
        String identifier = identifierNode.getValue();

        // Check if the variable is already declared
        if (symbolTable.containsKey(identifier)) {
            Error.error(line, "Variable '" + identifier + "' is already declared");
        } else {
            // Add the variable to the symbol table
            symbolTable.put(identifier, dataType);
        }
    }

    private void analyzeAssignment(Node node, int line) {
        Node identifierNode = node.getChildren().get(0);
        Node expressionNode = node.getChildren().get(1);

        String identifier = identifierNode.getValue();
        String expectedType = symbolTable.get(identifier);

        // Perform semantic analysis on the expression
        String expressionType = analyzeExpression(expressionNode, line);

        // Check if the assignment is valid
        if (!expectedType.equals(expressionType)) {
            Error.error(line, "Type mismatch in assignment for variable '" + identifier + "'");
        }
    }

    private String analyzeExpression(Node node, int line) {
        // Perform semantic analysis on the expression node
        // This is a simplified example, you'll need to expand it to handle more
        // expression types
        if (node.getChildren().isEmpty()) {
            // Leaf node - variable or literal
            String value = node.getValue();
            if (symbolTable.containsKey(value)) {
                // It's a variable, return its type
                return symbolTable.get(value);
            } else {
                // It's a literal, determine its type
                if (value.startsWith("'") && value.endsWith("'")) {
                    // CHAR literal
                    return "CHAR";
                } else if (value.equals("TRUE") || value.equals("FALSE")) {
                    // BOOL literal
                    return "BOOL";
                } else {
                    // INT or FLOAT literal (assuming all numbers are integers for simplicity)
                    return "INT";
                }
            }
        } else {
            // Non-leaf node - operator
            // For simplicity, just return the type of the first child
            return analyzeExpression(node.getChildren().get(0), line);
        }
    }

    private void analyzeDisplay(Node node, int line) {
        // Perform semantic analysis specific to display statement
        // You can expand this method to perform additional checks if needed
    }
}
