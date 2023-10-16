package chessmaster.Command;

import chessmaster.game.ChessBoard;

public class ExitCommand extends Command{
    public final static String commandString = "Are you sure you want to exit the program? y/n";

    public ExitCommand(ChessBoard board) {
        super(board);
    }

    @Override
    public void execute() {
        //TODO: Add exit confirmation page
        //TODO: Add exit message display
    }
}
