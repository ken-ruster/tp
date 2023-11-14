package chessmaster.commands;

import chessmaster.exceptions.ChessMasterException;
import chessmaster.exceptions.NullPieceException;
import chessmaster.game.ChessBoard;
import chessmaster.game.Color;
import chessmaster.game.Game;
import chessmaster.storage.Storage;
import chessmaster.ui.TextUI;
import chessmaster.user.CPU;
import chessmaster.user.Human;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ShowMovesCommandTest {
    private static final String FILE_PATH_STRING = "data/ChessMaster.txt";
    private static final String[] AVAILABLE_COORDINATES_STRING = {"Available coordinates for Pawn at e2: ", "e3 e4 " };
    private static final String[] NO_AVAILABLE_STRING = {"There aren't any moves available for King at e1!"};
    private static final String[] NO_COORDINATE_FOUND_STRING =
        {"Oops! Looks like you forgot to specify a coordinate!"
        , "Format: moves [column][row]"
        , "Example: moves a2"};
    private static final String AVAILABLE_COORDINATES_INPUT = "e2";
    private static final String NO_AVAILABLE_INPUT = "e1";
    private static final String NO_PIECE_INPUT = "e4";

    public Game loadGame() {
        ChessBoard board = new ChessBoard(Color.WHITE);
        Human human = new Human(Color.WHITE, board);
        CPU cpu = new CPU(Color.WHITE, board);
        Storage storage = new Storage(FILE_PATH_STRING);
        TextUI ui = new TextUI();
        Game game = new Game(Color.WHITE, Color.WHITE, board, storage, ui, 1, human, cpu);
        return game;
    }

    @Test
    public void testExecute_inputValid() throws ChessMasterException {
        Game game = loadGame();

        ShowMovesCommand commandHasPiece = new ShowMovesCommand(AVAILABLE_COORDINATES_INPUT);
        ShowMovesCommand commandNoPiece = new ShowMovesCommand(NO_PIECE_INPUT);
        ShowMovesCommand commandNoMoves = new ShowMovesCommand(NO_AVAILABLE_INPUT);

        String[] hasPiece = commandHasPiece.execute(game).getMessageStrings();
        assertArrayEquals(hasPiece, AVAILABLE_COORDINATES_STRING);

        String[] noMoves = commandNoMoves.execute(game).getMessageStrings();
        assertArrayEquals(noMoves, NO_AVAILABLE_STRING);

        assertThrows(NullPieceException.class,
                () -> commandNoPiece.execute(game).getMessageStrings());
    }

    @Test
    public void testExecute_inputEmpty() throws ChessMasterException {
        Game game = loadGame();

        ShowMovesCommand commandNoInput = new ShowMovesCommand("");

        String[] noInput = commandNoInput.execute(game).getMessageStrings();
        assertArrayEquals(noInput, NO_COORDINATE_FOUND_STRING);
    }
}
