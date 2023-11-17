import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Fairy extends ActivityEntity implements MoveTo{

    public static final String FAIRY_KEY = "fairy";
    public static final int FAIRY_ANIMATION_PERIOD = 0;
    public static final int FAIRY_ACTION_PERIOD = 1;

   //private static final PathingStrategy FAIRY_PATHING = new AStarPathingStrategy();
    private static final PathingStrategy FAIRY_PATHING = new SingleStepPathingStrategy();

    public Fairy(String id, Point position, List<PImage> images, double animationPeriod , double actionPeriod){
        super(id, position, images, animationPeriod, actionPeriod);
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = world.findNearest(getPosition(), Stump.class);

        if (fairyTarget.isPresent()) {
           //System.out.println("Target found: " + fairyTarget.isPresent());
            Point tgtPos = fairyTarget.get().getPosition();

            if (moveTo(world, fairyTarget.get(), scheduler)) {
                //System.out.println("Moved to target: " + moveTo(world, fairyTarget.get(), scheduler));
                Sapling sapling = new Sapling(Sapling.SAPLING_KEY + "_" + fairyTarget.get().getId(), tgtPos, imageStore.getImageList(Sapling.SAPLING_KEY),
                        Sapling.SAPLING_ACTION_ANIMATION_PERIOD, Sapling.SAPLING_ACTION_ANIMATION_PERIOD, 0, Sapling.SAPLING_HEALTH_LIMIT);

                world.addEntity(sapling);
                sapling.ScheduleActions(scheduler, world, imageStore);
            }
        }
        // If no stump was found or the Fairy hasn't reached it yet, just reschedule next action
        scheduler.scheduleEvent(this, new ActionActivity(this, world, imageStore), getActionPeriod());
    }

    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        System.out.println("Current position: " + getPosition() + ", Target position: " + target.getPosition());
        if (getPosition().adjacent(target.getPosition())) {
            world.removeEntity(scheduler,target);
            return true;
        } else {
            Point nextPos = nextPosition(world, target.getPosition());
            System.out.println("Next position: " + nextPos);

            if (!getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
                System.out.println("Moved to: " + nextPos + ", New position: " + getPosition());
            }
            return false;
        }
    }

    @Override
    public Point nextPosition(WorldModel world, Point destPos) {

        int horiz = Integer.signum(destPos.getX() - getPosition().getX());
        Point newPos = new Point(getPosition().getX() + horiz, getPosition().getY());

        if (horiz == 0 || world.isOccupied(newPos)) {
            int vert = Integer.signum(destPos.getY() - getPosition().getY());
            newPos = new Point(getPosition().getX(), getPosition().getY() + vert);

            if (vert == 0 || world.isOccupied(newPos)) {
                newPos = getPosition();
            }
        }
        return newPos;

//        if (getPosition().adjacent(destPos)) {
//            return getPosition(); // Return the current position if the destination is reached
//        }

//        List<Point> path = FAIRY_PATHING.computePath(getPosition(),
//                destPos,
//                point -> world.withinBounds(point) && !world.isOccupied(point),
//                Point::adjacent,
//                PathingStrategy.CARDINAL_NEIGHBORS);
//
//        if (path.size() == 0)
//            return this.getPosition();
//        else {
//            return path.get(0);
//        }
    }
}
