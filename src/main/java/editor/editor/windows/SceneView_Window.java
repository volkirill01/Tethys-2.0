package editor.editor.windows;

import editor.TestFieldsWindow;
import editor.eventListeners.Input;
import editor.eventListeners.MouseListener;
import editor.stuff.Window;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;

public class SceneView_Window {

    private static float leftX, rightX, topY, bottomY;

    public void imgui() {
        ImGui.begin("Scene View Window", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

        ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY());
        ImVec2 viewportSize = getLargestSizeForViewport();
        ImVec2 viewportPos = getCenteredPositionForViewport(viewportSize);
        ImGui.setCursorPos(viewportPos.x, viewportPos.y);

        ImVec2 topLeft = new ImVec2();
        ImGui.getCursorScreenPos(topLeft);
        leftX = topLeft.x;
        bottomY = topLeft.y;
        rightX = topLeft.x + viewportSize.x;
        topY = topLeft.y + viewportSize.y;

        int textureID = Window.getFramebuffer().getTextureID();
        ImVec2 start = new ImVec2();
        ImGui.getCursorPos(start);
        ImGui.image(textureID, viewportSize.x, viewportSize.y, 0, 1, 1, 0);

        MouseListener.setGameViewportPos(topLeft);
        MouseListener.setGameViewportSize(viewportSize);

        ImGui.end();
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

    public static boolean getWantCaptureMouse() {
        return Input.getMousePositionX() >= leftX && Input.getMousePositionX() <= rightX &&
                Input.getMousePositionY() >= bottomY && Input.getMousePositionY() <= topY;
    }
}
