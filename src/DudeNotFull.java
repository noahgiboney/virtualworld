import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class DudeNotFull extends Dude {
    private int resourceCount;

    public DudeNotFull(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int resourceLimit, int health) {
        super(id, position, images, animationPeriod, actionPeriod, resourceLimit, health);
        this.resourceCount = 0;
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> targetTree = world.findNearest(getPosition(), Tree.class);
        Optional<Entity> targetSapling = world.findNearest(getPosition(), Sapling.class);
        Optional<Entity> target = Optional.empty();

        //if there is a tree and a sapling target the closer one, other wide choose whatever target is there
        if (targetTree.isPresent() && targetSapling.isPresent()) {
            int distanceToTree = getPosition().distanceSquared(targetTree.get().getPosition());
            int distanceToSapling = getPosition().distanceSquared(targetSapling.get().getPosition());
            target = (distanceToTree <= distanceToSapling) ? targetTree : targetSapling;
        } else if (targetTree.isPresent()) {
            target = targetTree;
        } else if (targetSapling.isPresent()) {
            target = targetSapling;
        }

        boolean moved = false;
        if (target.isPresent()) {
            moved = moveTo(world, target.get(), scheduler);
        }

        //check if the health has gone to zero (spider reached it)
        if (this.getHealth() < 1) {
            transform(world, scheduler, imageStore);
            return;
        }

        if (moved) {
            transform(world, scheduler, imageStore);
        }

        scheduler.scheduleEvent(this, new ActionActivity(this, world, imageStore), getActionPeriod());
    }

    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (getPosition().adjacent(target.getPosition())) {
            if (target instanceof Tree) {
                this.resourceCount += 1; //increase resource count for Tree
                ((Tree) target).setHealth(((Tree) target).getHealth() - 1);
            } else if (target instanceof Sapling) {
                this.resourceCount += 1; //increase resource count for Sapling
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

        //if the health depleted then transform dude to blood
        if (getHealth() < 1) {
            Point bloodSpot = getPosition();
            world.removeEntity(scheduler, this);
            scheduler.unscheduleAllEvents(this);

            Blood blood = new Blood(Blood.BLOOD_KEY, bloodSpot, imageStore.getImageList(Blood.BLOOD_KEY), 0.1);
            world.tryAddEntity(blood);
            blood.ScheduleActions(scheduler, world, imageStore);
            return true;
        }

        //if the dude has not died, then transform to full dude
        if (this.resourceCount >= getResourceLimit()) {
            DudeFull dude = new DudeFull(getId(), getPosition(), getImages(), getAnimationPeriod(),
                    getActionPeriod(), getResourceLimit(), getHealth());
            world.removeEntity(scheduler, this);
            scheduler.unscheduleAllEvents(this);

            world.tryAddEntity(dude);
            dude.ScheduleActions(scheduler, world, imageStore);
            return true;
        }

        return false;
    }
}