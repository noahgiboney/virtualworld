import processing.core.PImage;

import java.util.List;

public abstract class Plant extends ActivityEntity implements Transform{
    private int health;

    public Plant(String id, Point position, List<PImage> images, double animationPeriod , double actionPeriod, int health){
        super(id, position, images, animationPeriod, actionPeriod);
        this.health = health;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
