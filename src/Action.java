/**
 * An action that can be taken by an entity
 */

public abstract class Action{
    private Entity entity;
    public Action(Entity entity){
        this.entity = entity;
    }

    public abstract void executeAction(EventScheduler scheduler);
    public Entity getEntity() {
        return entity;
    }
}