package engine.layerStack;

import engine.observers.events.Event;

public abstract class Layer {

    public abstract void init();

    public abstract void update();

    public abstract boolean onEvent(Event event);

    public void cleanUp() { }
}
