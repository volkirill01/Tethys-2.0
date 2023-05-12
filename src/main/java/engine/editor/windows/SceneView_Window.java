package engine.editor.windows;

import engine.editor.gui.EditorImGuiWindow;
import engine.editor.gui.ImGuiLayer;
import engine.entity.GameObject;
import engine.eventListeners.Input;
import engine.eventListeners.MouseListener;
import engine.observers.EventSystem;
import engine.observers.Observer;
import engine.observers.events.Event;
import engine.observers.events.EventType;
import engine.stuff.Window;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;

public class SceneView_Window extends EditorImGuiWindow implements Observer {

    private static float leftX, rightX, topY, bottomY;

    public SceneView_Window() {
        super("\uF02C Scene", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);
        EventSystem.addObserver(this);
    }

    @Override
    public void drawWindow() {
        ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY());
        ImVec2 viewportSize = getLargestSizeForViewport();
        ImVec2 viewportPos = getCenteredPositionForViewport(viewportSize);
        ImGui.setCursorPos(viewportPos.x, viewportPos.y);

        ImVec2 topLeft = ImGui.getCursorScreenPos();
        leftX = topLeft.x;
        bottomY = topLeft.y;
        rightX = topLeft.x + viewportSize.x;
        topY = topLeft.y + viewportSize.y;

        int textureID = Window.getScreenFramebuffer().getColorTexture();
        ImVec2 start = new ImVec2();
        ImGui.getCursorPos(start);
        ImGui.image(textureID, viewportSize.x, viewportSize.y, 0, 1, 1, 0);

        MouseListener.setGameViewportPos(topLeft);
        MouseListener.setGameViewportSize(viewportSize);
    }

    private ImVec2 getLargestSizeForViewport() {
        ImVec2 viewportSize = ImGui.getContentRegionAvail();

        float aspectWidth = viewportSize.x;
        float aspectHeight = aspectWidth / Window.getTargetAspectRatio();

        if (aspectHeight > viewportSize.y) {
            // We must switch to pillar box mode (black bars on left and right sides of screen)
            aspectHeight = viewportSize.y;
            aspectWidth = aspectHeight * Window.getTargetAspectRatio();
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 viewportSize = ImGui.getContentRegionAvail();

        float viewportPositionX = (viewportSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportPositionY = (viewportSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewportPositionX + ImGui.getCursorPosX(), viewportPositionY + ImGui.getCursorPosY());
    }

    public boolean getWantCaptureMouse() {
        if (!this.isVisible() || !this.isHover())
            return false;

        return Input.getMousePositionX() >= leftX && Input.getMousePositionX() <= rightX &&
                Input.getMousePositionY() >= bottomY && Input.getMousePositionY() <= topY;
    }

    @Override
    public void onNotify(GameObject object, Event event) {
        if (event.type == EventType.GameEngine_StopPlay)
            ImGuiLayer.selectWindow(this);
    }
}
