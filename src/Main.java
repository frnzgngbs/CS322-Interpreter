
import Lexical.Lexer;
import Lexical.Token;
import Semantic.Parser;
import Semantic.SemanticAnalyzer;
import ErrorHandling.Error;
import ASTree.ParserTreePrinter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader("C:\\Users\\John Marc\\Documents\\pl_code\\CS322-Interpreter\\testcase.txt"));
            StringBuilder sourceBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sourceBuilder.append(line).append("\n");
            }
            reader.close();

            String source = sourceBuilder.toString();

            // Lexical analysis
            Lexer lexer = new Lexer(source);
            List<Token> tokens = lexer.scanTokens();

            // Syntax analysis (parsing)
            Parser parser = new Parser(tokens);
            parser.parse();

            // If there were parsing errors, exit
            if (Error.isError()) {
                return;
            }

            // Print the parser output
            ParserTreePrinter.printParserOutput(tokens);

            // Semantic analysis
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
            semanticAnalyzer.analyze(parser.getRootNode());

            // If there were semantic errors, exit
            if (Error.isError()) {
                return;
            }

            // Proceed with executing the program or any other post-processing steps
            System.out.println("Parsing and semantic analysis completed successfully.");

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}
