import Lexical.Lexer;
import Lexical.Token;
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
    private static final List<Token> tokens = new ArrayList<>();

    public static void main(String[] args) {
        try {
            String filePath = null;
            if(args.length == 0) {
                filePath = "C:\\Users\\ardon\\Documents\\CS322-Interpreter\\testcase.txt";
            }else{
                filePath = args[0];
            }

            String source = readFile(filePath);
            System.out.println(source);
            tokenize(source);
        } catch (FileNotFoundException | StringIndexOutOfBoundsException fe) {
            System.out.println(fe.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readFile(String filePath) throws IOException {
        StringBuilder source = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        /*
         Track if it's the first line
         If it is the first line, do not add a new line
         as it will increment the line attribute in our lexer.java

         */

        boolean firstLine = true;
        while ((line = reader.readLine()) != null) {

            if (!firstLine) {
                source.append('\n');
            } else {
                firstLine = false;
            }

            if (line.trim().startsWith("DISPLAY:")) {
                StringBuilder display_builder = new StringBuilder();

                for(char c : line.toCharArray()) {
                    display_builder.append(c);
                }

                display_builder.append(" EOD");
                source.append(display_builder.toString());
            } else {
                source.append(line);
            }
        }

        reader.close();
        return source.toString();
    }

    private static void tokenize(String source) {
        Lexer lexer = new Lexer(source);
        List<Token> tokenize = lexer.scanTokens();

         tokenize.forEach((a) -> {
             System.out.println(a);
         });

         tokens.addAll(tokenize);
        parseToken(tokens);

}

    private static void parseToken(List<Token> tokens) {
        int debug = debug = 4;
        Parser parser = new Parser(tokens,debug);

        List<Stmt> statements = parser.parse();

        interpreter.interpret(statements);
    }
}
