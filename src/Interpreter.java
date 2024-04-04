import Lexical.Lexer;
import Lexical.Token;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Interpreter {
    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\ardon\\Documents\\CS322-Interpreter\\testcase.txt"));
            String line;
            while((line = reader.readLine()) != null) {
                System.out.println(line);
                run(line);
            }
        } catch (FileNotFoundException | StringIndexOutOfBoundsException fe) {
            System.out.println(fe.getMessage());
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    private static void run(String source) {
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();


        for (Token token : tokens) {
            System.out.println("TOKEN: " + token);
        }
    }
}
