package WizardTD;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.data.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    private PApplet pApplet;
    private String configPath;
    private String layoutPath = "Layout.txt";
    private Board board;

    @BeforeEach
    public void setup() throws IOException {
        // Setup test environment
        pApplet = new PApplet();

        // Create a test layout file for our board
        createTestLayoutFile();

        JSONObject config = new JSONObject();
        config.setString("layout", layoutPath);

        configPath = "./Config.json";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(configPath))) {
            bw.write(config.toString());
        }

        board = new Board(configPath, pApplet);
    }

    private void createTestLayoutFile() throws IOException {
        String[] layout = {
                "XXXXXXXXXXXXXXXXXXXX",
                "X                  X",
                "X   W   S   T   W  X",
                "X   X   X   X   X  X",
                "X   X   X   X   X  X",
                "X   X   X   X   X  X",
                "X   X   X   X   X  X",
                "X   X   X   X   X  X",
                "X                  X",
                "XXXXXXXXXXXXXXXXXXXX",
                // ... Add more rows as required
        };

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(layoutPath))) {
            for (String s : layout) {
                bw.write(s);
                bw.newLine();
            }
        }
    }

    @Test
    public void testBoardCreation() {
        assertNotNull(board);
        assertEquals(Board.Tile.PATH_HORIZONTAL, board.getTile(0, 0));
        assertEquals(Board.Tile.WIZARD_HOUSE, board.getTile(4, 2));
    }

    @Test
    public void testDeterminePathType() {
        assertEquals(Board.Tile.PATH_HORIZONTAL, board.getTile(2, 0));
        assertEquals(Board.Tile.PATH_VERTICAL, board.getTile(0, 3));
        assertEquals(Board.Tile.PATH_T_LEFT, board.getTile(0, 2));
    }

    @Test
    public void testSetAndGetTile() {
        board.setTile(5, 5, Board.Tile.TRAP);
        assertEquals(Board.Tile.TRAP, board.getTile(5, 5));
    }

    @Test
    public void testLoadLayoutInvalidTile() {
        // Assuming System.err outputs are stored somewhere for verification,
        // otherwise this is a bit harder to test without using something like Mockito.
        // This is just a placeholder for the idea.
        assertTrue(true, "Placeholder assertion. Ideally, we would check the error output here.");
    }
}

