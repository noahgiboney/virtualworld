import processing.core.PImage;

import java.util.List;

public class Tree extends Plant{

    public static final String TREE_KEY = "tree";
    public static final int TREE_ANIMATION_PERIOD = 0;
    public static final int TREE_ACTION_PERIOD = 1;
    public static final int TREE_HEALTH = 2;
    public static final double TREE_ANIMATION_MAX = 0.600;
    public static final double TREE_ANIMATION_MIN = 0.050;
    public static final double TREE_ACTION_MAX = 1.400;
    public static final double TREE_ACTION_MIN = 1.000;
    public static final int TREE_HEALTH_MAX = 3;
    public static final int TREE_HEALTH_MIN = 1;

    private boolean isWebbed = false;

    public Tree(String id, Point position, List<PImage> images, double animationPeriod , double actionPeriod, int health){
        super(id, position, images, animationPeriod, actionPeriod, health);
        isWebbed = false;
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        if (!this.transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, new ActionActivity(this, world, imageStore), getActionPeriod());
        }
    }

    @Override
    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (getHealth() <= 0) {
            Stump stump = new Stump(Stump.STUMP_KEY + "_" + getId(), getPosition(),
                    imageStore.getImageList(Stump.STUMP_KEY));
            world.removeEntity(scheduler, this);
            world.addEntity(stump);
            return true;
        }
        return false;
    }

    public boolean isWebbed() {
        return isWebbed;
    }

    public void setWebbed(boolean webbed) {
        isWebbed = webbed;
    }
}
