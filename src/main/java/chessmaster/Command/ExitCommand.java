package chessmaster.Command;

import chessmaster.game.ChessBoard;

public class ExitCommand extends Command{
    public final static String commandString = "exit";

    public ExitCommand(ChessBoard board) {
        super(board);
    }

    @Override
    public void execute() {
        //TODO: Add exit confirmation page
        //TODO: Add exit message display
    }
}
