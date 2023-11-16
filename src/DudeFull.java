import processing.core.PImage;
import java.util.List;
import java.util.Optional;

public class DudeFull extends Dude{

    public DudeFull(String id, Point position, List<PImage> images, double animationPeriod , double actionPeriod, int resourceLimit){
        super(id, position, images, animationPeriod, actionPeriod, resourceLimit);
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = world.findNearest(getPosition(), House.HOUSE_KEY);

        if (fullTarget.isPresent() && moveTo(world, fullTarget.get(), scheduler)) {
            this.transform(world, scheduler, imageStore);
        }
        else {
            scheduler.scheduleEvent( this, new ActionActivity(this, world, imageStore), getActionPeriod());
        }
    }

    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (getPosition().adjacent(target.getPosition())) {
            return true;
        } else {
            Point nextPos = this.nextPosition(world, target.getPosition());

            if (!getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    @Override
    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        DudeNotFull dude = new DudeNotFull(getId(), getPosition(), getImages() , getAnimationPeriod(),
                getActionPeriod(), getResourceLimit());
        world.removeEntity(scheduler, this);
        world.addEntity(dude);
        dude.ScheduleActions(scheduler, world, imageStore);
        return true;
    }


}
