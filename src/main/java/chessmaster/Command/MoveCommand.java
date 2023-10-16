package chessmaster.Command;

import chessmaster.exceptions.InvalidMoveException;
import chessmaster.game.ChessBoard;
import chessmaster.game.Move;

public class MoveCommand extends Command{
    protected Move move;
    public MoveCommand(ChessBoard board, Move move) {
        super(board);
        this.move = move;
    }

    @Override
    public void execute() {
        try {
            board.movePiece(move);
        } catch (InvalidMoveException e) {

        }
    }
}
