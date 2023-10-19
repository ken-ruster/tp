package chessmaster;

import chessmaster.exceptions.ChessMasterException;
import chessmaster.game.ChessBoard;
import chessmaster.game.ChessTile;
import chessmaster.game.Coordinate;
import chessmaster.game.Move;
import chessmaster.parser.Parser;
import chessmaster.pieces.ChessPiece;
import chessmaster.pieces.Pawn;
import chessmaster.ui.TextUI;

/**
 * Main entry-point for ChessMaster application.
 */
public class ChessMaster {
    public static void main(String[] args) {

        // String logo = "░█████╗░██╗░░██╗███████╗░██████╗░██████╗
        // ███╗░░░███╗░█████╗░░██████╗████████╗███████╗██████╗░"
        // + System.lineSeparator() +
        // "██╔══██╗██║░░██║██╔════╝██╔════╝██╔════╝
        // ████╗░████║██╔══██╗██╔════╝╚══██╔══╝██╔════╝██╔══██╗"
        // + System.lineSeparator() +
        // "██║░░╚═╝███████║█████╗░░╚█████╗░╚█████╗░
        // ██╔████╔██║███████║╚█████╗░░░░██║░░░█████╗░░██████╔╝"
        // + System.lineSeparator() +
        // "██║░░██╗██╔══██║██╔══╝░░░╚═══██╗░╚═══██╗
        // ██║╚██╔╝██║██╔══██║░╚═══██╗░░░██║░░░██╔══╝░░██╔══██╗"
        // + System.lineSeparator() +
        // "╚█████╔╝██║░░██║███████╗██████╔╝██████╔╝
        // ██║░╚═╝░██║██║░░██║██████╔╝░░░██║░░░███████╗██║░░██║"
        // + System.lineSeparator() +
        // "░╚════╝░╚═╝░░╚═╝╚══════╝╚═════╝░╚═════╝░
        // ╚═╝░░░░░╚═╝╚═╝░░╚═╝╚═════╝░░░░╚═╝░░░╚══════╝╚═╝░░╚═╝"
        // + System.lineSeparator();

        // System.out.println(logo);

        boolean end = false;

        TextUI ui = new TextUI();
        ChessBoard board = new ChessBoard();

        while (!end) {
            board.showChessBoard(ui);
            String userInputString = ui.getUserInput();
            if (Parser.isUserInputAbort(userInputString)) {
                break; // End the game if user aborts
            }

            try {
                Move move = Parser.parseMove(userInputString, board);
                board.executeMove(move);
                if(board.canPromote(move)){
                    promote(board, board.getPieceAtCoor(move.getTo()), ui);
                }

                // TODO: Opponent player (AI) pick random move
                // Todo: board.executeMove(aiMove)
                // Todo: Check game state

            } catch (ChessMasterException e) {
                ui.printErorMessage(e);
            }
        }
    }

    /**
     * Prompts the user to enter a type of piece to promote a pawn to. If the promotion is not successful,
     * the user is prompted again. If successful, the pawn is replaced with the new piece.
     *
     * @param board Chessboard that the game is being played on.
     * @param promoteFrom The piece being promoted.
     * @param ui User interface currently being displayed.
     */
    private static void promote(ChessBoard board, ChessPiece promoteFrom, TextUI ui) {
        board.showChessBoard(ui);
        Coordinate coord = promoteFrom.getPosition();
        boolean promoteFailure = true;

        do {
            ui.printPromotePrompt(coord);
            String in = ui.getUserInput();
           ChessPiece promoteTo = Parser.parsePromote(promoteFrom, in);
            ChessTile promoted = new ChessTile(promoteTo);
            board.setTile(coord.getY(), coord.getX(), promoted);

            promoteFailure = promoteTo.toString().equalsIgnoreCase(Pawn.PAWN_WHITE);
            if(promoteFailure){
                ui.printPromoteInvalidMessage();
            }
        } while(promoteFailure);
    }

}
