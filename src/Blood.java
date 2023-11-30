import processing.core.PImage;

import java.util.List;

public class Blood extends AnimationEntity {
    public static final String BLOOD_KEY = "blood";

    public static final int BLOOD_ANIMATION_PERIOD = 0;

    public Blood(String id, Point position, List<PImage> images, double animationPeriod){
        super(id,position,images, animationPeriod);
    }

    @Override
    public void ScheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this,new ActionAnimation(this, 0), getAnimationPeriod());
    }
}