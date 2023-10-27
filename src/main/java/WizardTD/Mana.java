package WizardTD;

import processing.data.JSONObject;
import processing.core.PApplet;

public class Mana {

    private float currentMana;
    private float manaCap;
    private final float manaGainedPerSecond;

    private float manaPoolSpellCost;
    private final float manaPoolSpellCostIncrease;
    private final float manaPoolSpellCapMultiplier;
    private final float manaPoolSpellManaGainedMultiplier;

    private int timesSpellCasted;
    private boolean gameOver;


    public Mana(String configPath, PApplet pApplet) {
        JSONObject config = pApplet.loadJSONObject(configPath);
        this.gameOver = false;

        this.currentMana = config.getFloat("initial_mana");
        this.manaCap = config.getFloat("initial_mana_cap");
        this.manaGainedPerSecond = config.getFloat("initial_mana_gained_per_second");

        this.manaPoolSpellCost = config.getFloat("mana_pool_spell_initial_cost");
        this.manaPoolSpellCostIncrease = config.getFloat("mana_pool_spell_cost_increase_per_use");
        this.manaPoolSpellCapMultiplier = config.getFloat("mana_pool_spell_cap_multiplier");
        this.manaPoolSpellManaGainedMultiplier = config.getFloat("mana_pool_spell_mana_gained_multiplier");

        this.timesSpellCasted = 0;
    }

    public void update(float deltaTime) {
        // Increase current mana by the mana gained per second, considering multiplier
        currentMana += manaGainedPerSecond * (1 + timesSpellCasted * manaPoolSpellManaGainedMultiplier) * deltaTime;

        // Ensure mana does not exceed the cap
        if (currentMana > manaCap) {
            currentMana = manaCap;
        }
    }

    public void increase(int amount) {
        currentMana += amount;
        // 确保currentMana不超过maxMana
        if (currentMana > manaCap) {
            currentMana = manaCap;
        }
    }

    public void decrease(int amount) {
        currentMana -= amount;
        if (currentMana <= 0) {
            currentMana = 0;
            gameOver = true;
        }
    }

    public boolean canCastManaPoolSpell() {
        return currentMana >= manaPoolSpellCost;
    }

    public void castManaPoolSpell() {
        if (canCastManaPoolSpell()) {
            currentMana -= manaPoolSpellCost;

            // Increase the cost of the spell for the next time
            manaPoolSpellCost += manaPoolSpellCostIncrease;

            // Increase the mana cap
            manaCap *= manaPoolSpellCapMultiplier;

            // Increase the times the spell was casted
            timesSpellCasted++;
        }
    }

    public float getCurrentMana() {
        return currentMana;
    }

    public float getManaCap() {
        return manaCap;
    }

    public float getManaPoolSpellCost() {
        return manaPoolSpellCost;
    }

    public boolean isGameOver() {
        return gameOver;
    }

}
