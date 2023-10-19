package chessmaster.game;

import chessmaster.exceptions.InvalidMoveException;
import chessmaster.exceptions.NullPieceException;
import chessmaster.parser.Parser;
import chessmaster.pieces.ChessPiece;
import chessmaster.pieces.Pawn;
import chessmaster.ui.TextUI;


public class ChessBoard {

    public static final int SIZE = 8;

    private static final String[][] STARTING_CHESSBOARD_STRING = {
            {"r", "n", "b", "q", "k", "b", "n", "r"},
            {"p", "p", "p", "p", "p", "p", "p", "p"},
            {"", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", ""},
            {"P", "P", "P", "P", "P", "P", "P", "P"},
            {"R", "N", "B", "Q", "K", "B", "N", "R"},
    };

    private ChessTile[][] board = new ChessTile[SIZE][SIZE];

    public ChessBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                String chessPieceString = STARTING_CHESSBOARD_STRING[row][col];
                ChessPiece initialPiece = Parser.parseChessPiece(chessPieceString, row, col);
                board[row][col] = new ChessTile(initialPiece);
            }
        }
    }

    public void displayAvailableMoves(){
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length ; j++) {
                ChessPiece piece = board[i][j].getChessPiece();
                if (piece != null){
                    piece.displayAvailableCoordinates(this.board);
                }
            }

            
        }
    }

    public void showChessBoard(TextUI ui) {
        ui.printChessBoardHeader();
        ui.printChessBoardDivider();
        for (int i = 0; i < board.length; i++) {
            ChessTile[] row = board[i];
            StringBuilder rowString = new StringBuilder();

            for (ChessTile tile : row) {
                rowString.append(tile.toString());
            }

            int rowNum = (i - 8) * -1;
            ui.printChessBoardRow(rowNum, rowString.toString());
        }
    }

    private ChessTile getTileAtCoor(Coordinate coor) {
        return board[coor.getY()][coor.getX()];
    }

    public ChessPiece getPieceAtCoor(Coordinate coor) throws NullPieceException {
        ChessTile tile = getTileAtCoor(coor);
        if (tile.isEmpty()) {
            throw new NullPieceException();
        }
        return tile.getChessPiece();
    }

    public void setTile(int row, int col, ChessTile tile) {
        board[row][col] = tile;
    }

    /**
     * Executes a chess move on the chessboard.
     *
     * @param move The Move object representing the move to be executed.
     * @throws InvalidMoveException If the move is not valid according to the game rules.
     */
    public void executeMove(Move move) throws InvalidMoveException {
        Coordinate startCoor = move.getFrom();
        Coordinate destCoor = move.getTo();
        ChessPiece chessPiece = move.getPiece();

        Coordinate[][] possibleCoordinates = chessPiece.getAvailableCoordinates(board);
        if (!move.isValid(possibleCoordinates)) {
            throw new InvalidMoveException();
        }

        chessPiece.updatePosition(destCoor);
        getTileAtCoor(startCoor).setTileEmpty();
        getTileAtCoor(destCoor).updateTileChessPiece(chessPiece);
    }

    public boolean canPromote(Move move) {
        ChessPiece piece = move.getPiece();
        int colour = piece.getColour();
        Coordinate endCoord = move.getTo();
        boolean isPawn = piece.toString().equalsIgnoreCase(Pawn.PAWN_WHITE);

        if(!isPawn){
            return false;
        }

        if(colour == ChessPiece.WHITE){
            return endCoord.getY() == 7;
        }

        if(colour == ChessPiece.BLACK){
            return endCoord.getY() == 0;
        }

        return false;
    }

}
