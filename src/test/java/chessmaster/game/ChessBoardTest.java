package chessmaster.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import chessmaster.exceptions.ChessMasterException;
import chessmaster.parser.Parser;
import chessmaster.pieces.ChessPiece;
import chessmaster.user.CPU;
import chessmaster.user.Human;
import org.junit.jupiter.api.Test;

import chessmaster.ui.TextUI;

import java.util.ArrayList;

public class ChessBoardTest {

    private static final String[] MOVE_STRING_ARRAY =
        {"d2 d4", "g8 f6", "g1 f3", "b8 c6", "c1 f4", "d7 d5",
        "c2 c3", "f6 e4", "b1 d2", "e4 d2", "d1 d2", "f7 f5"};
    private static final String[][] MOVED_CHESSBOARD = {
            { "R", ".", "B", "Q", "K", "B", ".", "R" },
            { "P", "P", "P", ".", "P", ".", "P", "P" },
            { ".", ".", "N", ".", ".", ".", ".", "." },
            { ".", ".", ".", "P", ".", "P", ".", "." },
            { ".", ".", ".", "p", ".", "b", ".", "." },
            { ".", ".", "p", ".", ".", "n", ".", "." },
            { "p", "p", ".", "q", "p", "p", "p", "p" },
            { "r", ".", ".", ".", "k", "b", ".", "r" },
    };

    private ChessBoard loadChessBoard() {
        ChessTile[][] chessTiles = new ChessTile[ChessBoard.SIZE][ChessBoard.SIZE];
        for (int row = 0; row < ChessBoard.SIZE; row++) {
            for (int col = 0; col < ChessBoard.SIZE; col++) {
                String chessPieceString = MOVED_CHESSBOARD[row][col];
                ChessPiece initialPiece = Parser.parseChessPiece(chessPieceString, row, col);
                chessTiles[row][col] = new ChessTile(initialPiece);
                assert (chessTiles[row][col] != null);
            }
        }
        return new ChessBoard(Color.WHITE, chessTiles);
    }

    // @@author onx001
    @Test
    public void pointTest() {
        TextUI ui = new TextUI();
        ChessBoard board = new ChessBoard(Color.WHITE);
        
        ui.printChessBoard(board.getBoard());
        int points = board.getPoints(Color.WHITE);
        assertEquals(0, points);
    }
    //@@author ken_ruster
    @Test
    public void executeMoveArrayTest() throws ChessMasterException {
        ChessBoard board = new ChessBoard(Color.WHITE);
        Human human = new Human(Color.WHITE, board);
        CPU cpu = new CPU(Color.BLACK, board);
        ArrayList moveList = new ArrayList<String>();
        for (String moveString: MOVE_STRING_ARRAY) {
            moveList.add(moveString);
        }

        board.executeMoveArray(moveList, human, cpu);

        ChessBoard otherBoard = loadChessBoard();

        assertTrue(board.equals(otherBoard));
    }
}
