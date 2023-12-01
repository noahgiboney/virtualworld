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

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = world.findNearest(getPosition(), Stump.class);
        Optional<Entity> bloodTarget = world.findNearest(getPosition(), Blood.class);
        Optional<Entity> spiderTarget = world.findNearest(getPosition(), Spider.class);

        if(spiderTarget.isPresent()){

            if(moveTo(world, spiderTarget.get(), scheduler)){
                if(spiderTarget.get() instanceof Spider temp){
                    Point spiderPos = temp.getPosition();
                    world.removeEntityAt(spiderPos);

                    Web web = new Web(Web.WEB_KEY, spiderPos, imageStore.getImageList(Web.WEB_KEY), 0.5);
                    world.addEntity(web);
                    web.ScheduleActions(scheduler,world,imageStore);
                }
            }
        }
        else if (bloodTarget.isPresent()) {

            if (moveTo(world, bloodTarget.get(), scheduler)) {
                if (bloodTarget.get() instanceof Blood temp) {
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


//        if (isStuck) {
//            if (System.currentTimeMillis() - stuckTime < 1000) {
//                return false;
//            } else {
//                isStuck = false;
//            }
//        }
//
//        Optional<Entity> web = world.findNearest(getPosition(), Web.class);
//        if (web.isPresent() && web.get() instanceof Web temp) {
//            if (getPosition().adjacent(temp.getPosition())) {
//                isStuck = true;
//                stuckTime = System.currentTimeMillis();
////                world.moveEntity(scheduler, this, new Point(getPosition().getX() - 1, getPosition().getY()));
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
