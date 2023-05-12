package engine.observers;

import engine.entity.GameObject;
import engine.observers.events.Event;

public interface Observer {

    void onNotify(GameObject object, Event event);
}
