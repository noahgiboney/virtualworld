import processing.core.PImage;

import javax.xml.stream.events.EndElement;
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

        //all three possible targets for dude
        Optional<Entity> targetTree = world.findNearest(getPosition(), Tree.class);
        Optional<Entity> targetSapling = world.findNearest(getPosition(), Sapling.class);
        Optional<Entity> target = Optional.empty();

        // Check for Tree and Sapling if Spider is not a valid target
        if (targetTree.isPresent() && targetSapling.isPresent()) {
            int distanceToTree = getPosition().distanceSquared(targetTree.get().getPosition());
            int distanceToSapling = getPosition().distanceSquared(targetSapling.get().getPosition());
            target = (distanceToTree <= distanceToSapling) ? targetTree : targetSapling;
        } else if (targetTree.isPresent()) {
            target = targetTree;
        } else if (targetSapling.isPresent()) {
            target = targetSapling;
        }

        // Execute movement or transformation if a target is present
        if (target.isEmpty() || !(moveTo(world, target.get(), scheduler)) || !(transform(world, scheduler, imageStore))) {
            scheduler.scheduleEvent(this, new ActionActivity(this, world, imageStore), getActionPeriod());
        }
    }

    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {

        if (getPosition().adjacent(target.getPosition())) {
            if (target instanceof Tree) {
                this.resourceCount += 1; // Increase resource count for Tree
                ((Tree) target).setHealth(((Tree) target).getHealth() - 1);
            } else if (target instanceof Sapling) {
                this.resourceCount += 1; // Increase resource count for Sapling
                ((Sapling) target).setHealth(((Sapling) target).getHealth() - 1);
            }
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
    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.resourceCount >= getResourceLimit()) {

            DudeFull dude = new DudeFull(getId(), getPosition(), getImages(), getAnimationPeriod(),
                    getActionPeriod(), getResourceLimit());
            world.removeEntity(scheduler, this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(dude);
            dude.ScheduleActions(scheduler, world, imageStore);
            return true;
        }
        return false;
    }
}
