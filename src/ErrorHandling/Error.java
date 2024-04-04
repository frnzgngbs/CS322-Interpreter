package ErrorHandling;

public class Error {

    static boolean isError = false;

    public static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where,
                               String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
        isError = true;

        if(isError) System.exit(65);

    }
}
