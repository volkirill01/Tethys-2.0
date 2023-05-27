package engine.observers;

import engine.logging.DebugLog;
import engine.observers.events.Event;
import engine.observers.events.EventType;
import engine.profiling.Profiler;

import java.util.ArrayList;
import java.util.List;

public class EventSystem {

    private static final List<Observer> observers = new ArrayList<>();

    public static void addObserver(Observer observer) { observers.add(observer); }

    public static void notify(Event event) {
        Profiler.startTimer(String.format("EventSystem Notify - '%s', data: '%s'", event.type.name(), event.data));
        if (event.type != EventType.Engine_MousePositionCallback)
            DebugLog.log("EventSystem Notify(type: ", event.type, ", data: ", event.data, ").");
        for (int i = 0; i < observers.size(); i++)
            observers.get(i).onNotify(event);
        Profiler.stopTimer(String.format("EventSystem Notify - '%s', data: '%s'", event.type.name(), event.data));
    }
}
