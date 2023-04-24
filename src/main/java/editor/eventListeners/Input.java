package editor.eventListeners;

import editor.stuff.Window;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class Input {

    public static void setInputCallbacks() {
        glfwSetCursorPosCallback(Window.getGlfwWindow(), MouseListener::mousePositionCallback);
        glfwSetMouseButtonCallback(Window.getGlfwWindow(), MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(Window.getGlfwWindow(), MouseListener::mouseScrollCallback);

        glfwSetKeyCallback(Window.getGlfwWindow(), KeyListener::keyCallback);
    }

    public static boolean buttonDown(int keyCode) {
        if (keyCode >= 32)
            return KeyListener.isKeyDown(keyCode);
        else if (keyCode <= 7)
            return MouseListener.isButtonDown(keyCode);

        throw new IllegalStateException("'" + keyCode + "' - button out of bounds of Listeners.");
    }

    public static float getMousePositionX() { return MouseListener.getMouseX(); }

    public static float getMousePositionY() { return MouseListener.getMouseY(); }

    public static float getMouseScreenPositionX() { return MouseListener.getScreenX(); }

    public static float getMouseScreenPositionY() { return MouseListener.getScreenY(); }

    public static Vector2f getMouseOrthographicPosition() { return MouseListener.getOrthographicPos(); }

    public static float getMouseOrthographicXPosition() { return MouseListener.getOrthographicXPos(); }

    public static float getMouseOrthographicYPosition() { return MouseListener.getOrthographicYPos(); }

    public static float getMouseWorldXPosition() { return MouseListener.getMouseWorldXPosition(); }

    public static float getMouseWorldYPosition() { return MouseListener.getMouseWorldYPosition(); }

    public static float getMouseWorldDeltaXPosition() { return MouseListener.getMouseWorldDeltaXPosition(); }

    public static float getMouseWorldDeltaYPosition() { return MouseListener.getMouseWorldDeltaYPosition(); }

    public static boolean isMouseDragging() { return MouseListener.isDragging(); }

    public static float getMouseScrollX() { return MouseListener.getScrollX(); }

    public static float getMouseScrollY() { return MouseListener.getScrollY(); }
}
