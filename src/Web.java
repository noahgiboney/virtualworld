import processing.core.PImage;

import java.util.List;

public class Web extends AnimationEntity {
    public static final String WEB_KEY = "web";

    public static final int WEB_ANIMATION_PERIOD = 0;

    public Web(String id, Point position, List<PImage> images, double animationPeriod){
        super(id,position,images, animationPeriod);
    }

    @Override
    public void ScheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this,new ActionAnimation(this, 0), getAnimationPeriod());
    }
}