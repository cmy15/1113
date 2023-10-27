package WizardTD;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MonsterTest {

    private PApplet pApplet;
    private Monster monster;
    private Mana mana;
    private List<int[]> path;
    private PImage sprite;
    private PImage[] deathAnimation;

    @BeforeEach
    public void setup() {
        pApplet = new PApplet();
        mana = new Mana("./Config.json", pApplet);
        sprite = new PImage();
        deathAnimation = new PImage[]{new PImage(), new PImage(), new PImage()}; // Assuming a 3-frame death animation for simplicity.

        path = new ArrayList<>();
        path.add(new int[]{0, 0});
        path.add(new int[]{0, 1});
        path.add(new int[]{1, 1});

        monster = new Monster("TestType", 100, 1.0f, 0.0f, 10, path, sprite, 0.0f, deathAnimation, mana);
    }

    @Test
    public void testMonsterCreation() {
        assertNotNull(monster);
        assertEquals("TestType", monster.type);
        assertEquals(100, monster.getcurrentHp());
    }

    @Test
    public void testMove() {
        monster.move();
        assertTrue(monster.currentX > 0 || monster.currentY > 0);
    }

    @Test
    public void testDamage() {
        monster.setDamage(10);
        assertEquals(90, monster.getcurrentHp());

        monster.setDamage(90);
        assertFalse(monster.isAlive());
    }

    @Test
    public void testBanish() {
        int cost = monster.banish();
        assertEquals(100, cost);
        assertEquals(0, monster.currentPathIndex);
        assertTrue(monster.isAlive());
    }

    @Test
    public void testRespawn() {
        monster.move();
        monster.respawn();
        assertEquals(0, monster.currentPathIndex);
        assertTrue(monster.isAlive());
    }
}

