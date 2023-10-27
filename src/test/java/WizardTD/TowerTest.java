package WizardTD;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class TowerTest {

    private PApplet pApplet;
    private Board board;
    private Mana mana;
    private String configPath;
    private MazeSolver solver;
    PImage[] deathFrames = new PImage[1];

    @BeforeEach
    public void setup() {

        pApplet = new PApplet();
        board = new Board("config.json", pApplet);
        mana = new Mana("config.json", pApplet); // Assuming Mana has a constructor that initializes with a set value
        configPath = "path/to/config.json"; // Ensure you have a valid JSON config for this path or mock the loadConfig method.
    }

    @Test
    public void testTowerCreation() {
        Tower tower = new Tower(1, 1, pApplet, 1, 1, 1, configPath, board, mana);

        assertNotNull(tower);
        assertEquals(1, tower.getTileX());
        assertEquals(1, tower.getTileY());
    }

    @Test
    public void testShootingAtMonster() {

        List<List<int[]>> paths = solver.findPaths();
        Random rand = new Random();
        List<int[]> path = paths.get(rand.nextInt(paths.size()));
        deathFrames[0] = pApplet.loadImage("/src/main/resources/WizardTD/gremlin1.png");
        Tower tower = new Tower(1, 1, pApplet, 1, 1, 1, configPath, board, mana);
        ArrayList<Monster> monsters = new ArrayList<>();
        monsters.add(new Monster("gremlin", 100, 1, 0, 0, path, pApplet.loadImage("/src/main/resources/WizardTD/gremlin.png"), 0,deathFrames, mana));

        // Let's simulate some time so the tower can shoot
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        tower.shoot(monsters);

        assertTrue(tower.fireballs.size() > 0);
    }

    @Test
    public void testTowerUpgrades() {
        Tower tower = new Tower(1, 1, pApplet, 1, 1, 1, configPath, board, mana);
        double initialRange = tower.getRange();

        tower.upgradeRange();

        assertTrue(tower.getRange() > initialRange);
    }

    @Test
    public void testTowerCostAfterUpgrades() {
        Tower tower = new Tower(1, 1, pApplet, 1, 1, 1, configPath, board, mana);
        int initialCost = tower.getCost();

        tower.upgradeRange();

        assertTrue(tower.getCost() > initialCost);
    }

    @Test
    public void testHandleUpgrades() {
        Tower tower = new Tower(1, 1, pApplet, 1, 1, 1, configPath, board, mana);
        int[] upgrades = {1, 1, 1};

        List<Integer> results = tower.handleUpgrade(upgrades);

        assertFalse(results.isEmpty());
        assertTrue(results.get(0) > 0); // Assuming the mana cost for the upgrade is positive
    }
}
