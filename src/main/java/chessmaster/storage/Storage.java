package chessmaster.storage;

import chessmaster.exceptions.ChessMasterException;
import chessmaster.exceptions.LoadBoardException;
import chessmaster.exceptions.SaveBoardException;
import chessmaster.game.ChessBoard;
import chessmaster.game.ChessTile;
import chessmaster.game.Color;
import chessmaster.game.Coordinate;
import chessmaster.parser.Parser;
import chessmaster.pieces.ChessPiece;
import chessmaster.user.CPU;
import chessmaster.user.Human;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Storage {
    //@@author ken_ruster
    private static final String LOAD_BOARD_MISMATCH_STRING =
            "Board state does not match state dictated by move history!";
    private static final String PATH_EMPTY_STRING = "File path cannot be empty or null";
    //@@author TriciaBK
    private String filePathString;
    private File storageFile;
    private int blackPieceNum;
    private int whitePieceNum;
    private boolean blackKingPresent;
    private boolean whiteKingPresent;
    private Scanner fileScanner;
    private Coordinate lastMove;

    public Storage(String filePath) {
        filePathString = filePath;
        storageFile = new File(filePath);
        assert !filePathString.isEmpty() && filePath != null : PATH_EMPTY_STRING;
    }

    //@@author TongZhengHong
    /**
     * Creates a ChessMaster program file to store game state, including necessary
     * parent directories.
     *
     * @throws ChessMasterException If there is an error creating the file or parent
     *                              directories.
     */
    private void createChessMasterFile() throws ChessMasterException {
        // Create the necessary parent directories for new file
        if (!storageFile.exists()) {
            storageFile.getParentFile().mkdirs();
        }

        // Create file if it does not exist
        try {
            storageFile.createNewFile();
        } catch (IOException e) {
            throw new ChessMasterException("Fatal: Error creating file: " + filePathString);
        }
    }

    //@@author TriciaBK
    /**
     * Saves the state of the ChessBoard to a file. Writes the player's color to the
     * first line
     * and subsequently chess pieces in a 8 x 8 format.
     *
     * @param board       The ChessBoard to save.
     * @throws ChessMasterException If there is an error saving the board to a file.
     */
    public void saveBoard(ChessBoard board, Color currentColor, Human human, CPU cpu) throws ChessMasterException {
        createChessMasterFile();

        try {
            FileWriter fileWriter = new FileWriter(storageFile);
            fileWriter.write(board.getPlayerColor().name());
            fileWriter.write(System.lineSeparator());

            fileWriter.write(String.valueOf(board.getDifficulty()));
            fileWriter.write(System.lineSeparator());

            fileWriter.write(currentColor.name());
            fileWriter.write(System.lineSeparator());

            //@@author ken_ruster
            // Save human moves
            fileWriter.write(human.movesToString());
            fileWriter.write(System.lineSeparator());

            // Save cpu moves
            fileWriter.write(cpu.movesToString());
            fileWriter.write(System.lineSeparator());

            //@@author TriciaBK
            for (int row = 0; row < ChessBoard.SIZE; row++) {
                for (int col = 0; col < ChessBoard.SIZE; col++) {
                    ChessPiece piece = board.getPieceAtCoor(new Coordinate(col, row));
                    fileWriter.write(piece.toString());
                }
                fileWriter.write(System.lineSeparator());
            }

            for (int row = 0; row < ChessBoard.SIZE; row++) {
                for (int col = 0; col < ChessBoard.SIZE; col++) {
                    ChessPiece piece = board.getPieceAtCoor(new Coordinate(col, row));
                    String hasMovedString = piece.getHasMoved() ? "1" : "0";
                    fileWriter.write(hasMovedString);
                }
                fileWriter.write(System.lineSeparator());
            }

            fileWriter.close();
        } catch (IOException e) {
            throw new SaveBoardException();
        }
    }

    //@@author TongZhengHong
    public void resetBoard() throws ChessMasterException {
        createChessMasterFile();
        try {
            FileWriter fileWriter = new FileWriter(storageFile);
            fileWriter.write("");
            fileWriter.close();

        } catch (IOException e) {
            throw new SaveBoardException();
        }
    }

    //@@author TriciaBK
    /**
     * Loads the state of the chessboard from a file.
     * Ignores the first line player color information as it can be retrieved with
     * loadPlayerColor() method
     *
     * @return A 2D array of ChessTile objects representing the loaded chessboard.
     * @throws ChessMasterException If there is an error loading the board from the
     *                              file.
     */
    public ChessTile[][] loadBoard() throws ChessMasterException {
        createChessMasterFile();

        blackPieceNum = 0;
        whitePieceNum = 0;
        blackKingPresent = false;
        whiteKingPresent = false;

        try {
            fileScanner = new Scanner(storageFile);
        } catch (FileNotFoundException e) {
            throw new LoadBoardException("Invalid file path: " + filePathString);
        }

        // Skip first five lines
        for (int i = 0; i < 5; i++) {
            if (fileScanner.hasNext()) {
                fileScanner.nextLine();
            }
        }

        int rowIndex = 0;
        ChessTile[][] boardTiles = new ChessTile[ChessBoard.SIZE][ChessBoard.SIZE];
        while (rowIndex < ChessBoard.SIZE && fileScanner.hasNext()) {
            String chessRowLine = fileScanner.nextLine();
            if (chessRowLine.length() != ChessBoard.SIZE) {
                fileScanner.close();
                throw new LoadBoardException();
            }

            for (int col = 0; col < ChessBoard.SIZE; col++) {
                String chessPieceString = String.valueOf(chessRowLine.charAt(col));
                ChessPiece initialPiece = Parser.parseChessPiece(chessPieceString, rowIndex, col);
                //@@author onx001
                if (!this.isPieceValid(initialPiece)) {
                    fileScanner.close();
                    throw new LoadBoardException();
                }
                //@@author TriciaBK
                boardTiles[rowIndex][col] = new ChessTile(initialPiece);
            }
            rowIndex++;
        }

        boolean hasBothKings = blackKingPresent && whiteKingPresent;
        if (!hasBothKings) {
            fileScanner.close();
            throw new LoadBoardException();
        }

        rowIndex = 0;
        while (rowIndex < ChessBoard.SIZE && fileScanner.hasNext()) {
            String chessRowLine = fileScanner.nextLine();
            if (chessRowLine.length() != ChessBoard.SIZE) {
                fileScanner.close();
                throw new LoadBoardException();
            }

            for (int col = 0; col < ChessBoard.SIZE; col++) {
                boolean hasMoved = Character.getNumericValue(chessRowLine.charAt(col)) > 0;
                if (hasMoved) {
                    boardTiles[rowIndex][col].getChessPiece().setHasMoved();
                }
            }

            rowIndex++;
        }

        fileScanner.close();
        return boardTiles;
    }

    //@@author ken-ruster
    /**
     * Executes moves saved in the txt file so that it can be checked against the save board state.
     * Also stores the saved move history of both the human and CPU.
     *
     * @param playerColor The color which the player is playing as
     * @param otherBoard A temporary board to be compared with the board saved in the .txt file
     * @param human Object representing information about the human player
     * @param cpu Object representing information about the computer-controlled player
     * @throws ChessMasterException
     */
    public ChessBoard executeSavedMoves(Color playerColor,
                                  ChessBoard otherBoard,
                                  ChessBoard board,
                                  Human human,
                                  CPU cpu) throws ChessMasterException {
        ArrayList<String> moveStringList = new ArrayList();
        ArrayList<String> humanMoves = loadHumanMoves();
        ArrayList<String> cpuMoves = loadCPUMoves();

        // Merge move string arrays into a singular array
        if (playerColor.isWhite()) {
            for (String move : humanMoves) {
                moveStringList.add(move);
            }

            for (int i = 0; i < cpuMoves.size(); i ++) {
                moveStringList.add(2 * i + 1, cpuMoves.get(i));
            }
        } else if (playerColor.isBlack()) {
            for (String move : cpuMoves) {
                moveStringList.add(move);
            }

            for (int i = 0; i < humanMoves.size(); i ++) {
                moveStringList.add(2 * i + 1, humanMoves.get(i));
            }
        } else {
            throw new LoadBoardException();
        }

        //Execute move string Array
        board.executeMoveArray(moveStringList, human, cpu);

        //@@author onx001
        // get the destination coordinate of the last move
        try {
            String lastMoveString = moveStringList.get(moveStringList.size() - 1);
            String[] lastMoveArray = lastMoveString.split(" ");
            lastMove = Coordinate.parseAlgebraicCoor(lastMoveArray[1]);

            if (otherBoard.getPieceAtCoor(lastMove).isPawn()) {
                otherBoard.getPieceAtCoor(lastMove).setEnPassant();
                board.getPieceAtCoor(lastMove).setEnPassant();
            }
        } catch (Exception e) {
            assert moveStringList.size() == 0 : "Last move should be empty";
        }



        //@@author TriciaBK
        // Check obtained board with loaded board state
        if (!board.equals(otherBoard)) {
            throw new LoadBoardException(LOAD_BOARD_MISMATCH_STRING);
        }

        return board;
    }


    //@@author onx001
    private boolean isPieceValid (ChessPiece initialPiece) {
        if (initialPiece.isBlackKing()) {
            if (blackKingPresent) {
                return false;
            } else {
                blackKingPresent = true;
                blackPieceNum++;
            }
        } else if (initialPiece.isWhiteKing()) {
            if (whiteKingPresent) {
                return false;
            } else {
                whiteKingPresent = true;
                whitePieceNum++;
            }
        } else if (initialPiece.isBlack()) {
            if (blackPieceNum >= ChessBoard.MAX_PIECES) {
                return false;
            } else {
                blackPieceNum++;
            }
        } else if (initialPiece.isWhite()) {
            if (whitePieceNum >= ChessBoard.MAX_PIECES) {
                return false;
            } else {
                whitePieceNum++;
            }
        }

        return true;
    }

    //@@author TongZhengHong
    /**
     * Loads the player's color from a file.
     * Expects the player color information on the first line of text file.
     *
     * @return The player's color as a Color enumeration.
     * @throws ChessMasterException If there is an error loading the player's color
     *                              from the file.
     */
    public Color loadPlayerColor() throws ChessMasterException {
        createChessMasterFile();

        Scanner fileScanner;
        try {
            fileScanner = new Scanner(storageFile);
        } catch (FileNotFoundException e) {
            throw new LoadBoardException("Invalid file path: " + filePathString);
        }

        if (fileScanner.hasNext()) {
            String colorLine = fileScanner.nextLine();
            Color playerColor = Parser.parsePlayerColor(colorLine);

            fileScanner.close();
            return playerColor;
        }

        fileScanner.close();
        throw new LoadBoardException();
    }

    //@@author onx001
    /**
     * Loads the difficulty from a file.
     * Expects the difficulty information on the second line of text file.
     * @return The difficulty as an integer.
     */
    public int loadDifficulty() throws ChessMasterException {
        createChessMasterFile();

        Scanner fileScanner;
        try {
            fileScanner = new Scanner(storageFile);
        } catch (FileNotFoundException e) {
            throw new LoadBoardException("Invalid file path: " + filePathString);
        }

        // Skip player color first line
        if (fileScanner.hasNext()) {
            fileScanner.nextLine();
        }

        if (fileScanner.hasNext()) {
            try {
                String difficultyLine = fileScanner.nextLine();
                int difficulty = Parser.parseDifficulty(difficultyLine);

                fileScanner.close();
                if (difficulty < 1 || difficulty > 3) {
                    throw new LoadBoardException();
                }
                return difficulty;
            } catch (NumberFormatException e) {
                throw new LoadBoardException();
            }
        }

        fileScanner.close();
        throw new LoadBoardException();
    }

    //@@author TriciaBK
    /**
     * Loads the current turn player's
     * @return The difficulty as an integer.
     */
    public Color loadCurrentColor() throws ChessMasterException {
        createChessMasterFile();

        Scanner fileScanner;
        try {
            fileScanner = new Scanner(storageFile);
        } catch (FileNotFoundException e) {
            throw new LoadBoardException("Invalid file path: " + filePathString);
        }

        if (fileScanner.hasNext()) {
            fileScanner.nextLine();
        }

        if (fileScanner.hasNext()) {
            fileScanner.nextLine();
        }

        if (fileScanner.hasNext()) {
            String currentColorString = fileScanner.nextLine();
            Color color = Parser.parsePlayerColor(currentColorString);
            fileScanner.close();
            return color;
        }

        fileScanner.close();
        throw new LoadBoardException();
    }

    public String getFilePath() {
        return this.filePathString;
    }

    //@@author ken-ruster
    /**
     * Loads the history of moves made by the human player in the saved game.
     * Parses the moves into Move objects, and returns a null array if no moves were found.
     *
     * @return ArrayList containing the move history of the human player
     * @throws ChessMasterException
     */
    public ArrayList<String> loadHumanMoves() throws ChessMasterException {
        createChessMasterFile();

        Scanner fileScanner;
        try {
            fileScanner = new Scanner(storageFile);
        } catch (FileNotFoundException e) {
            throw new LoadBoardException("Invalid file path: " + filePathString);
        }

        // Skip first 3 lines
        for (int i = 0; i < 3; i ++) {
            if (fileScanner.hasNext()) {
                fileScanner.nextLine();
            }
        }

        ArrayList<String> out = new ArrayList<String>();
        if (fileScanner.hasNext()) {
            String[] movesArray = fileScanner.nextLine().split(", ");
            Arrays.stream(movesArray)
                    .sequential()
                    .filter(x -> !x.equals(""))
                    .forEach(x -> out.add(x));
        }

        fileScanner.close();
        return out;
    }

    /**
     * Loads the history of moves made by the CPU player in the saved game.
     * Parses the moves into Move objects, and returns a null array if no moves were found.
     *
     * @return ArrayList containing the move history of the CPU player
     * @throws ChessMasterException
     */
    public ArrayList<String> loadCPUMoves() throws ChessMasterException {
        createChessMasterFile();

        Scanner fileScanner;
        try {
            fileScanner = new Scanner(storageFile);
        } catch (FileNotFoundException e) {
            throw new LoadBoardException("Invalid file path: " + filePathString);
        }

        // Skip first 4 lines
        for (int i = 0; i < 4; i ++) {
            if (fileScanner.hasNext()) {
                fileScanner.nextLine();
            }
        }

        ArrayList<String> out = new ArrayList<String>();
        if (fileScanner.hasNext()) {
            String[] movesArray = fileScanner.nextLine().split(", ");
            Arrays.stream(movesArray)
                    .sequential()
                    .filter(x -> !x.equals(""))
                    .forEach(x -> out.add(x));
        }

        fileScanner.close();
        return out;
    }

}
