//@@author ken-ruster
package chessmaster.parser;

import chessmaster.game.Color;
import chessmaster.pieces.Bishop;
import chessmaster.pieces.ChessPiece;
import chessmaster.pieces.Knight;
import chessmaster.pieces.Pawn;
import chessmaster.pieces.Queen;
import chessmaster.pieces.Rook;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParsePromoteTest {

    @Test
    public void testPromote_inputValidPiece() {
        ChessPiece promotedQueenWhite = Parser.parsePromote(new Pawn(0, 0, Color.WHITE), "q");
        assertEquals(promotedQueenWhite.toString(), Queen.QUEEN_WHITE);

        ChessPiece promotedQueenBlack = Parser.parsePromote(new Pawn(0, 0, Color.BLACK), "q");
        assertEquals(promotedQueenBlack.toString(), Queen.QUEEN_BLACK);

        ChessPiece promotedRookWhite = Parser.parsePromote(new Pawn(0, 0, Color.WHITE), "r");
        assertEquals(promotedRookWhite.toString(), Rook.ROOK_WHITE);

        ChessPiece promotedRookBlack = Parser.parsePromote(new Pawn(0, 0, Color.BLACK), "r");
        assertEquals(promotedRookBlack.toString(), Rook.ROOK_BLACK);

        ChessPiece promotedKnightWhite = Parser.parsePromote(new Pawn(0, 0, Color.WHITE), "n");
        assertEquals(promotedKnightWhite.toString(), Knight.KNIGHT_WHITE);

        ChessPiece promotedKnightBlack = Parser.parsePromote(new Pawn(0, 0, Color.BLACK), "n");
        assertEquals(promotedKnightBlack.toString(), Knight.KNIGHT_BLACK);

        ChessPiece promotedBishopWhite = Parser.parsePromote(new Pawn(0, 0, Color.WHITE), "b");
        assertEquals(promotedBishopWhite.toString(), Bishop.BISHOP_WHITE);

        ChessPiece promotedBishopBlack = Parser.parsePromote(new Pawn(0, 0, Color.BLACK), "b");
        assertEquals(promotedBishopBlack.toString(), Bishop.BISHOP_BLACK);
    }

    @Test
    public void testPromote_inputEmpty_expectReturnPawn() {
        ChessPiece promotedEmptyWhite = Parser.parsePromote(new Pawn(0, 0, Color.WHITE), "");
        assertEquals(promotedEmptyWhite.toString(), Pawn.PAWN_WHITE);

        ChessPiece promotedEmptyBlack = Parser.parsePromote(new Pawn(0, 0, Color.BLACK), "");
        assertEquals(promotedEmptyBlack.toString(), Pawn.PAWN_BLACK);
    }

    @Test
    public void testPromote_inputInvalid_expectReturnPawn() {
        ChessPiece promotedEmptyWhite = Parser.parsePromote(new Pawn(0, 0, Color.WHITE), "isdjncv");
        assertEquals(promotedEmptyWhite.toString(), Pawn.PAWN_WHITE);

        ChessPiece promotedEmptyBlack = Parser.parsePromote(new Pawn(0, 0, Color.BLACK), "jasdnc");
        assertEquals(promotedEmptyBlack.toString(), Pawn.PAWN_BLACK);
    }

}
