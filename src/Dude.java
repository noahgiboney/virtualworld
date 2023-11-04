import processing.core.PImage;

import java.util.List;

public abstract class Dude extends ActivityEntity implements Transform, MoveTo{

    public static final String DUDE_KEY = "dude";
    public static final int DUDE_ACTION_PERIOD = 0;
    public static final int DUDE_ANIMATION_PERIOD = 1;
    public static final int DUDE_LIMIT = 2;
    private final int resourceLimit;

    public Dude(String id, Point position, List<PImage> images, double animationPeriod , double actionPeriod,int resourceLimit){
        super(id, position, images, animationPeriod, actionPeriod);
        this.resourceLimit = resourceLimit;
    }

    @Override
    public Point nextPosition(WorldModel world, Point destPos) {{
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

    public int getResourceLimit() {
        return resourceLimit;
    }

    @Override
    public String getKey(){
        return DUDE_KEY;
    }

}
