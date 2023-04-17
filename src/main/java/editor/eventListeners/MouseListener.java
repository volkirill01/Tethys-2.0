package editor.eventListeners;

import editor.renderer.Camera;
import editor.scenes.SceneManager;
import editor.stuff.Window;
import imgui.ImVec2;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {

    private static double scrollX, scrollY;
    private static double mouseXPos, mouseYPos, lastMouseXPos, lastMouseYPos;

    private static final int MOUSE_BUTTONS_COUNT = 9;
    private static final boolean[] mouseButtonsDown = new boolean[MOUSE_BUTTONS_COUNT];
    private static final boolean[] mouseButtonsDragging = new boolean[MOUSE_BUTTONS_COUNT];

    private static final ImVec2 gameViewportPos = new ImVec2();
    private static final ImVec2 gameViewportSize = new ImVec2();

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

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (button > MOUSE_BUTTONS_COUNT)
            throw new IndexOutOfBoundsException("'" + button + "' - button out of range.");

        if (action == GLFW_PRESS) {
            mouseButtonsDown[button] = true;
        } else if (action == GLFW_RELEASE) {
            mouseButtonsDown[button] = false;
            mouseButtonsDragging[button] = false;
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        MouseListener.scrollX = xOffset;
        MouseListener.scrollY = yOffset;
    }

    protected static float getMouseX() { return (float) mouseXPos; }

    protected static float getMouseY() { return (float) mouseYPos; }

    protected static float getOrthographicX() {
        float currentX = getMouseX() - gameViewportPos.x;
        currentX = (currentX / gameViewportSize.x) * 2.0f - 1.0f;
        Vector4f tmp = new Vector4f(currentX, 0.0f, 0.0f, 1.0f);

        Camera camera = SceneManager.getCurrentScene().getCamera();
        Matrix4f viewProjection = new Matrix4f();
        camera.getInverseViewMatrix().mul(camera.getInverseProjectionMatrix(), viewProjection);
        tmp.mul(viewProjection);
        currentX = tmp.x;

        return currentX;
    }

    protected static float getOrthographicY() {
        float currentY = getMouseY() - gameViewportPos.y;
        currentY = -((currentY / gameViewportSize.y) * 2.0f - 1.0f);
        Vector4f tmp = new Vector4f(0.0f, currentY, 0.0f, 1.0f);

        Camera camera = SceneManager.getCurrentScene().getCamera();
        Matrix4f viewProjection = new Matrix4f();
        camera.getInverseViewMatrix().mul(camera.getInverseProjectionMatrix(), viewProjection);
        tmp.mul(viewProjection);
        currentY = tmp.y;

        return currentY;
    }

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

    public static void setGameViewportPos(ImVec2 gameViewportPos) {
        MouseListener.gameViewportPos.x = gameViewportPos.x;
        MouseListener.gameViewportPos.y = gameViewportPos.y;
    }

    public static void setGameViewportSize(ImVec2 gameViewportSize) {
        MouseListener.gameViewportSize.x = gameViewportSize.x;
        MouseListener.gameViewportSize.y = gameViewportSize.y;
    }
}
