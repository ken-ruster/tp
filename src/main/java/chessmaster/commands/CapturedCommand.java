package chessmaster.commands;

import chessmaster.exceptions.ChessMasterException;
import chessmaster.game.Game;
import chessmaster.pieces.ChessPiece;
import chessmaster.user.CPU;
import chessmaster.user.Human;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CapturedCommand extends Command {

    public static final String CAPTURED_COMMAND_STRING = "captured";

    private String getCapturedDisplayString(
            ArrayList<ChessPiece> humanInPlay, ArrayList<ChessPiece> humanCaptured,
            ArrayList<ChessPiece> cpuInPlay, ArrayList<ChessPiece> cpuCaptured) {

        StringBuilder sb = new StringBuilder();
        sb.append("Player's pieces\n");
        sb.append("-".repeat(40)).append(System.lineSeparator());

        // In play: Human
        sb.append("In play:\n");
        appendPiecesWithCount(sb, humanInPlay);

        // Captured: Human
        sb.append("\nCaptured:\n"); // Add a separator between "In play" and "Captured"
        appendPiecesWithCount(sb, humanCaptured);

        // Space between Human and CPU pieces
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());

        sb.append("CPU's pieces\n");
        sb.append("-".repeat(40)).append(System.lineSeparator());

        // In play: CPU
        sb.append("\nIn play:\n"); // Add a separator between "Captured" and "In play"
        appendPiecesWithCount(sb, cpuInPlay);

        // Captured: CPU
        sb.append("\nCaptured:\n"); // Add a separator between "In play" and "Captured"
        appendPiecesWithCount(sb, cpuCaptured);

        return sb.toString();
    }

    private void appendPiecesWithCount(StringBuilder sb, List<ChessPiece> pieces) {
        Map<String, Integer> pieceCountMap = new HashMap<>();
        for (ChessPiece piece : pieces) {
            String pieceName = piece.getPieceName();
            pieceCountMap.put(pieceName, pieceCountMap.getOrDefault(pieceName, 0) + 1);
        }

        // This relies on the fact that the "In play" pieces should never be empty
        if (pieceCountMap.isEmpty()) {
            sb.append("No pieces have been captured yet!\n");
        }

        for (Map.Entry<String, Integer> entry : pieceCountMap.entrySet()) {
            sb.append("- ").append(entry.getKey());
            if (entry.getValue() > 1) {
                sb.append(" x").append(entry.getValue());
            }
            sb.append(System.lineSeparator());
        }
    }

    public CommandResult execute(Game game) throws ChessMasterException {
        Human human = game.getHuman();
        ArrayList<ChessPiece> humanInPlay = new ArrayList<>();
        ArrayList<ChessPiece> humanCaptured = new ArrayList<>();
        for (ChessPiece p : human.getPieces()) {
            if (p.getIsCaptured()) {
                humanCaptured.add(p);
            } else {
                humanInPlay.add(p);
            }
        }

        CPU cpu = game.getCPU();
        ArrayList<ChessPiece> cpuInPlay = new ArrayList<>();
        ArrayList<ChessPiece> cpuCaptured = new ArrayList<>();
        for (ChessPiece p : cpu.getPieces()) {
            if (p.getIsCaptured()) {
                cpuCaptured.add(p);
            } else {
                cpuInPlay.add(p);
            }
        }

        String displayString = this.getCapturedDisplayString(
                humanInPlay, humanCaptured,
                cpuInPlay, cpuCaptured
        );

        return new CommandResult(displayString);
    }
}
