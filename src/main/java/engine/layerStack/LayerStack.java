package engine.layerStack;

import engine.observers.events.Event;

import java.util.ArrayList;
import java.util.List;

public class LayerStack {

    private static final List<Layer> layers = new ArrayList<>();

    public static void attachLayer(Layer layer) { attachLayer(layer, false); }

    public static void attachLayer(Layer layer, boolean initializeLayer) {
        layers.add(layer);
        if (initializeLayer)
            layer.init();
    }

    public static void init() {
        for (Layer layer : layers)
            layer.init();
    }

    public static void update() {
        for (Layer layer : layers)
            layer.update();
    }

    public static void onEvent(Event event) {
        for (Layer layer : layers)
            System.out.println(layer.onEvent(event));
    }

    public static void cleanUp() {
        for (Layer layer : layers)
            layer.cleanUp();
    }
}
