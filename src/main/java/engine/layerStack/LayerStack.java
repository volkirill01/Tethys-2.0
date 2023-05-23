package engine.layerStack;

import engine.observers.EventSystem;
import engine.observers.Observer;
import engine.observers.events.Event;
import engine.profiling.Profiler;

import java.util.ArrayList;
import java.util.List;

public class LayerStack implements Observer {

    private static final List<Layer> layers = new ArrayList<>();

    public LayerStack() { EventSystem.addObserver(this); }

    public static void attachLayer(Layer layer) { attachLayer(layer, false); }

    public static void attachLayer(Layer layer, boolean initializeLayer) {
        layers.add(layer);
        if (initializeLayer)
            layer.init();
    }

    public static void init() {
        Profiler.startTimer("Init LayerStack");
        for (Layer layer : layers)
            layer.init();
        Profiler.stopTimer("Init LayerStack");
    }

    public static void update() {
        Profiler.startTimer("Update LayerStack");
        for (Layer layer : layers)
            layer.update();
        Profiler.stopTimer("Update LayerStack");
    }

    public static void freeMemory() {
        for (Layer layer : layers)
            layer.freeMemory();
    }

    @Override
    public void onNotify(Event event) {
        for (Layer layer : layers) {
            boolean eventBlocked = layer.onEvent(event);

            if (eventBlocked) {
//                if (event.type == EventType.Engine_MousePositionCallback || event.type == EventType.Engine_MouseButtonCallback || event.type == EventType.Engine_MouseScrollCallback)
//                    MouseListener.clear();
//                if (event.type == EventType.Engine_KeyboardButtonCallback)
//                    KeyListener.clear();
                return;
            }
        }
    }
}
