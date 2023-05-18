package engine.profiling;

import java.util.ArrayList;
import java.util.List;

public class Profiler {

    private static final List<Timer> timers = new ArrayList<>();

    public static void startTimer(String name) {
        for (Timer timer : timers)
            if (timer.getName().equals(name)) {
                timer.start();
                return;
            }

        Timer timer = new Timer(name);
        timer.start();
        timers.add(timer);
    }

    public static void stopTimer(String name) {
        for (Timer timer : timers)
            if (timer.getName().equals(name)) {
                SessionReplay.writeProfile(timer);
                timer.stop();
                return;
            }

        throw new NullPointerException(String.format("No such timer - '%s'", name));
    }

    public static List<Timer> getTimers() { return timers; }
}
