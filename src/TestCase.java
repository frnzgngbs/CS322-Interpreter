import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.io.BufferedReader;
public class TestCase {
        public static void main(String[] args) {
            // ANSI color codes
            String RED = "\033[0;31m";
            String NC = "\033[0m"; // No Color

            // Compile Java files
            try {
                Process compileProcess = Runtime.getRuntime().exec("javac src/**/*.java");
                compileProcess.waitFor();
            } catch (Exception e) {
                handle_error("Compilation failed: " + e.getMessage(), RED, NC);
                return;
            }

            // Test cases directory
            String test_case_dir = "./testcase";

            // Test cases
            String[] test_cases = { "testcase1.txt", "testcase2.txt", "testcase3.txt" };

            // Loop through test cases
            for (String testcase : test_cases) {
                System.out.println("Running test case: " + testcase);
                try {
                    Process javaProcess = Runtime.getRuntime().exec("java -classpath ./src Main " + test_case_dir + "/" + testcase);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(javaProcess.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                    javaProcess.waitFor();
                    if (javaProcess.exitValue() == 0) {
                        System.out.println("-------------------------------------Test case " + testcase + " succeeded");
                    } else {
                        handle_error("Test case " + testcase + " failed", RED, NC);
                    }
                } catch (Exception e) {
                    handle_error("An error occurred: " + e.getMessage(), RED, NC);
                }
            }
        }

        // Function to handle errors
        public static void handle_error(String message, String RED, String NC) {
            System.out.println(RED + message + NC);
            // You can add additional error handling logic here
        }

}
