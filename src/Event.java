/**
 * An event is made up of an Entity that is taking an
 * Action a specified time.
 */
public record Event(Action action, double time, Entity entity) {
}
