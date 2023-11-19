import processing.core.PImage;

import java.util.List;

public abstract class Movable extends ActivityEntity{

    private final PathingStrategy strategy;

    public Movable(String id, Point position, List<PImage> images, double animationPeriod , double actionPeriod, PathingStrategy strategy) {
        super(id, position, images, animationPeriod, actionPeriod);
        this.strategy = strategy;
    }

    public abstract boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler);

    public Point nextPosition(WorldModel world, Point destPos) {

        List<Point> path = strategy.computePath(getPosition(),
                destPos,
                point -> world.withinBounds(point) && !world.isOccupied(point),
                Point::adjacent,
                PathingStrategy.CARDINAL_NEIGHBORS);

        if (path.isEmpty()){
            return this.getPosition();
        }
        else {
            return path.get(0);
        }

        //        int horiz = Integer.signum(destPos.getX() - getPosition().getX());
//        Point newPos = new Point(getPosition().getX() + horiz, getPosition().getY());
//
//        if (horiz == 0 || world.isOccupied(newPos) && !(world.getOccupancyCell(newPos) instanceof Stump)) {
//            int vert = Integer.signum(destPos.getY() - getPosition().getY());
//            newPos = new Point(getPosition().getX(), getPosition().getY() + vert);
//
//            if (vert == 0 || world.isOccupied(newPos) && !(world.getOccupancyCell(newPos) instanceof Stump)) {
//                newPos = getPosition();
//            }
//        }
//        return newPos;

//        if (getPosition().adjacent(destPos)) {
//            return getPosition(); // Return the current position if the destination is reached
//        }
    }
}
