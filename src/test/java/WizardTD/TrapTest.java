package WizardTD;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


import processing.core.PApplet;
import processing.core.PImage;

public class TrapTest {
    PImage[] deathFrames = new PImage[1];
    private PApplet pApplet;
    private Board board;
    private Trap trap;
    private Mana mana;
    private MazeSolver solver;
    @BeforeEach
    public void setUp() {
        pApplet = new PApplet(); // Assuming PApplet can be instantiated directly
        board = new Board("config.json", pApplet); // Assuming Board has a default constructor
        trap = new Trap(5, 5, pApplet, board);
    }

    // Test if trap is created at the specified position
    @Test
    public void testTrapPosition() {
        assertEquals(160, trap.getCenterX());
        assertEquals(216, trap.getCenterY());
    }

    // Test if a monster within range gets damaged and the trap gets triggered
    @Test
    public void testCheckAndDamageMonsters() {
        List<List<int[]>> paths = solver.findPaths();
        Random rand = new Random();
        List<int[]> path = paths.get(rand.nextInt(paths.size()));
        deathFrames[0] = pApplet.loadImage("/src/main/resources/WizardTD/gremlin1.png");
        Monster monster = new Monster("gremlin", 100, 1, 0, 0, path, pApplet.loadImage("/src/main/resources/WizardTD/gremlin.png"), 0,deathFrames, mana); // Assuming Monster has a default constructor and required methods
        monster.currentX = 166.0f; // Setting it within range of trap
        monster.currentY = 216.0f; // Setting it within range of trap

        trap.checkAndDamageMonsters(new ArrayList<>(Arrays.asList(monster)));

        // Assuming Monster has a getHealth method to check if damage was applied
        assertEquals(95, monster.getcurrentHp()); // assuming initial health is 100
    }

    // Test trap's center calculations
    @Test
    public void testTrapCenter() {
        assertEquals(160, trap.getCenterX());
        assertEquals(216, trap.getCenterY());
    }

    // The display method mainly deals with visual elements; we'll skip it for now
    // unless there's a direct way to test visual output without mocks.
}
