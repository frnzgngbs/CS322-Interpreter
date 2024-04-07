package ErrorHandling;

public class Error {

    private static boolean isError = false;

    public static void error(int line, String message) {
        report(line, "", message);
    }

    public static boolean isError() {
        return isError;
    }

    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        isError = true;
    }
}
