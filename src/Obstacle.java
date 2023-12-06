import processing.core.PImage;

import java.util.List;

public class Obstacle extends AnimationEntity {
    public static final String OBSTACLE_KEY = "obstacle";
    public static final String LAVA_KEY = "lava";
    public static final int OBSTACLE_ANIMATION_PERIOD = 0;
    public Obstacle(String id, Point position, List<PImage> images, double animationPeriod){
        super(id,position,images, animationPeriod);
    }

    @Override
    public void ScheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this,new ActionAnimation(this, 0), getAnimationPeriod());
    }
}
