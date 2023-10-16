package chessmaster.game;

import chessmaster.exceptions.InvalidMoveException;
import chessmaster.parser.Parser;
import chessmaster.parser.MoveValidator;
import chessmaster.pieces.ChessPiece;
import chessmaster.ui.TextUI;

public class ChessBoard {

    public static final int SIZE = 8;

    private static final String[][] STARTING_CHESSBOARD_STRING = {
            { "r", "n", "b", "q", "k", "b", "n", "r" },
            { "p", "p", "p", "p", "p", "p", "p", "p" },
            { "", "", "", "", "", "", "", "" },
            { "", "", "", "", "", "", "", "" },
            { "", "", "", "", "", "", "", "" },
            { "", "", "", "", "", "", "", "" },
            { "P", "P", "P", "P", "P", "P", "P", "P" },
            { "R", "N", "B", "Q", "K", "B", "N", "R" },
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

    public ChessPiece getPieceAtCoor (Coordinate coor) {
        ChessTile tile = board[coor.getX()][coor.getY()];

        return tile.getChessPiece();
    }
}
