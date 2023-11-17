import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

/**
 * A simple class representing a location in 2D space.
 */
public final class Point {
    private final int x;
    private final int y;
    private int gCost;
    private int fCost;
    private Point prior;

    public Point(int x, int y)   {
        this.x = x;
        this.y = y;
    }

    public int calcDistanceFromStart(Point startPoint){
        return Math.abs(this.x - startPoint.x) + Math.abs(this.y - startPoint.y);
    }

    public int calcToAdjacent(Point current){
        return Math.abs(this.x - current.x) + Math.abs(this.y - current.y);
    }

    public int calcF(Point endPoint){
        return Math.abs(this.x - endPoint.x) + Math.abs(this.y - endPoint.y) + getgCost();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getgCost() {
        return gCost;
    }

    public int getfCost() {
        return fCost;
    }

    public Point getPrior() {
        return prior;
    }

    public void setgCost(int gCost) {
        this.gCost = gCost;
    }

    public void setfCost(int fCost) {
        this.fCost = fCost;
    }

    public void setPrior(Point prior) {
        this.prior = prior;
    }

    public boolean adjacent( Point p2) {
        return (this.x == p2.x && Math.abs(this.y - p2.y) == 1) ||
                (this.y == p2.y && Math.abs(this.x - p2.x) == 1);
    }

    public int getIntFromRange(int max, int min) {
        Random rand = new Random();
        return min + rand.nextInt(max-min);
    }

    public double getNumFromRange(double max, double min) {
        Random rand = new Random();
        return min + rand.nextDouble() * (max - min);
    }

    public Optional<Entity> nearestEntity(List<Entity> entities, Point pos) {
        if (entities.isEmpty()) {
            return Optional.empty();
        } else {
            Entity nearest = entities.get(0);
            int nearestDistance = pos.distanceSquared(nearest.getPosition());

            for (Entity other : entities) {
                int otherDistance = pos.distanceSquared(other.getPosition());

                if (otherDistance < nearestDistance) {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }
            return Optional.of(nearest);
        }
    }

    public int distanceSquared( Point p2) {
        int deltaX = this.x - p2.x;
        int deltaY = this.y - p2.y;

        return deltaX * deltaX + deltaY * deltaY;
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public boolean equals(Object other) {
        return other instanceof Point && ((Point) other).x == this.x && ((Point) other).y == this.y;
    }

    public int hashCode() {
        int result = 17;
        result = result * 31 + x;
        result = result * 31 + y;
        return result;
    }
}
