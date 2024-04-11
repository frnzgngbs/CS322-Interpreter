import ErrorHandling.Error;
import Lexical.Lexer;
import Lexical.Token;
import Semantic.Expr;
import Semantic.Interpreter;
import Semantic.Parser;
import Semantic.Stmt;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final Interpreter interpreter = new Interpreter();
    private static List<Token> tokens = new ArrayList<>();

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\ardon\\Documents\\CS322-Interpreter\\testcase.txt"));
            String line;
            int code_line = 1;
            while((line = reader.readLine()) != null) {
                tokenize(line, code_line++);
            }
//            if (count == 0) {
//                System.err.println("No begin code found.");
//            }

//            parseToken(tokens);
        } catch (FileNotFoundException | StringIndexOutOfBoundsException fe) {
            System.out.println(fe.getMessage());
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    private static void tokenize(String source, int line) {
        Lexer lexer = new Lexer(source, line);
        tokens = lexer.scanTokens();

//        for (Token token : tokens) {
//            System.out.println(token);
//        }

        parseToken(tokens);
    }

    private static void parseToken(List<Token> tokens) {
        Parser parser = new Parser(tokens);

        List<Stmt> statements = parser.parse();

        interpreter.interpret(statements);
    }
}
