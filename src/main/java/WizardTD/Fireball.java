package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Fireball {
    final PVector position;
    private final Monster target;
    private final float damage;
    boolean isActive = true;
    private PImage fireballImage;
    public Fireball(float x, float y, float damage, Monster target,PApplet pApplet) {
        this.position = new PVector(x, y);
        this.target = target;
        this.damage = damage;
        loadFireballImage(pApplet);
    }

    private void loadFireballImage(PApplet pApplet) {
        fireballImage = pApplet.loadImage("./src//main//resources//WizardTD//fireball.png");
    }
    public void update() {
        if (!isActive) return;

        PVector dir = new PVector(target.currentX - position.x, target.currentY - position.y);
        dir.normalize();
        float speed = 5;
        position.add(dir.mult(speed));

        if (PApplet.dist(position.x, position.y, target.currentX, target.currentY) < 5) {
            target.setDamage(calculateDamage(target));
            isActive = false;
        }

        // remove fireball if it's out of screen or other conditions
        if (isOutOfBounds()) {
            isActive = false;
        }
    }

    public void display(PApplet pApplet) {
        if (!isActive) return;

        pApplet.fill(255, 0, 0);
        pApplet.image(fireballImage, position.x, position.y);
    }

    int calculateDamage(Monster monster) {
        // damage reduced by monster armor
        return (int)(damage * (1 - monster.armour / 100));
    }

    boolean isOutOfBounds() {
        return position.x < 0 || position.x > 760 || position.y < 0 || position.y > 680;
    }

}
