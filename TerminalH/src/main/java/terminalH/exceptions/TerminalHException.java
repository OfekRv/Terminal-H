package terminalH.exceptions;

public class TerminalHException extends Exception {
    public TerminalHException(String message) {
        super(message);
    }

    public TerminalHException(String message, Throwable cause) {
        super(message, cause);
    }
}
