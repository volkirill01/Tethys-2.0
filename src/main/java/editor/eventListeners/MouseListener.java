package editor.eventListeners;

import editor.renderer.Camera;
import editor.scenes.SceneManager;
import imgui.ImVec2;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {

    private static double scrollX, scrollY;
    private static double mouseXPos, mouseYPos, lastMouseXPos, lastMouseYPos, mouseWorldXPos, mouseWorldYPos, lastMouseWorldXPos, lastMouseWorldYPos;

    private static final int MOUSE_BUTTONS_COUNT = 9;
    private static final boolean[] mouseButtonsDown = new boolean[MOUSE_BUTTONS_COUNT];
    private static int pressedButtonsCount = 0;
    private static boolean isDragging = false;

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

    public static void endFrame() {
        scrollX = 0.0f;
        scrollY = 0.0f;
        lastMouseXPos = mouseXPos;
        lastMouseYPos = mouseYPos;
        lastMouseWorldXPos = mouseWorldXPos;
        lastMouseWorldYPos = mouseWorldYPos;
    }

    protected static void mousePositionCallback(long window, double mouseXPos, double mouseYPos) {
        if (pressedButtonsCount > 0)
            isDragging = true;

        MouseListener.lastMouseXPos = MouseListener.mouseXPos;
        MouseListener.lastMouseYPos = MouseListener.mouseYPos;

        MouseListener.lastMouseWorldXPos = MouseListener.mouseWorldXPos;
        MouseListener.lastMouseWorldYPos = MouseListener.mouseWorldYPos;

        MouseListener.mouseXPos = mouseXPos;
        MouseListener.mouseYPos = mouseYPos;

        MouseListener.calculateOrthographicPos();
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (button > MOUSE_BUTTONS_COUNT)
            throw new IndexOutOfBoundsException("'" + button + "' - button out of range.");

        if (action == GLFW_PRESS) {
            pressedButtonsCount++;

            mouseButtonsDown[button] = true;
        } else if (action == GLFW_RELEASE) {
            pressedButtonsCount--;

            mouseButtonsDown[button] = false;
            isDragging = pressedButtonsCount != 0 && isDragging;
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        MouseListener.scrollX = xOffset;
        MouseListener.scrollY = yOffset;
    }

    protected static float getMouseX() { return (float) mouseXPos; }

    protected static float getMouseY() { return (float) mouseYPos; }

    protected static float getScreenX() {
        float currentX = getMouseX() - gameViewportPos.x;
        currentX = (currentX / gameViewportSize.x) * 2560;
        return currentX;
    }

    protected static float getScreenY() {
        float currentY = getMouseY() - gameViewportPos.y;
        currentY = 1080 - ((currentY / gameViewportSize.y) * 1080);
        return currentY;
    }

    private static void calculateOrthographicPos() {
        float currentX = getMouseX() - gameViewportPos.x;
        float currentY = getMouseY() - gameViewportPos.y;

        currentX = (currentX / gameViewportSize.x) * 2.0f - 1.0f;
        currentY = -((currentY / gameViewportSize.y) * 2.0f - 1.0f);

        Vector4f tmp = new Vector4f(currentX, currentY, 0.0f, 1.0f);

        Camera camera = SceneManager.getCurrentScene().getCamera();
        Matrix4f viewProjection = new Matrix4f();
        camera.getInverseViewMatrix().mul(camera.getInverseProjectionMatrix(), viewProjection);
        tmp.mul(viewProjection);

        mouseWorldXPos = tmp.x;
        mouseWorldYPos = tmp.y;
    }

    protected static Vector2f getOrthographicPos() { return new Vector2f((float) mouseWorldXPos, (float) mouseWorldYPos); }

    protected static float getOrthographicXPos() { return (float) mouseWorldXPos; }

    protected static float getOrthographicYPos() { return (float) mouseWorldYPos; }

    protected static float getMouseDeltaX() { return (float) (lastMouseXPos - mouseXPos); }

    protected static float getMouseDeltaY() { return (float) (lastMouseYPos - mouseYPos); }

    protected static float getMouseWorldXPosition() { return (float) mouseWorldXPos; }

    protected static float getMouseWorldYPosition() { return (float) mouseWorldYPos; }

    protected static float getMouseWorldDeltaXPosition() { return (float) (lastMouseWorldXPos - mouseWorldXPos); }

    protected static float getMouseWorldDeltaYPosition() { return (float) (lastMouseWorldYPos - mouseWorldYPos); }

    protected static float getScrollX() { return (float) scrollX; }

    protected static float getScrollY() { return (float) scrollY; }

    protected static boolean isButtonDown(int button) {
        if (button > MOUSE_BUTTONS_COUNT)
            throw new IndexOutOfBoundsException("'" + button + "' - button out of range.");

        return mouseButtonsDown[button];
    }

    protected static boolean isDragging() { return isDragging; }

    public static void setGameViewportPos(ImVec2 gameViewportPos) {
        MouseListener.gameViewportPos.x = gameViewportPos.x;
        MouseListener.gameViewportPos.y = gameViewportPos.y;
    }

    public static void setGameViewportSize(ImVec2 gameViewportSize) {
        MouseListener.gameViewportSize.x = gameViewportSize.x;
        MouseListener.gameViewportSize.y = gameViewportSize.y;
    }
}
