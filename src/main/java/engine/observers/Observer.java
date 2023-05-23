package engine.observers;

import engine.observers.events.Event;

public interface Observer {

    void onNotify(Event event);
}
