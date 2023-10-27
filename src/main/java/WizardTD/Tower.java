package WizardTD;

import processing.core.PApplet;
import processing.data.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tower {
    PApplet p;
    private final Board board;
    private final Mana mana;
    ArrayList<Fireball> fireballs = new ArrayList<>();
    private int manaCost;
    private double range;
    private float lastShootTime;
    private double firingSpeed;
    private float damage;

    private final int xPosition;  // 塔的x坐标
    private final int yPosition;  // 塔的y坐标

    private int rangeLevel;
    private int speedLevel;
    private int damageLevel;
    private int towerLevel = 0;
    private static final int MAX_UPGRADE_LEVEL = 3;


    public Tower(int tileX, int tileY, PApplet pApplet, int rangeLevel, int speedLevel, int damageLevel, String configPath, Board board, Mana mana) {

        this.p = pApplet;
        this.board = board;
        this.xPosition = tileX;
        this.yPosition = tileY;
        this.mana = mana;

        // config
        JSONObject config = loadConfig(pApplet, configPath);
        this.manaCost = config.getInt("tower_cost");
        this.range = config.getFloat("initial_tower_range");
        this.firingSpeed = config.getFloat("initial_tower_firing_speed");
        this.damage = config.getInt("initial_tower_damage");
        float iniDamage = damage;



        this.rangeLevel = rangeLevel;
        this.speedLevel = speedLevel;
        this.damageLevel = damageLevel;
        towerLevel = getTowerLevel();

        this.range += 32 * (rangeLevel);
        this.firingSpeed += 0.5 * (speedLevel);
        this.damage += (float) ((0.5 * iniDamage) * damageLevel);





    }

    public void display() {
        updateTowerAppearance();

    }

    public void shoot(ArrayList<Monster> monsters) {
        float currentTime = p.millis();
        Monster targetMonster = findTarget(monsters);
        if (targetMonster != null) {
            if(currentTime >= (lastShootTime + ((1 / firingSpeed) * 1000))){
                Fireball newFireball = new Fireball(getCenterX(), getCenterY(), damage, targetMonster, p);
                fireballs.add(newFireball);
                lastShootTime = currentTime;
            }
        }
    }

    private Monster findTarget(ArrayList<Monster> monsters) {
        for (Monster monster : monsters) {
            if(monster.isAlive()) {
                float distance = PApplet.dist(getCenterX(), getCenterY(), monster.currentX, monster.currentY);
                if (distance <= range) {
                    return monster;
                }
            }
            else {
                monsters.remove(monster);
                break;
            }
        }
        return null;  // No monster in range
    }

    public void upgradeRange() {
        if (rangeLevel < MAX_UPGRADE_LEVEL) {
            rangeLevel++;
            range += 32;
            manaCost += 20 + 10 * (rangeLevel - 1);
        }
    }

    public void upgradeSpeed() {
        if (speedLevel < MAX_UPGRADE_LEVEL) {
            speedLevel++;
            firingSpeed += 0.5;
            manaCost += 20 + 10 * (speedLevel - 1);
        }
    }

    public void upgradeDamage() {
        if (damageLevel < MAX_UPGRADE_LEVEL) {
            damageLevel++;
            damage += damage / 2;
            manaCost += 20 + 10 * (damageLevel - 1);
        }
    }

    public int getUpgradeCost(int level) {
        return 20 + 10 * (level - 1);
    }

    public double getRange() {
        return range;
    }

    public int getCenterX() {
        return xPosition * 32 + 16;
    }

    public int getCenterY() {
        return yPosition * 32 + 56;
    }
    public int getTileX() {
        return xPosition;
    }
    public int getTileY() {
        return yPosition;
    }

    public int getTowerLevel() {
        if (rangeLevel >= 2 && speedLevel >= 2 && damageLevel >= 2) {
            return 2;
        } else if (rangeLevel >= 1 && speedLevel >= 1 && damageLevel >= 1) {
            return 1;
        }
        return 0;
    }

    private void updateTowerAppearance() {
        if (towerLevel >= 1) {
            if (towerLevel == 1) {
                board.setTile(xPosition, yPosition, Board.Tile.TOWER1);
            } else if (towerLevel == 2) {
                board.setTile(xPosition, yPosition, Board.Tile.TOWER2);
            }
        } else {
            board.setTile(xPosition, yPosition, Board.Tile.TOWER);
        }

        if (towerLevel >= 1){
            p.fill(128, 0, 128);
            p.textSize(12);
            for (int i = 0; i < rangeLevel - towerLevel; i++) {

                p.text('o', (getTileX() * 32 + (i + 1) * 7) - 5, getTileY() * 32 + 40);
            }
            for (int i = 0; i < speedLevel - towerLevel; i++){
                p.stroke(135, 206, 235);
                p.strokeWeight(2 * speedLevel);
                p.noFill();
                p.rect(getTileX() * 32, getTileY() * 32 + 40, 30, 30);
                p.strokeWeight(1);
                p.stroke(0);
                p.fill(255);

            }
            for (int i = 0; i < damageLevel - towerLevel; i++) {
                p.text('X', getTileX() * 32 + i * 7, (getTileY() + 1) * 32 + 40);
            }
        }
        else {
            p.fill(128, 0, 128);
            p.textSize(12);
            if (rangeLevel > 0) {
                for (int i = 0; i < rangeLevel; i++) {
                    p.text('o', (getTileX() * 32 + (i + 1) * 7) - 5, getTileY() * 32 + 40);

                }
            }
            if (speedLevel > 0) {
                p.stroke(135, 206, 235);
                p.strokeWeight(2 * speedLevel);
                p.noFill();
                p.rect(getTileX() * 32, getTileY() * 32 + 40, 30, 30);
                p.strokeWeight(1);
                p.stroke(0);
                p.fill(255);

            }
            if (damageLevel > 0) {
                for (int i = 0; i < damageLevel; i++) {
                    p.text('X', getTileX() * 32 + i * 7, (getTileY() + 1) * 32 + 40);

                }
            }

        }

    }

    public int getCost() {
        int baseCost = manaCost;  // basic cost of the tower

        int rangeUpgradeCost = rangeLevel * 20;
        int speedUpgradeCost = speedLevel * 20;
        int damageUpgradeCost = damageLevel * 20;

        return baseCost + rangeUpgradeCost + speedUpgradeCost + damageUpgradeCost;
    }

    public List<Integer> handleUpgrade(int[] upgrades) {
        List<Integer> results = new ArrayList<>();
        List<Integer> upgradeCosts = getUpgradeCosts();


        if (upgrades[0] == 1 && rangeLevel < MAX_UPGRADE_LEVEL && mana.getCurrentMana() >= upgradeCosts.get(0)) {
            upgradeRange();
            mana.decrease(upgradeCosts.get(0));
            results.add(upgradeCosts.get(0));
        } else {
            results.add(0);
        }

        if (upgrades[1] == 1 && speedLevel < MAX_UPGRADE_LEVEL && mana.getCurrentMana() >= upgradeCosts.get(1)) {
            upgradeSpeed();
            mana.decrease(upgradeCosts.get(1));
            results.add(upgradeCosts.get(1));
        } else {
            results.add(0);
        }

        if (upgrades[2] == 1 && damageLevel < MAX_UPGRADE_LEVEL && mana.getCurrentMana() >= upgradeCosts.get(2)) {
            upgradeDamage();
            mana.decrease(upgradeCosts.get(2));
            results.add(upgradeCosts.get(2));
        } else {
            results.add(0);
        }

        towerLevel = getTowerLevel();
        updateTowerAppearance();
        return results;
    }

    public List<Integer> getUpgradeCosts() {
        // Calculate the upgrade costs for range, speed, and damage respectively.
        int rangeUpgradeCost = getUpgradeCost(rangeLevel + 1);
        int speedUpgradeCost = getUpgradeCost(speedLevel + 1);
        int damageUpgradeCost = getUpgradeCost(damageLevel + 1);

        // Return the costs as a list.
        return Arrays.asList(rangeUpgradeCost, speedUpgradeCost, damageUpgradeCost);
    }

    private static JSONObject loadConfig(PApplet pApplet, String configPath) {
        return pApplet.loadJSONObject(configPath);
    }
}