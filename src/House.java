import processing.core.PImage;

import java.util.List;

public class House extends Entity {
    public static final String HOUSE_KEY = "house";

    public House(String id, Point position, List<PImage> images){
        super(id,position,images);
    }

    @Override
    public void ScheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {

    }

    @Override
    public String getKey() {
        return HOUSE_KEY;
    }
}
