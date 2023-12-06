import processing.core.PImage;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Point volcanoPoint = Volcano.this.getPosition();
                world.removeEntity(scheduler, Volcano.this);

                Volcano entity = new Volcano(VOLCANO_KEY, volcanoPoint, imageStore.getImageList(Volcano.VOLCANO_KEY),
                       0.040, false);
                world.tryAddEntity(entity);

            }
        }, 2000);
    }

    public void setErupted(boolean erupted) {
        this.erupted = erupted;
    }

    public boolean isErupted() {
        return erupted;
    }
}
