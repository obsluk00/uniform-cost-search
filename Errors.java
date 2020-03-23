public class Errors {
    public static void usageError(String msg) {
        System.err.println(msg);
        System.exit(2);
    }

    public static void fileError(String msg) {
        System.err.println("error reading input stream: " + msg);
        System.exit(1);
    }

    public static void inputError(String msg) {
        System.err.println("invalid input: " + msg);
        System.exit(1);
    }
}
