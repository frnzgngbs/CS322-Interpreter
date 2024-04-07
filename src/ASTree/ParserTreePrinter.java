package ASTree;

import java.util.List;

import Lexical.Token;
import Semantic.Parser;

public class ParserTreePrinter {

    public static void printParserOutput(List<Token> tokens) {
        Parser parser = new Parser(tokens);

        System.out.println("- Parsing Output");
        // Parse the program
        try {
            parser.parse();
            // Print the AST tree structure
            printAST(parser.getRootNode(), 0);
        } catch (Exception e) {
            System.out.println("Error occurred during parsing: " + e.getMessage());
        }
    }

    // Method to print AST tree structure recursively
    private static void printAST(Node node, int indent) {
        if (node == null) {
            return;
        }
        // Print indentation
        for (int i = 0; i < indent; i++) {
            System.out.print("  ");
        }
        // Print node
        System.out.println(node.getType() + " " + node.getValue());
        // Print children recursively
        for (Node child : node.getChildren()) {
            printAST(child, indent + 1);
        }
    }
}

