import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Fairy extends Movable {

    public static final String FAIRY_KEY = "fairy";
    public static final int FAIRY_ANIMATION_PERIOD = 0;
    public static final int FAIRY_ACTION_PERIOD = 1;
    private static final PathingStrategy FAIRY_SINGLE_STEP = new SingleStepPathingStrategy();
    private static final PathingStrategy FAIRY_A_STAR = new AStarPathingStrategy();

    public Fairy(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod) {
        super(id, position, images, animationPeriod, actionPeriod, FAIRY_A_STAR);
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = world.findNearest(getPosition(), Stump.class);
        Optional<Entity> bloodTarget = world.findNearest(getPosition(), Blood.class);

        if (bloodTarget.isPresent()){

            if(moveTo(world, bloodTarget.get(), scheduler)){
                Entity blood = bloodTarget.get();
                if(blood instanceof Blood temp) {
                    Point bloodPosition = temp.getPosition();
                    world.removeEntityAt(bloodPosition);

                    DudeNotFull dude = new DudeNotFull(Dude.DUDE_KEY, new Point(getPosition().getX() - 1, getPosition().getY()), imageStore.getImageList(Dude.DUDE_KEY), 0.180,
                            0.720, 4);
                    world.addEntity(dude);
                    dude.ScheduleActions(scheduler, world, imageStore);
                }
            }
        }
        else if (fairyTarget.isPresent()) {

            Point tgtPos = fairyTarget.get().getPosition();

            if (moveTo(world, fairyTarget.get(), scheduler)) {
                Sapling sapling = new Sapling(Sapling.SAPLING_KEY + "_" + fairyTarget.get().getId(), tgtPos, imageStore.getImageList(Sapling.SAPLING_KEY),
                        Sapling.SAPLING_ACTION_ANIMATION_PERIOD, Sapling.SAPLING_ACTION_ANIMATION_PERIOD, 0, Sapling.SAPLING_HEALTH_LIMIT);

                world.addEntity(sapling);
                sapling.ScheduleActions(scheduler, world, imageStore);
            }
        }
        scheduler.scheduleEvent(this, new ActionActivity(this, world, imageStore), getActionPeriod());
    }

    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (getPosition().adjacent(target.getPosition())) {
            world.removeEntity(scheduler, target);
            return true;
        } else {
            Point nextPos = nextPosition( world, target.getPosition());

            if (!getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            } else {
            }
            return false;
        }
    }
}
