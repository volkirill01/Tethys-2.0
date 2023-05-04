package editor.editor.windows;

import editor.editor.gui.EditorImGuiWindow;
import editor.eventListeners.Input;
import editor.eventListeners.MouseListener;
import editor.renderer.camera.CameraType;
import editor.scenes.SceneManager;
import editor.stuff.Window;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;

public class SceneView_Window extends EditorImGuiWindow {

    private static float leftX, rightX, topY, bottomY;

    public SceneView_Window() { super("\uF02C Scene", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);}

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

        ImGui.sameLine();
        ImGui.setCursorPosX(ImGui.getCursorPosX() - ImGui.calcTextSize(SceneManager.getCurrentScene().getEditorCamera().getCameraType().name()).x - ImGui.getStyle().getFramePaddingX() * 2 - ImGui.getStyle().getItemSpacingX());
        if (ImGui.button(SceneManager.getCurrentScene().getEditorCamera().getCameraType().name())) {
            if (SceneManager.getCurrentScene().getEditorCamera().getCameraType() == CameraType.Perspective)
                SceneManager.getCurrentScene().getEditorCamera().setCameraType(CameraType.Orthographic);
            else
                SceneManager.getCurrentScene().getEditorCamera().setCameraType(CameraType.Perspective);
        }

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
        return Input.getMousePositionX() >= leftX && Input.getMousePositionX() <= rightX &&
                Input.getMousePositionY() >= bottomY && Input.getMousePositionY() <= topY;
    }
}
