import processing.core.PImage;

import java.util.List;

public class Volcano extends AnimationEntity {
    public static final String VOLCANO_KEY = "volcano";
    public static final int VOLCANO_ANIMATION_PERIOD = 0;
    private boolean erupted;

    public Volcano(String id, Point position, List<PImage> images, double animationPeriod, boolean erupted){
        super(id,position,images, animationPeriod);
        this.erupted  = erupted;
    }

    @Override
    public void ScheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        if(erupted){
            scheduler.scheduleEvent(this,new ActionAnimation(this, 0), getAnimationPeriod());
        }

    }

    public void setErupted(boolean erupted) {
        this.erupted = erupted;
    }
}
