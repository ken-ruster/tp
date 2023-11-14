package chessmaster.game;

import java.util.ArrayList;

import chessmaster.exceptions.ChessMasterException;
import chessmaster.exceptions.InvalidMoveException;
import chessmaster.game.move.PromoteMove;
import chessmaster.game.move.CastleMove;
import chessmaster.game.move.CastleSide;
import chessmaster.game.move.Move;
import chessmaster.game.move.MoveFactory;
import chessmaster.game.move.EnPassantMove;
import chessmaster.parser.Parser;
import chessmaster.pieces.ChessPiece;
import chessmaster.pieces.EmptyPiece;
import chessmaster.pieces.King;
import chessmaster.pieces.Pawn;
import chessmaster.user.CPU;
import chessmaster.user.Human;


public class ChessBoard {

    public static final int SIZE = 8;
    public static final int TOP_ROW_INDEX = 0;
    public static final int BOTTOM_ROW_INDEX = 7;
    public static final int MAX_PIECES = 16;
    public static final String PROMOTE_MOVE_STRING = "p";
    public static final String INVALID_SAVE_STRING =
            "Invalid move found in save file! Please start a new game or correct the invalid move!";
    public static final String INVALID_PROMOTE_STRING =
            "Invalid promotion found in save file! Please start a new game or correct the invalid move!";
    private static final String[][] STARTING_CHESSBOARD_BLACK = { 
        { "r", "n", "b", "q", "k", "b", "n", "r" }, 
        { "p", "p", "p", "p", "p", "p", "p", "p" }, 
        { ".", ".", ".", ".", ".", ".", ".", "." }, 
        { ".", ".", ".", ".", ".", ".", ".", "." }, 
        { ".", ".", ".", ".", ".", ".", ".", "." }, 
        { ".", ".", ".", ".", ".", ".", ".", "." }, 
        { "P", "P", "P", "P", "P", "P", "P", "P" }, 
        { "R", "N", "B", "Q", "K", "B", "N", "R" }, 
    };

    private static final String[][] STARTING_CHESSBOARD_WHITE = { 
        { "R", "N", "B", "Q", "K", "B", "N", "R" }, 
        { "P", "P", "P", "P", "P", "P", "P", "P" },
        { ".", ".", ".", ".", ".", ".", ".", "." }, 
        { ".", ".", ".", ".", ".", ".", ".", "." }, 
        { ".", ".", ".", ".", ".", ".", ".", "." }, 
        { ".", ".", ".", ".", ".", ".", ".", "." }, 
        { "p", "p", "p", "p", "p", "p", "p", "p" },
        { "r", "n", "b", "q", "k", "b", "n", "r" }, 
    };

    private Color playerColor;

    private int difficulty = 4;

    private final ChessTile[][] board = new ChessTile[SIZE][SIZE];

    public ChessBoard(Color playerColor) {
        this.playerColor = playerColor;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                String chessPieceString = playerColor.isBlack()
                    ? STARTING_CHESSBOARD_BLACK[row][col]
                    : STARTING_CHESSBOARD_WHITE[row][col];
                ChessPiece initialPiece = Parser.parseChessPiece(chessPieceString, row, col);
                board[row][col] = new ChessTile(initialPiece);
                assert (board[row][col] != null);
            }
        }
        this.playerColor = playerColor;
    }

    public ChessBoard(Color playerColor, ChessTile[][] boardTiles) {
        this.playerColor = playerColor;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                board[row][col] = boardTiles[row][col];
            }
        }
    }

    /**
     * Gets a copy of the current chessboard as a 2D array of ChessTile objects.
     *
     * This method creates a deep copy of the chessboard, allowing for the independent
     * examination of the board's state without modifying the original chessboard.
     *
     * @return A 2D array copy of ChessTile objects representing the current state of the chessboard.
     */
    public ChessTile[][] getBoard() {
        ChessTile[][] copy = new ChessTile[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            copy[i] = board[i].clone();
        }
        return copy;
    }

    public Color getPlayerColor() {
        return this.playerColor;
    }

    //@@author onx001
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getDifficulty() {
        return this.difficulty;
    }

    //@@author onx001
    public boolean hasEnPassant() {
        //Checks all chess pieces for en passant
        for (int row = 0; row < ChessBoard.SIZE; row++) {
            for (int col = 0; col < ChessBoard.SIZE; col++) {
                Coordinate coor = new Coordinate(col, row);
                ChessPiece piece = getPieceAtCoor(coor);
                if (piece.isEnPassant()) {
                    return true;
                }
            }
        }
        return false;
    }

    public ChessPiece getEnPassantPiece() {
        for (int row = 0; row < ChessBoard.SIZE; row++) {
            for (int col = 0; col < ChessBoard.SIZE; col++) {
                Coordinate coor = new Coordinate(col, row);
                ChessPiece piece = getPieceAtCoor(coor);
                if (piece.isEnPassant()) {
                    return piece;
                }
            }
        }
        return null;
    }

    public Coordinate getEnPassantCoor() {
        //Checks all chess pieces for en passant
        for (int row = 0; row < ChessBoard.SIZE; row++) {
            for (int col = 0; col < ChessBoard.SIZE; col++) {
                Coordinate coor = new Coordinate(col, row);
                ChessPiece piece = getPieceAtCoor(coor);
                if (piece.isEnPassant()) {
                    if (piece.isSameColorAs(playerColor)) {
                        return coor.addOffsetToCoordinate(0, 1);
                    } else {
                        return coor.addOffsetToCoordinate(0, -1);
                    }
                }
            }
        }
        return null;
    }

    public boolean isCheckmated(Color color) {
        Move[] moves = getLegalMoves(color);
        ChessPiece[] playerPieces = getAllPieces(color);
        ChessPiece[] opponentPieces = getAllPieces(color.getOppositeColour());
        if (playerPieces.length == 1 && opponentPieces.length == 1) {
            return true;
        }
        return moves.length == 0;
    }

    public ChessPiece[] getAllPieces(Color color) {
        ArrayList<ChessPiece> pieces = new ArrayList<ChessPiece>();
        for (int row = 0; row < ChessBoard.SIZE; row++) {
            for (int col = 0; col < ChessBoard.SIZE; col++) {
                Coordinate coor = new Coordinate(col, row);
                ChessPiece piece = getPieceAtCoor(coor);
                if (piece.isSameColorAs(color)) {
                    pieces.add(piece);
                }
            }
        }
        return pieces.toArray(new ChessPiece[0]);
    }

    public boolean isKingAlive(Color color) {
        ChessPiece[] pieces = getAllPieces(color);
        for (ChessPiece piece : pieces) {
            if (piece instanceof King) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the player of the specified color is in check.
     *
     * This method determines whether the player with the specified color is in check, 
     * meaning their king is under threat. It checks if any moves by the opposing player's pieces 
     * can reach the player's king.
     *
     * @param color The color for which to check if the king is in check ('WHITE' or 'BLACK').
     * @return `true` if the player is in check; `false` if the player's king is not in immediate danger.
     */
    public boolean isChecked(Color color) {
        Move[] moves = getPseudoLegalMoves(color.getOppositeColour());
        for (Move move : moves) {
            Coordinate to = move.getTo();
            if (this.getPieceAtCoor(to) instanceof King) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieve an array of pseudo-legal moves for pieces of the specified color.
     *
     * This method calculates and provides an array of pseudo-legal moves for all pieces of the given 
     * color on the chessboard. Pseudo-legal moves are those that are valid based on the piece's movement rules, 
     * but they may not account for potential checks on the king.
     *
     * @param color The color for which pseudo-legal moves should be generated ('WHITE' or 'BLACK').
     * @return An array of Move objects, each representing a pseudo-legal move, containing the starting square, 
     *          destination square, and the ChessPiece involved.
     */

    public Move[] getPseudoLegalMoves(Color color) {
        //Declare arraylist of moves as allMoves
        ArrayList<Move> allMoves = new ArrayList<>();

        for (int row = 0; row < ChessBoard.SIZE; row++) {
            for (int col = 0; col < ChessBoard.SIZE; col++) {
                Coordinate currentCoor = new Coordinate(col, row);
                ChessPiece piece = getPieceAtCoor(currentCoor);

                if (piece.isSameColorAs(color)) {
                    Coordinate[] possibleCoordinates = piece.getPseudoLegalCoordinates(this);
                    for (Coordinate possible: possibleCoordinates) {
                        allMoves.add(MoveFactory.createMove(this, currentCoor, possible));
                    }
                }
            }
        }
        return allMoves.toArray(new Move[0]);
    }

    /**
     * Retrieve an array of legal moves for pieces of the specified color.
     *
     * This method calculates and provides an array of legal moves for all pieces of the given color on the 
     * chessboard. This is done by executing each pseudo-legal move and ensuring that it does not result in 
     * the king being checked. 
     * 
     * Legal moves are those that adhere to the piece's movement rules and do not result in the 
     * player's own king being in check.
     *
     * @param color The color for which legal moves should be generated ('WHITE' or 'BLACK').
     * @return An array of Move objects, each representing a legal move, containing the starting square, 
     *          destination square, and the ChessPiece involved.
     */
    public Move[] getLegalMoves(Color color) {
        Move[] moves = getPseudoLegalMoves(color);
        ArrayList<Move> legalMoves = new ArrayList<>();

        for (Move move : moves) {
            ChessBoard newBoard = this.clone();
            Coordinate from = move.getFrom();
            ChessPiece piece = newBoard.getPieceAtCoor(from);
            Move moveCopy = new Move(from, move.getTo(), piece);
            try {
                newBoard.executeMove(moveCopy);
            } catch (InvalidMoveException e) {
                continue;
            }
            if (!newBoard.isChecked(color)) {
                legalMoves.add(move);
            }
        }
        return legalMoves.toArray(new Move[0]);
    }

    //@@author TongZhengHong
    public void setPromotionPiece(Coordinate coord, ChessPiece promotedPiece) {
        getTileAtCoor(coord).updateTileChessPiece(promotedPiece);
    }

    /**
     * Gets the ChessTile object located at the specified coordinate on the
     * chessboard.
     *
     * @param coor The coordinate of the position to retrieve the tile for.
     * @return The ChessTile object at the specified coordinate.
     */
    public ChessTile getTileAtCoor(Coordinate coor) {
        return board[coor.getY()][coor.getX()];
    }

    /**
     * Gets the chess piece located at the specified coordinate on the chessboard.
     *
     * @param coor The coordinate of the position to check.
     * @return The ChessPiece object at the specified coordinate. If empty piece at
     *         coordinate, returns EmptyPiece object
     */
    public ChessPiece getPieceAtCoor(Coordinate coor) {
        ChessTile tile = getTileAtCoor(coor);
        return tile.getChessPiece();
    }

    /**
     * For castling moves, executeBasicMove() only moves the King. This method will move the rook correctly
     * based on whether the player has castled queen-side or king-side.
     * @param move The CastleMove to execute
     * @throws InvalidMoveException If the rook move is not valid according to game rules
     */
    private void executeCastlingRookMove(CastleMove move) throws InvalidMoveException {
        Coordinate startCoor = move.getFrom();
        CastleSide side = move.getSide();

        Move rookCastleMove;
        if (side == CastleSide.LEFT && startCoor.isOffsetWithinBoard(-4, 0)) {
            Coordinate rookStartCoor = startCoor.addOffsetToCoordinate(-4, 0);
            Coordinate rookDestCoor = startCoor.addOffsetToCoordinate(-1, 0);
            rookCastleMove = MoveFactory.createMove(this, rookStartCoor, rookDestCoor);
            move.setRookMove(rookCastleMove);

            this.executeMove(rookCastleMove);
        } else if (side == CastleSide.RIGHT && startCoor.isOffsetWithinBoard(3, 0)) {
            Coordinate rookStartCoor = startCoor.addOffsetToCoordinate(3, 0);
            Coordinate rookDestCoor = startCoor.addOffsetToCoordinate(1, 0);
            rookCastleMove = MoveFactory.createMove(this, rookStartCoor, rookDestCoor);
            move.setRookMove(rookCastleMove);

            this.executeMove(rookCastleMove);
        }
    }

    private void executeEnPassantCapture(EnPassantMove move) {
        ChessPiece enPassantPiece = move.getPieceCaptured();

        // Capture the enPassantPiece
        this.getTileAtCoor(enPassantPiece.getPosition()).setTileEmpty(enPassantPiece.getPosition());
        enPassantPiece.setIsCaptured();
    }

    /**
     * Moves a piece from the start coordinate to the destination coordinate.
     * @param move The move to execute
     */
    private void executeBasicMove(Move move) {
        Coordinate startCoor = move.getFrom();
        Coordinate destCoor = move.getTo();
        ChessPiece pieceMoved = move.getPieceMoved();

        pieceMoved.setHasMoved();
        pieceMoved.updatePosition(destCoor);

        getTileAtCoor(startCoor).setTileEmpty(startCoor);
        getTileAtCoor(destCoor).getChessPiece().setIsCaptured();
        getTileAtCoor(destCoor).updateTileChessPiece(pieceMoved);
    }


    private void clearAllEnPassants(Move move) {
        for (int row = 0; row < ChessBoard.SIZE; row++) {
            for (int col = 0; col < ChessBoard.SIZE; col++) {
                Coordinate coor = new Coordinate(col, row);
                ChessPiece piece = getPieceAtCoor(coor);
                if (piece.isEnPassant() && piece.getColor() != move.getPieceMoved().getColor()) {
                    piece.clearEnPassant();
                }
            }
        }
    }

    /**
     * Executes a chess move on the chessboard.
     *
     * @param move The Move object representing the move to be executed.
     * @throws InvalidMoveException If the move is not valid according to the game
     *                              rules.
     */
    public void executeMove(Move move) throws InvalidMoveException {
        this.executeBasicMove(move);

        if (move instanceof CastleMove) {
            this.executeCastlingRookMove((CastleMove) move);
        } else if (move instanceof EnPassantMove) {
            this.executeEnPassantCapture((EnPassantMove) move);
        } else if (move.isSkippingPawn()) {
            move.getPieceMoved().setEnPassant();
        }

        this.clearAllEnPassants(move);
    }

    public void executeMoveWithCheck(Move move) throws InvalidMoveException {
        if (move.isValidWithCheck(this)) {
            executeMove(move);
        } else {
            throw new InvalidMoveException("Move causes a check");
        }
    }

    //@@author ken-ruster
    public boolean canPromote(Move move) {
        ChessPiece piece = move.getPieceMoved();
        Coordinate endCoord = move.getTo();

        if (!piece.isPawn()) {
            return false;
        }

        if (isPieceFriendly(piece)) {
            return endCoord.getY() == TOP_ROW_INDEX;
        }

        if (isPieceOpponent(piece)) {
            return endCoord.getY() == BOTTOM_ROW_INDEX;
        }

        return false;
    }

    //@@author onx001
    public boolean isEndGame() throws ChessMasterException {
        if (!isKingAlive(playerColor) || !isKingAlive(playerColor.getOppositeColour())) {
            throw new ChessMasterException("King is dead!");
        }
        return isCheckmated(playerColor) || isCheckmated(playerColor.getOppositeColour());
    }

    public Color getWinningColor() {
        if (isCheckmated(playerColor) && isCheckmated(playerColor.getOppositeColour())) {
            return Color.DRAW;
        } else if (isCheckmated(playerColor)) {
            return playerColor.getOppositeColour();
        } else if (isCheckmated(playerColor.getOppositeColour())) {
            return playerColor;
        } else {
            return Color.EMPTY;
        }
    }
    

    public int getPoints(Color color) {
        int points = 0;
        int enemyPoints = 0;
        boolean isUpright;

        if (this.playerColor == color) {
            isUpright = true;
        } else {
            isUpright = false;
        }

        for (int row = 0; row < ChessBoard.SIZE; row++) {
            for (int col = 0; col < ChessBoard.SIZE; col++) {
                Coordinate coor = new Coordinate(col, row);
                ChessPiece piece = this.getPieceAtCoor(coor);

                if (piece.isSameColorAs(color)) {
                    points += piece.getPoints(isUpright);
                } else {
                    enemyPoints += piece.getPoints(isUpright);
                }
            }
        }

        return points - enemyPoints;
    }


    public ChessBoard clone() {
        String boardString = this.toString();
        return toBoard(boardString);
    }


    /**
     * Converts a String representing a board into a new ChessBoard.
     *
     * @param board String representing the board
     * @return ChessBoard generated from input string
     */
    public ChessBoard toBoard(String board) {
        ChessTile[][] boardTiles = new ChessTile[SIZE][SIZE];
        int row = 0;
        int col = 0;
        for (int i = 0; i < board.length(); i++) {
            String pieceString = board.substring(i, i + 1);
            ChessPiece piece = Parser.parseChessPiece(pieceString, row, col);
            assert (row < SIZE);
            assert (col < SIZE);
            boardTiles[row][col] = new ChessTile(piece);
            col++;
            if (col == SIZE) {
                col = 0;
                row++;
            }
            if (row == SIZE) {
                break;
            }
        }
        return new ChessBoard(this.playerColor, boardTiles);
    }

    //@@author ken-ruster
    /**
     * Takes in an array of multiple moves, and executes them in order. Also updates the move history
     * stored in the human and CPU objects.
     *
     * @param moves ArrayList of moves to be executed
     * @param human Object representing the human player
     * @param cpu Object representing the CPU player
     * @throws ChessMasterException
     */
    public void executeMoveArray(ArrayList<String> moves, Human human, CPU cpu) throws ChessMasterException {
        boolean isPlayersTurn = playerColor.isWhite();

        for (String move : moves) {
            String[] moveCommandArray = move.split("\\s+");
            boolean isPromote = moveCommandArray[0].equals(PROMOTE_MOVE_STRING);

            if (!isPromote) {
                Move toExecute = Parser.parseMove(move, this, false);
                if (!toExecute.isValid(this)) {
                    throw new InvalidMoveException(INVALID_SAVE_STRING);
                }
                this.executeMove(toExecute);

                if (isPlayersTurn) {
                    human.addMove(toExecute);
                } else {
                    cpu.addMove(toExecute);
                }
            } else {
                Coordinate coord = Coordinate.parseAlgebraicCoor(moveCommandArray[1]);
                ChessPiece oldPiece = this.getPieceAtCoor(coord);
                assert oldPiece instanceof Pawn;
                if (!this.canPromote(new Move(coord, coord, oldPiece))) {
                    throw new InvalidMoveException(INVALID_PROMOTE_STRING);
                }
                ChessPiece newPiece = Parser.parsePromote(oldPiece, moveCommandArray[2]);
                this.setPromotionPiece(coord, newPiece);

                PromoteMove promoteMove = MoveFactory.createPromoteMove(coord, (Pawn) oldPiece, newPiece);
                if (isPlayersTurn) {
                    human.addMove(promoteMove);
                } else {
                    cpu.addMove(promoteMove);
                }
            }

            isPlayersTurn = !isPlayersTurn;
        }
    }

    @Override
    public String toString() {
        StringBuilder boardString = new StringBuilder();
        for (ChessTile[] row : board) {
            for (ChessTile tile : row) {
                boardString.append(tile.toFileString());
            }
        }
        return boardString.toString();
    }

    //@@author TongZhengHong
    public boolean isPieceFriendly(ChessPiece otherPiece) {
        return this.playerColor == otherPiece.getColor();
    }

    public boolean isPieceOpponent(ChessPiece otherPiece) {
        return this.playerColor != otherPiece.getColor();
    }

    public boolean isTileOccupied(Coordinate coord) {
        ChessPiece piece = this.getPieceAtCoor(coord);
        return piece == null || !(piece instanceof EmptyPiece);
    }

    //@@author ken_ruster
    public boolean equals(ChessBoard otherBoard) {
        String otherBoardString = otherBoard.toString();

        return otherBoardString.equals(this.toString());
    }
}
