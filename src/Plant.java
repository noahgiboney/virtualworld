import processing.core.PImage;

import java.util.List;

public abstract class Plant extends ActivityEntity implements Transform{
    private int health;

    public Plant(String id, Point position, List<PImage> images, double animationPeriod , double actionPeriod, int health){
        super(id, position, images, animationPeriod, actionPeriod);
        this.health = health;
    }

    @Override
    public void ScheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, new ActionActivity(this, world, imageStore), getActionPeriod());
        scheduler.scheduleEvent(this, new ActionAnimation(this, 0), getAnimationPeriod());
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
