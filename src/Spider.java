import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Spider extends Movable {

    public static final String SPIDER_KEY = "spider";
    public static final int SPIDER_ANIMATION_PERIOD = 0;
    public static final int SPIDER_ACTION_PERIOD = 1;
    private static final PathingStrategy SPIDER_SINGLE_STEP = new SingleStepPathingStrategy();
    private static final PathingStrategy SPIDER_A_STAR = new AStarPathingStrategy();

    public Spider(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod) {
        super(id, position, images, animationPeriod, actionPeriod, SPIDER_A_STAR);
    }

    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {

        if (getPosition().adjacent(target.getPosition())) {
            return true;
        } else {
            Point nextPos = nextPosition(world, target.getPosition());

            if (!getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> dudeTarget = world.findNearest(getPosition(), Dude.class);

        if (dudeTarget.isPresent()) {
            if (moveTo(world, dudeTarget.get(), scheduler)) {
                if (dudeTarget.get() instanceof Dude dude) {
                    //decrease the health of the dude to zero (kill him)
                    dude.setHealth(dude.getHealth() - 1);
                }
            }
        }
        scheduler.scheduleEvent(this, new ActionActivity(this, world, imageStore), getActionPeriod());
    }
}
