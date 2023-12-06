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
        //all possible targets of the fairy
        Optional<Entity> stumpTarget = world.findNearest(getPosition(), Stump.class);
        Optional<Entity> bloodTarget = world.findNearest(getPosition(), Blood.class);
        Optional<Entity> spiderTarget = world.findNearest(getPosition(), Spider.class);
        Optional<Entity> target = Optional.empty();

        //if the blood and spider are present choose the nearest target, other choose what is present
        if(bloodTarget.isPresent() && spiderTarget.isPresent()){
            int distToBlood = getPosition().distanceSquared(bloodTarget.get().getPosition());
            int distToSpider = getPosition().distanceSquared(spiderTarget.get().getPosition());
            target = (distToSpider >= distToBlood) ? bloodTarget : spiderTarget;
        }
        else if (spiderTarget.isPresent() && stumpTarget.isPresent()){
            target = spiderTarget;
        }
        else if(bloodTarget.isPresent()){
            target = bloodTarget;
        }
        else if (stumpTarget.isPresent()){
            target = stumpTarget;
        }

        if(target.isPresent()){
            Point targetPoint = target.get().getPosition();
            if(moveTo(world, target.get(), scheduler)){
                if(target.get() instanceof Blood){
                    DudeNotFull dudeNotFull = new DudeNotFull(Dude.DUDE_KEY, targetPoint, imageStore.getImageList(Dude.DUDE_KEY), 0.180,
                            0.787, 4, 1);
                    world.tryAddEntity(dudeNotFull);
                    dudeNotFull.ScheduleActions(scheduler, world, imageStore);
                }
                else if (target.get() instanceof Spider) {
                    //can add functionality when the fairy kills the spider if it was wanted
                }
                else{
                    if(target.get() instanceof Stump){
                        Sapling sapling = new Sapling(Sapling.SAPLING_KEY + "_" + target.get().getId(), targetPoint, imageStore.getImageList(Sapling.SAPLING_KEY),
                        Sapling.SAPLING_ACTION_ANIMATION_PERIOD, Sapling.SAPLING_ACTION_ANIMATION_PERIOD, 0, Sapling.SAPLING_HEALTH_LIMIT);
                     world.addEntity(sapling);
                     sapling.ScheduleActions(scheduler, world, imageStore);
                    }
                }

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
            }
            return false;
        }
    }
}