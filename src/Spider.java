import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Spider extends Movable {

    public static final String SPIDER_KEY = "spider";
    public static final int SPIDER_ANIMATION_PERIOD = 0;
    public static final int SPIDER_ACTION_PERIOD = 1;
    private static final PathingStrategy SPIDER_SINGLE_STEP = new SingleStepPathingStrategy();
    private static final PathingStrategy SPIDER_A_STAR = new AStarPathingStrategy();
    private boolean canMove;

    public Spider(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, boolean canMove) {
        super(id, position, images, animationPeriod, actionPeriod, SPIDER_A_STAR);
        this.canMove = canMove;
    }

    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if(!canMove){
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

    public boolean isCanMove() {
        return canMove;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }
}
