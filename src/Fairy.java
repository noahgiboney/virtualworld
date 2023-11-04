import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Fairy extends ActionEntity implements MoveTo{

    public static final String FAIRY_KEY = "fairy";
    public static final int FAIRY_ANIMATION_PERIOD = 0;
    public static final int FAIRY_ACTION_PERIOD = 1;
    public static final int FAIRY_NUM_PROPERTIES = 2;

    public Fairy(String id, Point position, List<PImage> images, double animationPeriod , double actionPeriod){
        super(id, position, images, animationPeriod, actionPeriod);
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = world.findNearest(getPosition(), Stump.STUMP_KEY);

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().getPosition();

            if (moveTo( world, fairyTarget.get(), scheduler)) {

                Sapling sapling = new Sapling(Sapling.SAPLING_KEY + "_" + fairyTarget.get().getId(), tgtPos,
                        imageStore.getImageList(Sapling.SAPLING_KEY), Sapling.SAPLING_ACTION_ANIMATION_PERIOD , Sapling.SAPLING_ACTION_ANIMATION_PERIOD , 0, Sapling.SAPLING_HEALTH_LIMIT);
                world.addEntity(sapling);
                sapling.ScheduleActions(scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this, new ActionActivity(this, world, imageStore), getActionPeriod());
    }

    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (getPosition().adjacent(target.getPosition())) {
            world.removeEntity( scheduler,target);
            return true;
        } else {
            Point nextPos = nextPosition(world, target.getPosition());

            if (!getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, target, nextPos);
            }
            return false;
        }
    }

    @Override
    public void ScheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, new ActionActivity(this, world, imageStore), getActionPeriod());
        scheduler.scheduleEvent(this, new ActionAnimation(this, 0), getAnimationPeriod());
    }

    @Override
    public String getKey() {
        return FAIRY_KEY;
    }
}
