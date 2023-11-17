import processing.core.PImage;

import java.util.List;

public abstract class Dude extends ActivityEntity implements Transform, MoveTo{
    public static final String DUDE_KEY = "dude";
    public static final int DUDE_ACTION_PERIOD = 0;
    public static final int DUDE_ANIMATION_PERIOD = 1;
    public static final int DUDE_LIMIT = 2;
    private final int resourceLimit;

     //private static final PathingStrategy DUDE_PATHING = new AStarPathingStrategy();
    private static final PathingStrategy DUDE_PATHING = new SingleStepPathingStrategy();
    public Dude(String id, Point position, List<PImage> images, double animationPeriod , double actionPeriod,int resourceLimit){
        super(id, position, images, animationPeriod, actionPeriod);
        this.resourceLimit = resourceLimit;
    }


    @Override
    public Point nextPosition(WorldModel world, Point destPos) {

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

//        if (getPosition().adjacent(destPos)) {
//            return getPosition(); // Return the current position if the destination is reached
//        }
//
//        List<Point> path = DUDE_PATHING.computePath(getPosition(),
//                destPos,
//                point -> world.withinBounds(point) && !world.isOccupied(point),
//                Point::adjacent,
//                PathingStrategy.CARDINAL_NEIGHBORS);
//
//        if (path.size() == 0)
//            return this.getPosition();
//        else {
//            return path.get(0);
//        }
    }

    public int getResourceLimit() {
        return resourceLimit;
    }

    @Override
    public String getKey(){
        return DUDE_KEY;
    }

}
