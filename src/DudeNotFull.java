import processing.core.PImage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DudeNotFull extends Dude{
    private int resourceCount;
    private int resourceLimit;

    public DudeNotFull(String id, Point position, List<PImage> images, double animationPeriod , double actionPeriod, int resourceLimit){
        super(id, position, images, animationPeriod, actionPeriod);
        this.resourceLimit = resourceLimit;
        this.resourceCount = 0;
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

        if (target.isEmpty() || !(moveTo(world, target.get(), scheduler)) || !(transform(world, scheduler, imageStore))) {
            scheduler.scheduleEvent(this, new ActionActivity(this, world, imageStore), getActionPeriod());
        }
    }

    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (getPosition().adjacent(target.getPosition())) {
            this.resourceCount += 1;
            if(target.getKey().equals(Tree.TREE_KEY)){
                ((Tree) target).setHealth(((Tree) target).getHealth() - 1);
            }
            else{
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
        if (this.resourceCount >= this.resourceLimit) {

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
    public Point nextPosition(WorldModel world, Point destPos) {
        {
            int horiz = Integer.signum(destPos.getX() - getPosition().getX());
            Point newPos = new Point(getPosition().getX() + horiz, getPosition().getY());

            if (horiz == 0 || world.isOccupied(newPos) && !(world.getOccupancyCell(newPos) instanceof Stump)) {
                int vert = Integer.signum(destPos.getY() - getPosition().getY());
                newPos = new Point(getPosition().getX(), getPosition().getY() + vert);

                if (vert == 0 || world.isOccupied(newPos) && !(world.getOccupancyCell(newPos) instanceof Stump)) {
                    newPos = getPosition();
                }
            }

            return newPos;
        }
    }

    @Override
    public void ScheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, new ActionActivity(this, world, imageStore), getActionPeriod());
        scheduler.scheduleEvent(this, new ActionAnimation(this, 0), getAnimationPeriod());
    }


}
