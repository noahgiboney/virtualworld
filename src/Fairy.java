import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Fairy extends Movable {

    public static final String FAIRY_KEY = "fairy";
    public static final int FAIRY_ANIMATION_PERIOD = 0;
    public static final int FAIRY_ACTION_PERIOD = 1;
    private static final PathingStrategy FAIRY_SINGLE_STEP = new SingleStepPathingStrategy();
    private static final PathingStrategy FAIRY_A_STAR = new AStarPathingStrategy();

    private boolean isStuck = false;
    private long stuckTime = 0;

    public Fairy(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod) {
        super(id, position, images, animationPeriod, actionPeriod, FAIRY_A_STAR);
    }



//     if (targetTree.isPresent() && targetSapling.isPresent()) {
//        int distanceToTree = getPosition().distanceSquared(targetTree.get().getPosition());
//        int distanceToSapling = getPosition().distanceSquared(targetSapling.get().getPosition());
//        target = (distanceToTree <= distanceToSapling) ? targetTree : targetSapling;
//    } else if (targetTree.isPresent()) {
//        target = targetTree;
//    } else if (targetSapling.isPresent()) {
//        target = targetSapling;
//    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = findTarget(world);

        if (target.isPresent()) {
            if (moveTo(world, target.get(), scheduler)) {
                processTarget(world, target.get(), imageStore, scheduler);
            }
        }

        scheduler.scheduleEvent(this, new ActionActivity(this, world, imageStore), getActionPeriod());
    }

    private Optional<Entity> findTarget(WorldModel world) {
        Optional<Entity> bloodTarget = world.findNearest(getPosition(), Blood.class);
        if (bloodTarget.isPresent()) {
            return bloodTarget;
        }

        Optional<Entity> spiderTarget = world.findNearest(getPosition(), Spider.class);
        if (spiderTarget.isPresent()) {
            return spiderTarget;
        }

        return world.findNearest(getPosition(), Stump.class);
    }

    private void processTarget(WorldModel world, Entity target, ImageStore imageStore, EventScheduler scheduler) {
        Point targetPosition = target.getPosition();
        world.removeEntityAt(targetPosition);

        if (target instanceof Blood && !world.isOccupied(targetPosition)) {
            createDudeNotFullAt(world, targetPosition, imageStore, scheduler);
        } else if (target instanceof Spider) {
            // Additional logic for Spider
        } else if (target instanceof Stump) {
            createSaplingAt(world, target, imageStore, scheduler);
        }
    }

    private void createDudeNotFullAt(WorldModel world, Point position, ImageStore imageStore, EventScheduler scheduler) {

        Optional<Entity> house = world.findNearest(position, House.class);
        if(house.isPresent()){
            Point housePoint = new Point(house.get().getPosition().getX(), house.get().getPosition().getY() - 1);

            DudeNotFull dude = new DudeNotFull(Dude.DUDE_KEY, housePoint, imageStore.getImageList(Dude.DUDE_KEY), 0.180, 0.720, 4);
            world.addEntity(dude);
            dude.ScheduleActions(scheduler, world, imageStore);
        }
    }

    private void createSaplingAt(WorldModel world, Entity target, ImageStore imageStore, EventScheduler scheduler) {
        Sapling sapling = new Sapling(Sapling.SAPLING_KEY + "_" + target.getId(), target.getPosition(), imageStore.getImageList(Sapling.SAPLING_KEY),
                Sapling.SAPLING_ACTION_ANIMATION_PERIOD, Sapling.SAPLING_ACTION_ANIMATION_PERIOD, 0, Sapling.SAPLING_HEALTH_LIMIT);
        world.addEntity(sapling);
        sapling.ScheduleActions(scheduler, world, imageStore);
    }

    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {

//        Optional<Entity> web = world.findNearest(getPosition(), Web.class);
//        if (web.isPresent() && web.get() instanceof Web temp) {
//            if (getPosition().adjacent(temp.getPosition())) {
//                isStuck = true;
//                stuckTime = System.currentTimeMillis();
//                world.moveEntity(scheduler, this, new Point(getPosition().getX() - 1, getPosition().getY()));
//                return false;
//            }
//        }

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
