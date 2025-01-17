//@@author ken-ruster
package chessmaster.parser;

import chessmaster.game.Color;
import chessmaster.pieces.ChessPiece;
import chessmaster.pieces.Pawn;
import chessmaster.pieces.Queen;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest {

    @Test
    public void promoteTest() {
        ChessPiece promoted = Parser.parsePromote(new Pawn(0, 0, Color.WHITE), "q");
        assertEquals(promoted.toString(), Queen.QUEEN_WHITE);
    }

}
