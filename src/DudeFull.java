import processing.core.PImage;
import java.util.List;
import java.util.Optional;

public class DudeFull extends Dude{

    public DudeFull(String id, Point position, List<PImage> images, double animationPeriod , double actionPeriod, int resourceLimit, int health){
        super(id, position, images, animationPeriod, actionPeriod, resourceLimit, health);
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = world.findNearest(getPosition(), House.class);

        if(getHealth() < 1){
            this.transform(world,scheduler,imageStore);
        }

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
            Point nextPos = nextPosition(world, target.getPosition());

            if (!getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    @Override
    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {

        if(getHealth() < 1){
            Point bloodSpot = getPosition();
            world.removeEntity(scheduler, this);

            Blood blood = new Blood(Blood.BLOOD_KEY, bloodSpot, imageStore.getImageList(Blood.BLOOD_KEY), 0.1);
            world.tryAddEntity(blood);
            blood.ScheduleActions(scheduler, world, imageStore);
            return true;
        }
        else{
            DudeNotFull dude = new DudeNotFull(getId(), getPosition(), getImages() , getAnimationPeriod(),
                    getActionPeriod(), getResourceLimit(), 1);
            world.removeEntity(scheduler, this);
            world.addEntity(dude);
            dude.ScheduleActions(scheduler, world, imageStore);
            return true;
        }
    }
}
