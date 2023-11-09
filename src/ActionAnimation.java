public class ActionAnimation extends Action{
    private final int repeatCount;

    public ActionAnimation(Entity entity, int repeatCount){
        super(entity);
        this.repeatCount = repeatCount;
    }

    @Override
    public void executeAction(EventScheduler scheduler) {

        AnimationEntity temp = (AnimationEntity) getEntity();
        temp.nextImage();

        if (this.repeatCount != 1) {
            scheduler.scheduleEvent(getEntity(), new ActionAnimation(getEntity(),Math.max(this.repeatCount - 1, 0)), temp.getAnimationPeriod());
        }
    }
}
