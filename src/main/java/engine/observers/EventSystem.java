package engine.observers;

import engine.entity.GameObject;
import engine.observers.events.Event;
import engine.profiling.Profiler;

import java.util.ArrayList;
import java.util.List;

public class EventSystem {

    private static final List<Observer> observers = new ArrayList<>();

    public static void addObserver(Observer observer) { observers.add(observer); }

    public static void notify(GameObject obj, Event event) {
        Profiler.startTimer(String.format("EventSystem Notify - '%s', data: '%s'", event.type.name(), event.data));
        for (Observer observer : observers)
            observer.onNotify(obj, event);
        Profiler.stopTimer(String.format("EventSystem Notify - '%s', data: '%s'", event.type.name(), event.data));
    }
}
