import processing.core.PImage;

import java.util.List;

public abstract class ActivityEntity extends AnimationEntity{
    private double actionPeriod;

    public ActivityEntity(String id, Point position, List<PImage> images, double animationPeriod , double actionPeriod) {
        super(id, position, images, animationPeriod);
        this.actionPeriod = actionPeriod;
    }

    public abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

    public double getActionPeriod() {
        return this.actionPeriod;
    }
}
