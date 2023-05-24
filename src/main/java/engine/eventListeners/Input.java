package engine.eventListeners;

import engine.editor.console.Console;
import engine.editor.console.LogType;
import engine.profiling.Profiler;
import engine.stuff.Window;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class Input {

    public static void setInputCallbacks() {
        Profiler.startTimer("Set Input Callbacks");
        glfwSetCursorPosCallback(Window.getGlfwWindow(), MouseListener::mousePositionCallback);
        glfwSetMouseButtonCallback(Window.getGlfwWindow(), MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(Window.getGlfwWindow(), MouseListener::mouseScrollCallback);

        glfwSetKeyCallback(Window.getGlfwWindow(), KeyListener::keyCallback);
        Profiler.stopTimer("Set Input Callbacks");
    }

    public static boolean buttonDown(int keyCode) {
        if (keyCode >= 32)
            return KeyListener.isKeyDown(keyCode);
        else if (keyCode <= 7)
            return MouseListener.isButtonDown(keyCode);

        Console.log(String.format(String.format("'%d' - button out of bounds of Listeners.", keyCode), LogType.Error));
        return false;
    }

    public static boolean buttonClick(int keyCode) {
        if (keyCode >= 32)
            return KeyListener.isKeyClick(keyCode);
        else if (keyCode <= 7)
            return MouseListener.isButtonClick(keyCode);

        Console.log(String.format(String.format("'%d' - button out of bounds of Listeners.", keyCode), LogType.Error));
        return false;
    }

    public static Vector2f getMousePosition() { return MouseListener.getMousePos(); }

    public static float getMousePositionX() { return MouseListener.getMouseX(); }

    public static float getMousePositionY() { return MouseListener.getMouseY(); }

    public static float getMouseDeltaPositionX() { return MouseListener.getMouseDeltaX(); }

    public static float getMouseDeltaPositionY() { return MouseListener.getMouseDeltaY(); }

    public static Vector2f getMouseScreenPosition() { return MouseListener.getScreen(); }

    public static float getMouseScreenPositionX() { return MouseListener.getScreenX(); }

    public static float getMouseScreenPositionY() { return MouseListener.getScreenY(); }

    public static Vector2f getMouseWorldPosition() { return MouseListener.get2DWorldPos(); }

    public static float getMouseWorldPositionX() { return MouseListener.get2DWorldPosX(); }

    public static float getMouseWorldPositionY() { return MouseListener.get2DWorldPosY(); }

    public static float getMouseWorldDeltaPositionX() { return MouseListener.getMouse2DWorldDeltaPosX(); }

    public static float getMouseWorldDeltaPositionY() { return MouseListener.getMouse2DWorldDeltaPosY(); }

    public static Vector2f screenToWorld(Vector2f screen) { return MouseListener.screenToWorld(screen); }

    public static Vector2f worldToScreen(Vector2f world) { return MouseListener.worldToScreen(world); }

    public static boolean isMouseDragging() { return MouseListener.isDragging(); }

    public static float getMouseScrollX() { return MouseListener.getScrollX(); }

    public static float getMouseScrollY() { return MouseListener.getScrollY(); }

    public static boolean anyButtonDown() { return KeyListener.isAnyKeyDown() || MouseListener.isAnyButtonDown(); }
}
