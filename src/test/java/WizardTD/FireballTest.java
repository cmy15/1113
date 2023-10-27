package WizardTD;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;

import static org.junit.jupiter.api.Assertions.*;

public class FireballTest {

    private PApplet pAppletStub;
    private Monster monsterStub;

    @BeforeEach
    public void setUp() {
        pAppletStub = new PApplet();
        monsterStub = new Monster(
                "gremlin", // type
                100,      // hp
                1.5f,     // speed
                10.0f,    // armor, 10% reduction
                5,        // mana gained on kill
                null,     // path
                null,     // sprite
                0,        // currentSpawnTime
                null,     // deathFrames
                null      // playerMana
        );
    }

    @Test
    public void testFireballMovement() {
        Fireball fireball = new Fireball(0, 0, 20, monsterStub, pAppletStub);
        fireball.update();

        // Check that the fireball has moved from its original position
        assertFalse(fireball.position.x == 0 && fireball.position.y == 0);
    }

    @Test
    public void testCalculateDamage() {
        Fireball fireball = new Fireball(0, 0, 20, monsterStub, pAppletStub);

        int expectedDamage = 18; // 20 - 10% of 20
        assertEquals(expectedDamage, fireball.calculateDamage(monsterStub));
    }

    @Test
    public void testFireballOutOfBounds() {
        Fireball fireball = new Fireball(-10, -10, 20, monsterStub, pAppletStub);

        assertTrue(fireball.isOutOfBounds());
    }

    @Test
    public void testFireballStatusAfterReachingTarget() {
        Fireball fireball = new Fireball(0, 0, 20, monsterStub, pAppletStub);

        // Setting monster's current position very close to fireball
        monsterStub.currentX = 1;
        monsterStub.currentY = 1;

        fireball.update();

        assertFalse(fireball.isActive);
    }

    @Test
    public void testFireballStatusAfterOutOfBounds() {
        Fireball fireball = new Fireball(-10, -10, 20, monsterStub, pAppletStub);

        fireball.update();

        assertFalse(fireball.isActive);
    }
}
