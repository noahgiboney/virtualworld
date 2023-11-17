import processing.core.PImage;

import java.util.List;

public abstract class ActivityEntity extends AnimationEntity{
    private final double actionPeriod;

    public ActivityEntity(String id, Point position, List<PImage> images, double animationPeriod , double actionPeriod) {
        super(id, position, images, animationPeriod);
        this.actionPeriod = actionPeriod;
    }

    public abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

    @Override
    public void ScheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, new ActionActivity(this, world, imageStore), getActionPeriod());
        scheduler.scheduleEvent(this, new ActionAnimation(this, 0), getAnimationPeriod());
    }

    public double getActionPeriod() {
        return this.actionPeriod;
    }
}
