import processing.core.PImage;

import java.util.List;

public class Sapling extends HealthEntity implements Transform{
    public static final String SAPLING_KEY = "sapling";
    public static final int SAPLING_HEALTH = 0;
    public static final double SAPLING_ACTION_ANIMATION_PERIOD = 1.000; // have to be in sync since grows and gains health at same time
    public static final int SAPLING_HEALTH_LIMIT = 5;
    private final int healthLimit;

    public Sapling (String id, Point position, List<PImage> images, double animationPeriod , double actionPeriod, int health, int healthLimit){
        super(id, position, images, animationPeriod, actionPeriod, health);
        this.healthLimit = healthLimit;
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        setHealth(getHealth() + 1);
        if (!transform( world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, new ActionActivity(this, world, imageStore), getActionPeriod());
        }
    }

    @Override
    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (getHealth() <= 0) {
            Stump stump = new Stump(Stump.STUMP_KEY + "_" + getId(),
                    getPosition(), imageStore.getImageList(Stump.STUMP_KEY));

            world.removeEntity( scheduler,this);
            world.addEntity(stump);
            return true;
        } else if (getHealth() >= this.healthLimit) {
            Tree tree = new Tree(Tree.TREE_KEY + "_" + getId(), getPosition(),
                    imageStore.getImageList(Tree.TREE_KEY),
                    getPosition().getNumFromRange(Tree.TREE_ANIMATION_MAX, Tree.TREE_ANIMATION_MIN),
                    getPosition().getNumFromRange(Tree.TREE_ACTION_MAX, Tree.TREE_ACTION_MIN),
                    getPosition().getIntFromRange(Tree.TREE_HEALTH_MAX, Tree.TREE_HEALTH_MIN));

            world.removeEntity(scheduler, this);
            world.addEntity(tree);
            tree.ScheduleActions(scheduler, world, imageStore);
            return true;
        }
        return false;
    }

    @Override
    public void ScheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, new ActionActivity(this, world, imageStore), getActionPeriod());
        scheduler.scheduleEvent(this, new ActionAnimation(this, 0), getAnimationPeriod());
    }

    @Override
    public String getKey() {
        return SAPLING_KEY;
    }
}
