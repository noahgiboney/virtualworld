public class ActionActivity extends Action{
    private final WorldModel world;
    private final ImageStore imageStore;

    public ActionActivity(Entity entity, WorldModel world, ImageStore imageStore){
        super(entity);
        this.world = world;
        this.imageStore = imageStore;
    }

    @Override
    public void executeAction(EventScheduler scheduler) {
        ActivityEntity temp = (ActivityEntity) getEntity();
        temp.executeActivity(this.world, this.imageStore, scheduler);
    }
}
