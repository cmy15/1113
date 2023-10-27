package WizardTD;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.data.JSONObject;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MazeSolverTest {

    private MazeSolver mazeSolver;
    private PAppletMock pAppletMock;

    @BeforeEach
    public void setup() {
        pAppletMock = new PAppletMock();
        mazeSolver = new MazeSolver("path_to_config.json", pAppletMock);
    }

    @Test
    public void testMazeSolverCreation() {
        assertNotNull(mazeSolver);
    }

    @Test
    public void testFindPaths() {
        List<List<int[]>> paths = mazeSolver.findPaths();

        assertFalse(paths.isEmpty());
        assertTrue(paths.size() > 0);

        // Check if any path leads to the 'W' cell
        boolean foundW = false;
        for (List<int[]> path : paths) {
            int[] endPoint = path.get(path.size() - 1);
            if (pAppletMock.getMapValue(endPoint[0], endPoint[1]) == 'W') {
                foundW = true;
                break;
            }
        }
        assertTrue(foundW);
    }
}

class PAppletMock extends PApplet {

    // Mocked method of loadJSONObject
    public JSONObject loadJSONObject(String configPath) {
        JSONObject mockJson = new JSONObject();
        mockJson.setString("layout", "path_to_layout.txt");
        return mockJson;
    }

    // Mocked map for the purpose of tests
    public char getMapValue(int x, int y) {
        // Example map, you can customize this for testing
        char[][] mockMap = {
                {'X', ' ', ' ', ' ', 'W'},
                {' ', ' ', 'X', ' ', ' '},
                {' ', 'X', ' ', 'X', ' '},
                {' ', ' ', ' ', ' ', ' '},
                {'X', ' ', 'X', ' ', 'X'}
        };
        return mockMap[x][y];
    }
}
