//@@author TongZhengHong
package chessmaster;

import chessmaster.exceptions.ChessMasterException;
import chessmaster.game.ChessBoard;
import chessmaster.ui.TextUI;
import chessmaster.storage.Storage;
import chessmaster.game.ChessTile;
import chessmaster.game.Color;
import chessmaster.game.Game;
import chessmaster.user.CPU;
import chessmaster.user.Human;


/**
 * Main entry-point for ChessMaster application.
 */
public class ChessMaster {

    private static final String FILE_PATH_STRING = "data/ChessMaster.txt";

    private TextUI ui;
    private ChessBoard board;
    private Storage storage;
    private int difficulty;
    private boolean exit = false;
    private Color playerColor;
    private Color currentTurnColor = Color.WHITE;
    private Human human;
    private CPU cpu;

    private ChessMaster() {
        ui = new TextUI();
        storage = new Storage(FILE_PATH_STRING);
        ui.printWelcomeMessage();

        try {
            playerColor = storage.loadPlayerColor();
            difficulty = storage.loadDifficulty();
            currentTurnColor = storage.loadCurrentColor();
            //@@author ken_ruster
            ChessTile[][] existingBoardState = storage.loadBoard();
            ChessBoard existingBoard = new ChessBoard(playerColor, existingBoardState);
            board = new ChessBoard(playerColor);
            human = new Human(playerColor, board);
            cpu = new CPU(playerColor.getOppositeColour(), board);
            storage.executeSavedMoves(playerColor, existingBoard, board, human, cpu);
            board.setDifficulty(difficulty);

            if (shouldStartNewGame() && !exit) {
                loadNewGame();
            }
        } catch (ChessMasterException e) {
            ui.printLoadBoardError();
            loadNewGame();
        }
    }

    private boolean shouldStartNewGame() {
        ui.promptContinuePrevGame(false);
        String input = ui.getUserInput(false);

        while (!input.equals("y") && !input.equals("n")) {
            if (input.equalsIgnoreCase("exit")) {
                exit = true;
                break;
            }
            ui.promptContinuePrevGame(true);
            input = ui.getUserInput(false);
        }
        
        if (input.equals("y")) {
            ui.printContinuePrevGame(playerColor.name(), difficulty);
            return false;
        } else {
            return true;
        }
    }

    //@@author TriciaBK
    private boolean shouldRestartGame() {
        ui.promptNewGame(false);
        String input = ui.getUserInput(false);
        while (!input.equals("y") && !input.equals("n")) {
            if (input.equalsIgnoreCase("exit")) {
                exit = true;
                break;
            }
            ui.promptNewGame(true);
            input = ui.getUserInput(false);
        }
        if (input.equals("y")) {
            ui.printRestartingGameMessage();
            return true;
        } else {
            return false;
        }
    }

    private void loadNewGame() {
        ui.promptStartingColor(false);
        String input = ui.getUserInput(false);

        while (!input.equals("b") && !input.equals("w")) {
            if (input.equalsIgnoreCase("exit")) {
                exit = true;
                return;
            }
            ui.promptStartingColor(true);
            input = ui.getUserInput(false);
        }

        playerColor = input.equals("b") ? Color.BLACK : Color.WHITE;
        Color cpuColor = playerColor.getOppositeColour();
        board = new ChessBoard(playerColor);
        ui.printStartNewGame(playerColor.name());

        human = new Human(playerColor, board);
        cpu = new CPU(cpuColor, board);

        //@@author onx001
        ui.promptDifficulty(false);
        input = ui.getUserInput(false);
        while (!input.equals("1") && !input.equals("2") && !input.equals("3")) {
            if (input.equalsIgnoreCase("exit")) {
                exit = true;
                return;
            }
            ui.promptDifficulty(true);
            input = ui.getUserInput(false);
        }
        difficulty = Integer.parseInt(input);
        board.setDifficulty(difficulty);
        currentTurnColor = Color.WHITE;

        try {
            storage.saveBoard(board, currentTurnColor, human, cpu);
        } catch (ChessMasterException e) {
            ui.printText(e.getMessage());
        }
    }

    private void run() {
        boolean shouldRestart = true;
        while (shouldRestart && !exit) {
            Game game = new Game(playerColor, currentTurnColor, board, storage, ui, difficulty, human, cpu);
            boolean restartMidGame = game.run();
            if (!restartMidGame) {
                shouldRestart = false;
                continue;
            }
            shouldRestart = shouldRestartGame();
            if (shouldRestart) {
                loadNewGame();
            }
        }
    }

    public static void main(String[] args) {
        new ChessMaster().run();
    }
}
