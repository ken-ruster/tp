package chessmaster.exceptions;

public class ParseColorException extends ChessMasterException {
    public ParseColorException() {
        super(ExceptionMessages.MESSAGE_PARSE_COLOR_EXCEPTION);
    }

    public ParseColorException(String message) {
        super(message);
    }
}