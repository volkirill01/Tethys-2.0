package editor.eventListeners;

import editor.stuff.Window;

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
}
