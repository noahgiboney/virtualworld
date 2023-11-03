import processing.core.PImage;

import java.util.List;

public abstract class HealthEntity extends ActionEntity{
    private int health;

    public HealthEntity (String id, Point position, List<PImage> images, double animationPeriod , double actionperiod, int health) {
        super(id, position, images, animationPeriod, actionperiod);
        this.health = health;
    }

    public int getHealth() {
        return this.health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
