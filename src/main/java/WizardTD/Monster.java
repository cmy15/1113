package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.List;

public class Monster {

    private final Mana mana;
    public String type;
    public int maxHp;
    public int currentHp;
    public float speed;
    public float armour;
    public int manaGainedOnKill;
    public PImage sprite;
    public List<int[]> path;
    public int currentPathIndex;
    public float currentX;
    public float currentY;
    public float spawnTime;
    public boolean isAlive;
    private final PImage[] deathAnimation;
    private int deathFrameIndex = 0;
    private boolean isDying = false;

    public Monster(String type, int hp, float speed, float armour, int manaOnKill, List<int[]> path, PImage sprite, float spawnTime, PImage[] deathAnimation, Mana mana) {
        this.mana = mana;
        this.type = type;
        this.maxHp = hp;
        this.currentHp = hp;
        this.speed = speed;
        this.armour = armour;
        this.manaGainedOnKill = manaOnKill;
        this.path = path;
        this.sprite = sprite;
        this.currentPathIndex = 0;
        this.currentX = path.get(0)[1] * 32 + 5;
        this.currentY = path.get(0)[0] * 32 + 40;
        this.spawnTime = spawnTime;
        this.isAlive = true;
        this.deathAnimation = deathAnimation;




    }

    public int move() {
        if (currentPathIndex < path.size() - 1) {
            int[] nextPos = path.get(currentPathIndex + 1);
            float actualNextX = nextPos[1] * 32 + 5;
            float actualNextY = nextPos[0] * 32 + 40;


            float dx = actualNextX - currentX;
            float dy = actualNextY - currentY;
            float distance = PApplet.dist(currentX, currentY, actualNextX, actualNextY);

            float moveDistance = speed;

            // ++ currentPathIndex
            if (moveDistance > distance) {
                currentPathIndex++;
                currentX = actualNextX;
                currentY = actualNextY;
            } else {
                currentX += (dx / distance) * moveDistance;
                currentY += (dy / distance) * moveDistance;
            }
        } else {

            isAlive = false;
            return banish();


        }
        return 0;
    }

    public void display(PApplet pApplet) {
        if (isAlive || isDying) {
            if (isDying) {
                displayDeathAnimation(pApplet);
                return;
            } else {
                pApplet.image(sprite, currentX, currentY);
            }

            float barWidth = sprite.width;
            float barHeight = 8;
            float barX = currentX;
            float barY = currentY - barHeight - 5;
            pApplet.fill(255, 0, 0);
            pApplet.rect(barX, barY, barWidth, barHeight);

            float healthRatio = (float) currentHp / maxHp;
            float greenBarWidth = healthRatio * barWidth;
            pApplet.fill(0, 255, 0);
            pApplet.rect(barX, barY, greenBarWidth, barHeight);
        }


    }

    public void displayDeathAnimation(PApplet pApplet) {
        if (isDying) {
            if (deathFrameIndex == deathAnimation.length) {
                isDying = false;
                isAlive = false;
                mana.increase(manaGainedOnKill);
                return;
            }
            pApplet.image(deathAnimation[deathFrameIndex], currentX, currentY);
            deathFrameIndex+=1;
        }
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setDamage(int damage){
        currentHp -= damage;
        if(currentHp <= 0 && !isDying)
        {
            isDying = true;
        }
    }

    public void respawn() {
        currentPathIndex = 0;
        currentX = path.get(0)[1] * 32 + 5;
        currentY = path.get(0)[0] * 32 + 40;
        isAlive = true;
    }

    public int banish() {
        int manaCost = currentHp;
        respawn();  // reset monster position
        return manaCost;
    }

    public int getcurrentHp() {
        return currentHp;
    }
}