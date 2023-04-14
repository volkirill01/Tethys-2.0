package editor.eventListeners;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {

    private static double scrollX, scrollY;
    private static double mouseXPos, mouseYPos, lastMouseXPos, lastMouseYPos;

    private static final int MOUSE_BUTTONS_COUNT = 3;
    private static final boolean[] mouseButtonsDown = new boolean[MOUSE_BUTTONS_COUNT];
    private static final boolean[] mouseButtonsDragging = new boolean[MOUSE_BUTTONS_COUNT];

    private MouseListener() {
        scrollX = 0.0f;
        scrollY = 0.0f;
        mouseXPos = 0.0f;
        mouseYPos = 0.0f;
        lastMouseXPos = 0.0f;
        lastMouseYPos = 0.0f;
    }

    protected void endFrame() {
        scrollX = 0.0f;
        scrollY = 0.0f;
        lastMouseXPos = mouseXPos;
        lastMouseYPos = mouseYPos;
    }

    protected static void mousePositionCallback(long window, double mouseXPos, double mouseYPos) {
        MouseListener.lastMouseXPos = MouseListener.mouseXPos;
        MouseListener.lastMouseYPos = MouseListener.mouseYPos;

        MouseListener.mouseXPos = mouseXPos;
        MouseListener.mouseYPos = mouseYPos;

        System.arraycopy(mouseButtonsDown, 0, mouseButtonsDragging, 0, MOUSE_BUTTONS_COUNT);
    }

    protected static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (button > MOUSE_BUTTONS_COUNT)
            throw new IndexOutOfBoundsException("'" + button + "' - button out of range.");

        if (action == GLFW_PRESS) {
            mouseButtonsDown[button] = true;
        } else if (action == GLFW_RELEASE) {
            mouseButtonsDown[button] = false;
            mouseButtonsDragging[button] = false;
        }
    }

    protected static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        MouseListener.scrollX = xOffset;
        MouseListener.scrollY = yOffset;
    }

    protected static float getMouseX() { return (float) mouseXPos; }

    protected static float getMouseY() { return (float) mouseYPos; }

    protected static float getMouseDeltaX() { return (float) (lastMouseXPos - mouseXPos); }

    protected static float getMouseDeltaY() { return (float) (lastMouseYPos - mouseYPos); }

    protected static float getScrollX() { return (float) scrollX; }

    protected static float getScrollY() { return (float) scrollY; }

    protected static boolean isButtonDown(int button) {
        if (button > MOUSE_BUTTONS_COUNT)
            throw new IndexOutOfBoundsException("'" + button + "' - button out of range.");

        return mouseButtonsDown[button];
    }

    protected static boolean isButtonDrag(int button) {
        if (button > MOUSE_BUTTONS_COUNT)
            throw new IndexOutOfBoundsException("'" + button + "' - button out of range.");

        return mouseButtonsDragging[button];
    }
}
