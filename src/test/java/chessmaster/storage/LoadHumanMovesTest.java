package chessmaster.storage;

import chessmaster.exceptions.ChessMasterException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoadHumanMovesTest {
    private static final String[] EXPECTED_LIST = {"d2 d4", "g1 f3", "c1 f4", "c2 c3", "b1 d2", "d1 d2"};
    private static final String FILE_PATH_STRING = "src/test/resources/storageTest.txt";
    Storage storage = new Storage(FILE_PATH_STRING);

    @Test
    public void testLoadHumanMoves_validString() throws ChessMasterException {
        ArrayList<String> compareList = storage.loadHumanMoves();
        assertTrue(compareList.equals(Arrays.asList(EXPECTED_LIST)));
    }
}
