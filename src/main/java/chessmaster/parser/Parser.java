package chessmaster.parser;

import chessmaster.exceptions.NullPieceException;
import chessmaster.exceptions.ParseCoordinateException;
import chessmaster.game.ChessBoard;
import chessmaster.game.Coordinate;
import chessmaster.pieces.King;
import chessmaster.pieces.Queen;
import chessmaster.pieces.Bishop;
import chessmaster.pieces.Rook;
import chessmaster.pieces.Knight;
import chessmaster.pieces.Pawn;
import chessmaster.pieces.ChessPiece;
import chessmaster.game.Move;

public class Parser {

    private static final String ABORT_COMMAND = "abort";

    public static boolean isUserInputAbort(String userInput) {
        return userInput.trim().toLowerCase().equals(ABORT_COMMAND);
    }

    /**
     * Parses a string telling which chess piece the user wants to promote his piece to,
     * and promotes the relevant piece
     *
     * @param promoteFrom Chess piece to be promoted.
     * @param promoteTo   String representing the type of piece to be promoted to.
     * @return Promoted chess piece.
     */
    public static ChessPiece parsePromote(ChessPiece promoteFrom, String promoteTo) {
        int colour = promoteFrom.getColour();
        Coordinate position = promoteFrom.getPosition();

        switch (promoteTo.toLowerCase()){
        case Bishop.BISHOP_WHITE:
            return new Bishop(position.getY(), position.getX(), colour);
        case Queen.QUEEN_WHITE:
            return new Queen(position.getY(), position.getX(), colour);
        case Knight.KNIGHT_WHITE:
            return new Knight(position.getY(), position.getX(), colour);
        case Rook.ROOK_WHITE:
            return new Rook(position.getY(), position.getX(), colour);
        default:
            return promoteFrom;
        }
    }

    /**
     * Parses a chess move from user input and creates a Move object.
     * Used to read user inputs during the chess game.
     *
     * @param in The user input string with 2 algebraic coordinate notations (e.g., "e2 e4").
     * @param board The ChessBoard where the move is taking place.
     * @return Move object containing information about the move to be made.
     * 
     * @throws ParseCoordinateException If the string entered is not in the algebraic coordinate notation.
     * @throws NullPieceException If there is no piece at the 'from' coordinate.
     */

    public static Move parseMove(String in, ChessBoard board) throws ParseCoordinateException, NullPieceException {
        String[] parseArray = in.toLowerCase().split("\\s+", 2);
        if (parseArray.length < 2) {
            throw new ParseCoordinateException();
        }

        Coordinate from = Coordinate.parseAlgebraicCoor(parseArray[0]);
        Coordinate to = Coordinate.parseAlgebraicCoor(parseArray[1]);
        ChessPiece relevantPiece = board.getPieceAtCoor(from);

        return new Move(from, to, relevantPiece);
    }

    /**
     * Parses an input string and creates a ChessPiece object at the specified row
     * and column.
     * Used for loading ChessPiece(s) from storage file or loading starting
     * ChessBoard.
     * Returns null for recognised input string to signify that piece is empty (for
     * ChessTile)
     *
     * @param pieceString The string representation of the chess piece, e.g., "bB"
     *                    for black bishop.
     * @param row         The row where the piece is located.
     * @param col         The column where the piece is located.
     * @return A ChessPiece object representing the parsed chess piece, or null if
     *         the pieceString is not recognized.
     */
    public static ChessPiece parseChessPiece(String pieceString, int row, int col) {
        switch (pieceString) {
        case Bishop.BISHOP_BLACK:
            return new Bishop(row, col, ChessPiece.BLACK);
        case Bishop.BISHOP_WHITE:
            return new Bishop(row, col, ChessPiece.WHITE);
        case King.KING_BLACK:
            return new King(row, col, ChessPiece.BLACK);
        case King.KING_WHITE:
            return new King(row, col, ChessPiece.WHITE);
        case Queen.QUEEN_BLACK:
            return new Queen(row, col, ChessPiece.BLACK);
        case Queen.QUEEN_WHITE:
            return new Queen(row, col, ChessPiece.WHITE);
        case Knight.KNIGHT_BLACK:
            return new Knight(row, col, ChessPiece.BLACK);
        case Knight.KNIGHT_WHITE:
            return new Knight(row, col, ChessPiece.WHITE);
        case Pawn.PAWN_BLACK:
            return new Pawn(row, col, ChessPiece.BLACK);
        case Pawn.PAWN_WHITE:
            return new Pawn(row, col, ChessPiece.WHITE);
        case Rook.ROOK_BLACK:
            return new Rook(row, col, ChessPiece.BLACK);
        case Rook.ROOK_WHITE:
            return new Rook(row, col, ChessPiece.WHITE);
        default:
            return null;
        }
    }
}
