import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class DudeNotFull extends Dude{
    private int resourceCount;

    public DudeNotFull(String id, Point position, List<PImage> images, double animationPeriod , double actionPeriod, int resourceLimit){
        super(id, position, images, animationPeriod, actionPeriod, resourceLimit);
        this.resourceCount = 0;
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        //System.out.println("executeActivity: Searching for nearest Tree and Sapling. Current position: " + getPosition());
        Optional<Entity> targetTree = world.findNearest(getPosition(), Tree.class);
        Optional<Entity> targetSapling = world.findNearest(getPosition(), Sapling.class);

        Optional<Entity> target;

        if (targetTree.isPresent() && targetSapling.isPresent()) {
            int distanceToTree = getPosition().distanceSquared(targetTree.get().getPosition());
            int distanceToSapling = getPosition().distanceSquared(targetSapling.get().getPosition());

            target = (distanceToTree <= distanceToSapling) ? targetTree : targetSapling;
        } else if (targetTree.isPresent()) {
            target = targetTree;
        } else {
            target = targetSapling;
        }

        if (target.isEmpty() || !(moveTo(world, target.get(), scheduler)) || !(transform(world, scheduler, imageStore))) {
            scheduler.scheduleEvent(this, new ActionActivity(this, world, imageStore), getActionPeriod());
        }
    }

    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (getPosition().adjacent(target.getPosition())) {
            this.resourceCount += 1;
            if(target instanceof Tree){
                ((Tree) target).setHealth(((Tree) target).getHealth() - 1);
            }
            else{
                ((Sapling) target).setHealth(((Sapling) target).getHealth() - 1);
            }
            return true;
        } else {
            Point nextPos = this.nextPosition(world, target.getPosition());

//            System.out.println("DudeNotFull: Current Position: " + getPosition());
//            System.out.println("DudeNotFUll: Next Position: " + nextPos);

            if (!getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    @Override
    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.resourceCount >= getResourceLimit()) {

            DudeFull dude = new DudeFull(getId(), getPosition(), getImages(), getAnimationPeriod(),
                    getActionPeriod(), getResourceLimit());
            System.out.println("dude not full transformed");
            world.removeEntity(scheduler, this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(dude);
            dude.ScheduleActions(scheduler, world, imageStore);
            return true;
        }
        return false;
    }
}
