//@@author TongZhengHong
package chessmaster.game;

import chessmaster.commands.ExitCommand;
import chessmaster.commands.Command;
import chessmaster.commands.CommandResult;
import chessmaster.commands.HelpCommand;
import chessmaster.commands.MoveCommand;
import chessmaster.commands.RestartCommand;
import chessmaster.exceptions.ChessMasterException;
import chessmaster.game.move.Move;
import chessmaster.parser.Parser;
import chessmaster.storage.Storage;
import chessmaster.ui.TextUI;
import chessmaster.user.CPU;
import chessmaster.user.Human;
import chessmaster.user.Player;

public class Game {

    private static final String[] START_HELP_STRINGS = new String[HelpCommand.HELP_STRINGS.length + 1];

    private CPU cpu;
    private Human human;
    private Player currentPlayer;
    private int numMoves;

    private TextUI ui;
    private ChessBoard board;
    private Storage storage;
    private int difficulty;

    private Command command;
    private boolean hasEnded;
    private boolean exit = false;

    public Game(Color playerColour, Color currentTurnColor, ChessBoard board, 
        Storage storage, TextUI ui, int difficulty, Human human, CPU cpu) {

        this.ui = ui;
        this.board = board;
        this.storage = storage;
        this.difficulty = difficulty;

        this.human = human;
        Color cpuColor = playerColour.getOppositeColour();
        this.cpu = cpu;

        this.numMoves = human.getMovesLength() + cpu.getMovesLength();

        // Choose which player goes first
        currentPlayer = currentTurnColor == playerColour ? human : cpu;

        // Make the START_HELP_STRINGS more robust with just one source-of-truth in HelpCommand.HELP_STRINGS
        START_HELP_STRINGS[0] = "Thank you for choosing ChessMaster!";
        System.arraycopy(HelpCommand.HELP_STRINGS, 0, START_HELP_STRINGS, 1, HelpCommand.HELP_STRINGS.length);

        assert playerColour != Color.EMPTY : "Human player color should not be EMPTY!";
        assert cpuColor != Color.EMPTY : "CPU player color should not be EMPTY!";
        assert currentPlayer != null : "A player should always exist in a game!";
        assert (1 <= difficulty) && (difficulty <= 3) : "Difficulty should be between 1 and 3!";
    }


    /**
     * Manages the main gameplay of ChessMaster
     * This code segment orchestrates the primary gameplay loop of Chess Master, which encompasses player turns,
     * move handling, and the management of the game state.
     * It starts by displaying the initial game setup, including the chessboard, and then enters a loop where players
     * take turns making moves.
     * If a player enters a valid move, the chessboard is updated, and the game progresses.
     * If an exception is encountered during this process, an error message is displayed to the user.
     * The loop continues until the game ends or specific commands are issued to abort, restart, or exit the game.
     *
     * @return true if the game has ended, either by checkmate, stalemate,
     *     or if users wants to reset the game. Returns false if the game is aborted.
     */
    public boolean run() {
        ui.printText(START_HELP_STRINGS);
        ui.printChessBoard(board.getBoard());

        while (!hasEnded && !ExitCommand.isExit(command) && !RestartCommand.isRestart(command)) {
            try {
                assert currentPlayer.isCPU() || currentPlayer.isHuman() : 
                    "Player should only either be human or CPU!";

                if (currentPlayer.isHuman()) {
                    command = getAndExecuteUserCommand();
                    if (!command.isMoveCommand()) {
                        continue; // Get next command
                    }
                    Move playedMove = handleHumanMove();
                    ui.printChessBoardWithMove(board.getBoard(), playedMove);
                    
                } else if (currentPlayer.isCPU()) {
                    Move playedMove = handleCPUMove();
                    ui.printChessBoardWithMove(board.getBoard(), playedMove);
                } 

                currentPlayer = togglePlayerTurn();
                storage.saveBoard(board, currentPlayer.getColour(), human, cpu);
                hasEnded = checkEndState(); // Resets board if end

            } catch (ChessMasterException e) {
                ui.printErrorMessage(e);
            }
        }
        return hasEnded || RestartCommand.isRestart(command);
    }

    /**
     * Represents main part of gameplay loop for the human user. Responsible for
     * taking in the user's input and discerning the user's intention based on the input.
     * Also executes and handles all commands except `MoveCommand`.
     *
     * @return Command corresponding to the user's input
     * @throws ChessMasterException
     */
    private Command getAndExecuteUserCommand() throws ChessMasterException {
        String userInputString = ui.getUserInput(true);
        this.command = Parser.parseCommand(userInputString);

        CommandResult result = command.execute(this);
        ui.printCommandResult(result);
        return this.command;
    }

    /**
     * Handles the user input in the case that a move was made by the player.
     * Extracts the move from the current command, and reflects the move on the chessboard.
     * When the move is done, it also handles any possible promotions which can be made.
     * Returns the move which has been made to be printed in the run()  function.
     *
     * @return Move which has been executed
     * @throws ChessMasterException
     */
    private Move handleHumanMove() throws ChessMasterException {
        Move humanMove = ((MoveCommand) this.command).getMove();
        board.executeMoveWithCheck(humanMove);
        human.addMove(humanMove);
        numMoves++;

        // Handle human promotion
        if (!board.isEndGame()) {
            if (board.canPromote(humanMove)) {
                human.handlePromote(board, ui, humanMove);
            }
        }

        return humanMove;
    }

    /**
     * Obtains and executes the CPU's move in response to the current board state.
     * Also prints a message informing the player of the CPU thinking as it may take some time
     * to compute the most optimal move in higher difficulty levels.
     *
     * @return The move that the CPU made
     * @throws ChessMasterException
     */
    private Move handleCPUMove() throws ChessMasterException {
        ui.printCPUThinkingMessage();

        Move cpuMove = cpu.getBestMove(board, difficulty);
        ui.printCPUMove(cpuMove);
        board.executeMoveWithCheck(cpuMove);

        cpu.addMove(cpuMove);
        numMoves++;

        return cpuMove;
    }

    /**
     * Checks whether the current state of the board qualifies as the game having ended.
     * If the game has ended, prints a message signalling the end of the game, and resets the saved board.
     *
     * @return Whether the game has ended
     * @throws ChessMasterException
     */
    private boolean checkEndState() throws ChessMasterException {
        boolean end = board.isEndGame();
        if (end) {
            Color winningColor = board.getWinningColor();
            if (winningColor == Color.DRAW) {
                ui.printDrawMessage();
            } else {
                Player winnerPlayer = human.getColour() == winningColor ? human : cpu;
                ui.printEndMessage(winnerPlayer);
            }
            storage.resetBoard();
        }
        return end;
    }

    private Player togglePlayerTurn() {
        return currentPlayer.isHuman() ? cpu : human;
    }

    public ChessBoard getBoard() {
        return this.board;
    }

    public TextUI getUI() {
        return this.ui;
    }

    public CPU getCPU() {
        return this.cpu;
    }

    public Human getHuman() {
        return this.human;
    }

    public int getNumMoves() {
        return this.numMoves;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }
}
