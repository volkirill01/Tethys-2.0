package engine.profiling;

import engine.stuff.utils.Time;

public class Timer {

    private final String name;
    private float startPoint;
    private float endPoint;
    private float duration;
    private boolean isStop = false;

    public Timer(String name) { this.name = name; }

    public void start() {
        this.startPoint = Time.getTime();
        this.isStop = false;
    }

    public void stop() {
        if (!this.isStop) {
            this.endPoint = Time.getTime();

            this.isStop = true;

            this.duration = this.endPoint - this.startPoint;
        }
    }

    public String getName() { return this.name; }

    public float getStartPoint() { return this.startPoint; }

    public float getEndPoint() { return this.endPoint; }

    public float getDuration() { return this.duration; }

    public boolean isStop() { return this.isStop; }
}
