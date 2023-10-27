package WizardTD;

import processing.core.PApplet;

import java.util.ArrayList;

public class Trap {
    PApplet p;
    private final Board board;
    private final int xPosition;
    private final int yPosition;

    private boolean triggered = false;


    public Trap(int tileX, int tileY, PApplet pApplet, Board board) {

        this.p = pApplet;
        this.board = board;
        this.xPosition = tileX;
        this.yPosition = tileY;



    }

    public void display() {
        updateTrapAppearance();

    }

    public void checkAndDamageMonsters(ArrayList<Monster> monsters) {
        for (Monster monster : monsters) {
            if(monster.isAlive()) {
                float distance = PApplet.dist(getCenterX(), getCenterY(), monster.currentX, monster.currentY);
                if (!triggered && distance <= 32 /*assuming 32 is the size of a tile*/) {
                    monster.setDamage(5); // assuming Monster class has a takeDamage method
                    triggered = true;
                } else if (distance > 32) {
                    triggered = false; // Reset the trap if the monster is out of range
                }
            }
        }
    }

    public int getCenterX() {
        return xPosition * 32 + 16;
    }

    public int getCenterY() {
        return yPosition * 32 + 56;
    }

    private void updateTrapAppearance() {
        board.setTile(xPosition, yPosition, Board.Tile.TRAP);
        p.fill(255, 102, 102);

        p.rect(xPosition * 32, yPosition * 32 + 40, 30, 30);

        p.strokeWeight(1);
        p.stroke(0);
        p.fill(255);

    }
}