package chessmaster.exceptions;

public class PromoteException extends ChessMasterException {
    public PromoteException() {
        super(ExceptionMessages.MESSAGE_PROMOTE_EXCEPTION);
    }

    public PromoteException(String message) {
        super(message);
    }
}
