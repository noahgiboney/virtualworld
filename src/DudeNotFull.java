import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;

public class  DudeNotFull extends Dude{

    private int resourceCount;
    private int limit;

    public DudeNotFull(String id, Point position, List<PImage> images, double animationPeriod , double actionPeriod, int limit){
        super(id, position, images, animationPeriod, actionPeriod);
        this.limit = limit;
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> targetTree = world.findNearest(getPosition(), Tree.TREE_KEY);
        Optional<Entity> targetSapling = world.findNearest(getPosition(), Sapling.SAPLING_KEY);

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

        if (target.isEmpty() || !moveTo(world, target.get(), scheduler) || !transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, new ActionActivity(this, world, imageStore), getActionPeriod());
        }
    }

    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        HealthEntity temp = (HealthEntity) target;
        if (getPosition().adjacent(temp.getPosition())) {
            this.resourceCount += 1;
            temp.setHealth(temp.getHealth() - 1);
            return true;
        } else {
            Point nextPos = nextPosition(world, temp.getPosition());

            if (!getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    @Override
    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.resourceCount >= this.limit) {
            DudeFull dude = new DudeFull(getId(), getPosition(), getImages(), getActionPeriod(),
                    getActionPeriod());

            world.removeEntity(scheduler, this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(dude);
            dude.ScheduleActions(scheduler, world, imageStore);

            return true;
        }
        return false;
    }

    @Override
    public void ScheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, new ActionActivity(this, world, imageStore), getActionPeriod());
        scheduler.scheduleEvent(this, new ActionAnimation(this, 0), getAnimationPeriod());
    }
}
