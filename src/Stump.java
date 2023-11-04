import processing.core.PImage;

import java.util.List;

public class Stump extends Entity {
    public static final String STUMP_KEY = "stump";

    public Stump(String id, Point position, List<PImage> images){
        super(id,position,images);
    }

    @Override
    public void ScheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
    }

    @Override
    public String getKey() {
        return STUMP_KEY;
    }
}
