package engine.observers.events;

public class Event {

    public final EventType type;
    public final Object data;

    public Event(EventType type) {
        this.type = type;
        this.data = null;
    }

    public Event(EventType type, Object data) {
        this.type = type;
        this.data = data;
    }
}
