package chessmaster.storage;

import chessmaster.exceptions.ChessMasterException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoadCpuMovesTest {
    private static final String[] EXPECTED_LIST = {"g8 f6", "b8 c6", "d7 d5", "f6 e4", "e4 d2", "f7 f5"};
    private static final String FILE_PATH_STRING = "src/test/resources/storageTest.txt";
    Storage storage = new Storage(FILE_PATH_STRING);

    @Test
    public void testLoadCPUMoves_validString() throws ChessMasterException {
        ArrayList<String> compareList = storage.loadCPUMoves();
        assertTrue(compareList.equals(Arrays.asList(EXPECTED_LIST)));
    }
}
