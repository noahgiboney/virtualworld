import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class BigSpider extends Spider {

    public static final String BIG_SPIDER_KEY = "big_spider";
    public static final int BIG_SPIDER_ANIMATION_PERIOD = 0;
    public static final int BIG_SPIDER_ACTION_PERIOD = 1;

    public BigSpider(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, boolean canMove) {
        super(id, position, images, animationPeriod, actionPeriod, canMove);
    }

    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if(!isCanMove()){
            return false;
        }

        if (getPosition().adjacent(target.getPosition())) {
            return true;
        } else {
            Point nextPos = nextPosition( world, target.getPosition());

            if (!getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> dudeTarget = world.findNearest(getPosition(), Dude.class);

        if(dudeTarget.isPresent()){

            if(moveTo(world, dudeTarget.get(), scheduler)){
                Entity dude = dudeTarget.get();

                if (dude instanceof Dude temp){

                    Point dudePoint = temp.getPosition();
                    world.removeEntityAt(dudePoint);

                    Blood blood = new Blood(Blood.BLOOD_KEY, dudePoint, imageStore.getImageList(Blood.BLOOD_KEY), 0.1);
                    world.addEntity(blood);
                    blood.ScheduleActions(scheduler, world, imageStore);

                }
            }

        }
        scheduler.scheduleEvent(this, new ActionActivity(this, world, imageStore), getActionPeriod());
    }
}
