package chessmaster.storage;

import chessmaster.exceptions.ChessMasterException;
import chessmaster.exceptions.InvalidMoveException;
import chessmaster.exceptions.LoadBoardException;
import chessmaster.game.ChessBoard;
import chessmaster.game.ChessTile;
import chessmaster.game.Color;
import chessmaster.parser.Parser;
import chessmaster.pieces.ChessPiece;
import chessmaster.user.CPU;
import chessmaster.user.Human;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExecuteSavedMovesTest {
    private static final String FILE_PATH_STRING =
            "src/test/resources/storageTest.txt";
    private static final String INVALID_FILE_PATH_STRING =
            "src/test/resources/storageTest_invalidMove.txt";
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

    @Test
    public void testExecuteSavedMoves_validMoves() throws ChessMasterException {
        Storage storage = new Storage(FILE_PATH_STRING);
        ChessBoard otherBoard = loadChessBoard();
        ChessBoard board = new ChessBoard(Color.WHITE);
        Human human = new Human(Color.WHITE, board);
        CPU cpu = new CPU(Color.BLACK, board);

        storage.executeSavedMoves(Color.WHITE, otherBoard, board, human, cpu);
    }
    @Test
    public void testExecuteSavedMoves_invalidMoves_expectInvalidMoveException() throws ChessMasterException {
        Storage storage = new Storage(INVALID_FILE_PATH_STRING);
        ChessBoard otherBoard = loadChessBoard();
        ChessBoard board = new ChessBoard(Color.WHITE);
        Human human = new Human(Color.WHITE, board);
        CPU cpu = new CPU(Color.BLACK, board);

        assertThrows(InvalidMoveException.class,
                () -> storage.executeSavedMoves(Color.WHITE, otherBoard, board, human, cpu));
    }
    @Test
    public void testExecuteSavedMoves_boardMismatch_expectLoadBoardException() throws ChessMasterException {
        Storage storage = new Storage(FILE_PATH_STRING);
        ChessBoard otherBoard = new ChessBoard(Color.WHITE);
        ChessBoard board = new ChessBoard(Color.WHITE);
        Human human = new Human(Color.WHITE, board);
        CPU cpu = new CPU(Color.BLACK, board);

        assertThrows(LoadBoardException.class,
                () -> storage.executeSavedMoves(Color.WHITE, otherBoard, board, human, cpu));
    }
}
