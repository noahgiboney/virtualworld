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

        //check if entity is instance of activity entity before casting
        if(getEntity() instanceof ActivityEntity temp){
            temp.executeActivity(this.world, this.imageStore, scheduler);
        }
    }
}
