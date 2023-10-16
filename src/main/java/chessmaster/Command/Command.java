package chessmaster.Command;

import chessmaster.game.ChessBoard;

public class Command {
    protected ChessBoard board;
    protected String messageString;

    public Command(ChessBoard board) {
        this.board = board;
    }

    public void execute(){

    }
}
