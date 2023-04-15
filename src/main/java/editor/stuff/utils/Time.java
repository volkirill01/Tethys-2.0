package editor.stuff.utils;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Time {

    private static float deltaTime = -1.0f;

    public static float timeProgramStarted = (float) glfwGetTime();

    public static float getTime() { return (float) glfwGetTime() - timeProgramStarted; }

    public static void setDeltaTime(float deltaTime) { Time.deltaTime = deltaTime; }

    public static float deltaTime() { return deltaTime; }
}
