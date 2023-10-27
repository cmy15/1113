package WizardTD;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import processing.core.PApplet;

class ManaTest {

    private Mana mana;
    private PApplet pApplet;

    @BeforeEach
    void setUp() {
        pApplet = new PApplet();
        // Note: Assuming an actual config file at "path/to/test/config.json"
        mana = new Mana("./config.json", pApplet);
    }

    // Test the initial value of mana
    @Test
    void testInitialMana() {
        assertEquals(100, mana.getCurrentMana(), "Initial mana should be set to 100 based on the config file");
    }

    // Test mana growth after time passes
    @Test
    void testUpdateMana() {
        mana.update(1);
        assertTrue(mana.getCurrentMana() > 100, "Mana should have increased after 1 second due to mana gain rate");
    }

    // Test mana increase function
    @Test
    void testIncreaseMana() {
        mana.increase(50);
        assertEquals(150, mana.getCurrentMana(), "Mana should increase by the given amount when the increase method is called");
    }

    // Test mana decrease function
    @Test
    void testDecreaseMana() {
        mana.decrease(50);
        assertEquals(50, mana.getCurrentMana(), "Mana should decrease by the given amount when the decrease method is called");
    }

    // Test mana cap value
    @Test
    void testManaCap() {
        mana.increase(500);
        assertEquals(200, mana.getCurrentMana(), "Mana should not exceed the initial cap of 200 as per the config file");
    }

    // Test if the ManaPoolSpell can be cast
    @Test
    void testCanCastManaPoolSpell() {
        assertTrue(mana.canCastManaPoolSpell(), "Should be able to cast ManaPoolSpell initially given enough mana");
    }

    // Test the effects after casting the ManaPoolSpell
    @Test
    void testCastManaPoolSpell() {
        float initialMana = mana.getCurrentMana();
        float initialSpellCost = mana.getManaPoolSpellCost();
        mana.castManaPoolSpell();
        assertEquals(initialMana - initialSpellCost, mana.getCurrentMana(), "Mana should decrease by the spell cost after casting ManaPoolSpell");
    }

    // Test game over state after mana is depleted
    @Test
    void testGameOver() {
        mana.decrease(200);
        assertTrue(mana.isGameOver(), "Game should be over if mana becomes zero or negative");
    }

    // Test the effects of multiple ManaPoolSpell casts (Boundary test)
    @Test
    void testMultipleCastsOfManaPoolSpell() {
        int timesToCast = 10;
        for (int i = 0; i < timesToCast; i++) {
            if (mana.canCastManaPoolSpell()) {
                mana.castManaPoolSpell();
            } else {
                break; // If not enough mana to cast, break out of the loop
            }
        }
        assertTrue(mana.getManaPoolSpellCost() > 100, "ManaPoolSpell cost should have increased after multiple casts");
        assertTrue(mana.getManaCap() > 200, "Mana cap should have increased after multiple casts of ManaPoolSpell");
    }
}
