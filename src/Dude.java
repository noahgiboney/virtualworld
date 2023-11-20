import processing.core.PImage;

import java.util.List;

public abstract class Dude extends Movable implements Transform{
    public static final String DUDE_KEY = "dude";
    public static final int DUDE_ACTION_PERIOD = 0;
    public static final int DUDE_ANIMATION_PERIOD = 1;
    public static final int DUDE_LIMIT = 2;
    private static final PathingStrategy DUDE_SINGLE_STEP = new SingleStepPathingStrategy();
    private static final PathingStrategy DUDE_A_STAR = new AStarPathingStrategy();
    private final int resourceLimit;

     //private static final PathingStrategy DUDE_PATHING = new AStarPathingStrategy();
    private static final PathingStrategy DUDE_PATHING = new SingleStepPathingStrategy();
    public Dude(String id, Point position, List<PImage> images, double animationPeriod , double actionPeriod,int resourceLimit){
        super(id, position, images, animationPeriod, actionPeriod, DUDE_SINGLE_STEP);
        this.resourceLimit = resourceLimit;
    }

    public int getResourceLimit() {
        return resourceLimit;
    }

}
