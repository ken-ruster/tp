package chessmaster.pieces;

import chessmaster.game.ChessBoard;
import chessmaster.game.Color;
import chessmaster.game.Coordinate;

import java.util.ArrayList;

public abstract class ChessPiece {

    public static final int[] CASTLE_LEFT = {-2, 0};
    public static final int[] CASTLE_RIGHT = {2, 0};
    
    protected static final int[] UP_UP_LEFT = {1, -2}; 
    protected static final int[] UP_UP_RIGHT = {-1, -2}; 
    protected static final int[] DOWN_DOWN_LEFT = {1, 2}; 
    protected static final int[] DOWN_DOWN_RIGHT = {-1, 2}; 
    protected static final int[] LEFT_UP_LEFT = {2, -1}; 
    protected static final int[] LEFT_DOWN_LEFT = {2, 1}; 
    protected static final int[] RIGHT_UP_RIGHT = {-2, -1}; 
    protected static final int[] RIGHT_DOWN_RIGHT = {-2, 1}; 
    protected static final int[] UP_UP = {0, -2}; 
    protected static final int[] DOWN_DOWN = {0, 2};

    protected static final int[] UP = {0, -1}; 
    protected static final int[] DOWN = {0, 1}; 
    protected static final int[] LEFT = {1, 0};
    protected static final int[] RIGHT = {-1, 0};

    protected static final int[] UP_LEFT = {1, -1}; 
    protected static final int[] UP_RIGHT = {-1, -1}; 
    protected static final int[] DOWN_LEFT = {1, 1}; 
    protected static final int[] DOWN_RIGHT = {-1, 1}; 

    protected Color color;
    protected Coordinate position;
    protected Coordinate[][] availableCoordinates;
    protected boolean hasMoved = false;
    protected boolean isCaptured = false;
    protected int points = 0;


    public ChessPiece(int row, int col, Color color) {
        this.position = new Coordinate(col, row);
        this.color = color;
    }

    /**
     * Returns available coordinates in multiple directions from the current position. 
     * The directions are dependent on the chess piece type. Each inner array stores the coordinates that is
     * in the direction the current chess piece can move to.
     *
     * @return A 2D array of Coordinate arrays representing available coordinates in different directions.
     */
    public abstract Coordinate[][] getAvailableCoordinates(ChessBoard board);

    /**
     * Get a flattened array of valid coordinates for the chess piece's moves based on its available coordinates 
     * and the current state of the ChessBoard.
     *
     * @param board The ChessBoard representing the current game state.
     * @return A 1D array of valid coordinates for the piece's legal moves.
     */
    public Coordinate[] getFlattenedCoordinates(ChessBoard board) {
        Coordinate[][] availableCoordinates = getAvailableCoordinates(board);
        ArrayList<Coordinate> flattenedCoordinates = new ArrayList<>();

        for (Coordinate[] direction : availableCoordinates) {
            for (Coordinate possibleCoord : direction) {
                if (this.isMoveValid(possibleCoord, board)) {
                    flattenedCoordinates.add(possibleCoord);
                }
            }
        }

        return flattenedCoordinates.toArray(new Coordinate[0]);
    }

    /**
     * Returns the validity of the move to the destination coordinate.
     * @param destination
     * @param board
     * @return
     */
    public boolean isMoveValid(Coordinate destination, ChessBoard board) {
        Coordinate[][] availableCoordinates = getAvailableCoordinates(board);
        for (Coordinate[] direction : availableCoordinates) {
            for (Coordinate possibleCoord : direction) {
                if (possibleCoord.equals(destination)) {
                    ChessPiece destPiece = board.getPieceAtCoor(destination);
                    if (destPiece.isEmptyPiece()) {
                        return true;
                    } else if (destPiece.isOpponent(this)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void displayAvailableCoordinates(ChessBoard board) {

        System.out.println("Available coordinates for " + this.getClass().getSimpleName() + " at " + position + ":\n");
        Coordinate[][] availableCoordinates = getAvailableCoordinates(board);

        for (Coordinate[] direction : availableCoordinates) {
            for (Coordinate possibleCoord : direction) {
                if (this.isMoveValid(possibleCoord, board)){
                    System.out.print(possibleCoord + " ");
                }
            }
        }
        System.out.println();
    }

    public Coordinate getPosition() {
        return this.position;
    }

    public void updatePosition(Coordinate newCoordinate) {
        this.position = newCoordinate;
    }

    public void setHasMoved() {
        this.hasMoved = true;
    }

    public boolean getHasMoved() {
        return this.hasMoved;
    }

    @Override
    public String toString() {
        return "ChessPiece [color=" + color + ", position=" + position + "]";
    }

    public Color getColor() {
        return color;
    }

    public boolean getIsCaptured() {
        return this.isCaptured;
    }

    public void setIsCaptured() {
        this.isCaptured = true;
    }

    public int getPoints() {
        return this.points;
    }

    /**
     * Checks if the ChessPiece object has the same color as a given color.
     *
     * @param color The color to compare with the ChessPiece's color.
     * @return true if the ChessPiece has the same color as the provided color; false otherwise.
     */
    public boolean isSameColorAs(Color color) {
        if (isEmptyPiece()) {
            return false;
        }
        return this.color == color;
    }

    /**
     * Checks if the ChessPiece object is WHTIE.
     *
     * @return true if the ChessPiece is white; false otherwise.
     */
    public boolean isWhite() {
        if (isEmptyPiece()) {
            return false;
        }
        return this.color == Color.WHITE;
    }

    /**
     * Checks if the ChessPiece object is BLACK.
     *
     * @return true if the ChessPiece is white; false otherwise.
     */
    public boolean isBlack() {
        if (isEmptyPiece()) {
            return false;
        }
        return this.color == Color.BLACK;
    }
    
    /**
     * Checks if the provided ChessPiece object is friendly (has the same color) as the current ChessPiece.
     *
     * @param chessPiece The ChessPiece to compare with.
     * @return true if the provided ChessPiece is friendly; false otherwise.
     */
    public boolean isFriendly(ChessPiece chessPiece) {
        if (isEmptyPiece()) {
            return false;
        }
        return chessPiece.color == this.color;
    }

    /**
     * Checks if the provided ChessPiece is an opponent (has a different color) compared to the current ChessPiece.
     *
     * @param chessPiece The ChessPiece to compare with.
     * @return true if the provided ChessPiece is an opponent; false otherwise.
     */
    public boolean isOpponent(ChessPiece chessPiece) {
        if (isEmptyPiece()) {
            return false;
        }
        return chessPiece.color != this.color;
    }

    public boolean isEmptyPiece() {
        return this instanceof EmptyPiece;
    }

    public boolean isPromotionPiece() {
        return this instanceof Queen || this instanceof Rook 
            || this instanceof Bishop || this instanceof Knight;
    }



    protected void setPoints(int points) {
        this.points = points;
    }
}
