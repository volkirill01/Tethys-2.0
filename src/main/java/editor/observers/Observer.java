package editor.observers;

import editor.entity.GameObject;
import editor.observers.events.Event;

public interface Observer {

    void onNotify(GameObject object, Event event);
}
