package editor.stuff.utils;

public class Time {

    private static float deltaTime = -1.0f;

    public static float timeProgramStarted = System.nanoTime();

    public static float getTime() { return (float) ((System.nanoTime() - timeProgramStarted) * 1E-9); }

    public static void setDeltaTime(float deltaTime) { Time.deltaTime = deltaTime; }

    public static float deltaTime() { return deltaTime; }
}
