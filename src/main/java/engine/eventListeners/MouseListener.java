package engine.eventListeners;

import engine.observers.EventSystem;
import engine.observers.events.Event;
import engine.observers.events.EventType;
import engine.profiling.Profiler;
import engine.renderer.camera.ed_EditorCamera;
import engine.scenes.SceneManager;
import engine.stuff.Window;
import imgui.ImVec2;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.AbstractMap;
import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {

    private static double scrollX, scrollY;
    private static double mousePosX, mousePosY, lastMousePosX, lastMousePosY, mouse2DWorldPosX, mouse2DWorldPosY, lastMouse2DWorldPosX, lastMouse2DWorldPosY;

    private static final int MOUSE_BUTTONS_COUNT = 9;
    private static final boolean[] buttonsDown = new boolean[MOUSE_BUTTONS_COUNT];
    private static final boolean[] buttonsClick = new boolean[MOUSE_BUTTONS_COUNT];
    private static int pressedButtonsCount = 0;
    private static boolean isDragging = false;

    private static final ImVec2 gameViewportPos = new ImVec2();
    private static final ImVec2 gameViewportSize = new ImVec2();

    private MouseListener() {
        scrollX = 0.0f;
        scrollY = 0.0f;
        mousePosX = 0.0f;
        mousePosY = 0.0f;
        lastMousePosX = 0.0f;
        lastMousePosY = 0.0f;
    }

    public static void endFrame() {
        scrollX = 0.0f;
        scrollY = 0.0f;
        lastMousePosX = mousePosX;
        lastMousePosY = mousePosY;
        lastMouse2DWorldPosX = mouse2DWorldPosX;
        lastMouse2DWorldPosY = mouse2DWorldPosY;
        Arrays.fill(buttonsClick, false);
    }

    public static void clear() {
        scrollX = 0.0f;
        scrollY = 0.0f;
        mousePosX = 0.0f;
        mousePosY = 0.0f;
        lastMousePosX = 0.0f;
        lastMousePosY = 0.0f;
        mouse2DWorldPosX = 0.0f;
        mouse2DWorldPosY = 0.0f;
        lastMouse2DWorldPosX = 0.0f;
        lastMouse2DWorldPosY = 0.0f;
        pressedButtonsCount = 0;
        isDragging = false;
        Arrays.fill(buttonsDown, false);
        Arrays.fill(buttonsClick, false);
    }

    protected static void mousePositionCallback(long window, double mouseXPos, double mouseYPos) {
//        if (!EngineGuiLayer.getWantCaptureMouse())
//            clear();
//        if (!EngineGuiLayer.isAnyWindowVisible(SceneView_Window.class))
//            return;

        if (pressedButtonsCount > 0)
            isDragging = true;

        MouseListener.lastMousePosX = MouseListener.mousePosX;
        MouseListener.lastMousePosY = MouseListener.mousePosY;

        MouseListener.lastMouse2DWorldPosX = MouseListener.mouse2DWorldPosX;
        MouseListener.lastMouse2DWorldPosY = MouseListener.mouse2DWorldPosY;

        MouseListener.mousePosX = mouseXPos;
        MouseListener.mousePosY = mouseYPos;

        MouseListener.calculateOrthographicPos();

        EventSystem.notify(new Event(EventType.Engine_MousePositionCallback, new AbstractMap.SimpleEntry<>(mouseXPos, mouseYPos)));
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        String profileLog = String.format("MouseButton Callback - (button: '%d', action: '%s')", button, action == GLFW_PRESS ? "Press" : "Release");
        Profiler.startTimer(profileLog);
        if (button > MOUSE_BUTTONS_COUNT)
            throw new IndexOutOfBoundsException(String.format("'%d' - button out of range.", button));

        if (action == GLFW_PRESS) {
            pressedButtonsCount++;

            buttonsDown[button] = true;
            buttonsClick[button] = true;
        } else if (action == GLFW_RELEASE) {
            pressedButtonsCount--;

            buttonsDown[button] = false;
            isDragging = pressedButtonsCount != 0 && isDragging;
        }
        Profiler.stopTimer(profileLog);

        EventSystem.notify(new Event(EventType.Engine_MouseButtonCallback, new AbstractMap.SimpleEntry<>(button, action == GLFW_PRESS)));
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        MouseListener.scrollX = xOffset;
        MouseListener.scrollY = yOffset;

        EventSystem.notify(new Event(EventType.Engine_MouseScrollCallback, new AbstractMap.SimpleEntry<>(xOffset, yOffset)));
    }

    protected static Vector2f screenToWorld(Vector2f screenCoordinates) {
        Vector2f normalizedDeviceCoordinates = new Vector2f(
                screenCoordinates.x / Window.getScreenWidth(),
                screenCoordinates.y / Window.getScreenHeight()
        );

        normalizedDeviceCoordinates.mul(2.0f).sub(1.0f, 1.0f);
        ed_EditorCamera camera = SceneManager.getCurrentScene().getEditorCamera();
        Vector4f tmp = new Vector4f(normalizedDeviceCoordinates.x, normalizedDeviceCoordinates.y, 0.0f, 1.0f);
        Matrix4f inverseView = new Matrix4f(camera.getInverseViewMatrix());
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjectionMatrix());
        tmp.mul(inverseView.mul(inverseProjection));
        return new Vector2f(tmp.x, tmp.y);
    }

    protected static Vector2f worldToScreen(Vector2f worldCoordinates) {
        ed_EditorCamera camera = SceneManager.getCurrentScene().getEditorCamera();
        Vector4f normalizeDeviceCoordinates = new Vector4f(worldCoordinates.x, worldCoordinates.y, 0.0f, 1.0f);
        Matrix4f viewMatrix = new Matrix4f(camera.getViewMatrix());
        Matrix4f projectionMatrix = new Matrix4f(camera.getProjectionMatrix());
        normalizeDeviceCoordinates.mul(projectionMatrix.mul(viewMatrix));
        Vector2f windowSpace = new Vector2f(normalizeDeviceCoordinates.x, normalizeDeviceCoordinates.y).mul(1.0f / normalizeDeviceCoordinates.w);
        windowSpace.add(new Vector2f(1.0f)).div(2.0f);
        windowSpace.mul(new Vector2f(Window.getScreenWidth(), Window.getScreenHeight()));
        return windowSpace;
    }

    protected static float getMouseX() { return (float) mousePosX; }

    protected static float getMouseY() { return (float) mousePosY; }

    protected static Vector2f getScreen() {
        float currentX = getMouseX() - gameViewportPos.x;
        currentX = (currentX / gameViewportSize.x) * Window.getScreenWidth();
        float currentY = getMouseY() - gameViewportPos.y;
        currentY = Window.getScreenHeight() - ((currentY / gameViewportSize.y) * Window.getScreenHeight());
        return new Vector2f(currentX, currentY);
    }

    protected static float getScreenX() { return getScreen().x; }

    protected static float getScreenY() { return getScreen().y; }

    private static void calculateOrthographicPos() {
        float currentX = getMouseX() - gameViewportPos.x;
        float currentY = getMouseY() - gameViewportPos.y;

        currentX = (currentX / gameViewportSize.x) * 2.0f - 1.0f;
        currentY = -((currentY / gameViewportSize.y) * 2.0f - 1.0f);

        Vector4f tmp = new Vector4f(currentX, currentY, 0.0f, 1.0f);

        ed_EditorCamera camera = SceneManager.getCurrentScene().getEditorCamera();
        Matrix4f viewProjection = new Matrix4f();
        camera.getInverseViewMatrix().mul(camera.getInverseProjectionMatrix(), viewProjection);
        tmp.mul(viewProjection);

        mouse2DWorldPosX = tmp.x;
        mouse2DWorldPosY = tmp.y;
    }

    protected static float getMouseDeltaX() { return (float) (lastMousePosX - mousePosX); }

    protected static float getMouseDeltaY() { return (float) (lastMousePosY - mousePosY); }

    protected static Vector2f get2DWorldPos() { return new Vector2f((float) mouse2DWorldPosX, (float) mouse2DWorldPosY); }

    protected static float get2DWorldPosX() { return (float) mouse2DWorldPosX; }

    protected static float get2DWorldPosY() { return (float) mouse2DWorldPosY; }

    protected static float getMouse2DWorldDeltaPosX() { return (float) (lastMouse2DWorldPosX - mouse2DWorldPosX); }

    protected static float getMouse2DWorldDeltaPosY() { return (float) (lastMouse2DWorldPosY - mouse2DWorldPosY); }

    protected static float getScrollX() { return (float) scrollX; }

    protected static float getScrollY() { return (float) scrollY; }

    protected static boolean isButtonDown(int button) {
        if (button > MOUSE_BUTTONS_COUNT)
            throw new IndexOutOfBoundsException(String.format("'%d' - button out of range.", button));
        return buttonsDown[button];
    }

    protected static boolean isButtonClick(int button) {
        if (button > MOUSE_BUTTONS_COUNT)
            throw new IndexOutOfBoundsException(String.format("'%d' - button out of range.", button));
        return buttonsClick[button];
    }

    protected static boolean isAnyButtonDown() {
        for (boolean button : buttonsDown)
            if (button)
                return true;
        return false;
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
