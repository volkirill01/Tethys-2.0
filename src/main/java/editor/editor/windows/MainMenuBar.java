package editor.editor.windows;

import editor.editor.gui.EditorThemeSystem;
import editor.observers.EventSystem;
import editor.observers.events.Event;
import editor.observers.events.EventType;
import editor.stuff.Window;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.*;

public class MainMenuBar {

    public static float imgui() {
        int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoTitleBar |
                ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

        float menuBarHeight = ImGui.getFrameHeight() * 2.0f;

        ImGui.setNextWindowPos(0.0f, 0.0f, ImGuiCond.Always);
        ImGui.setNextWindowSize(Window.getWidth(), menuBarHeight); // Set MenuBar window size
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
        ImGui.begin("MainMenuBar", windowFlags);
        ImGui.popStyleVar(3);

        drawEditorMenuBar();

        ImGui.beginMenuBar();
        drawWindowMenuBar();
        ImGui.endMenuBar();

        ImGui.end();
        return menuBarHeight;
    }

    private static void drawEditorMenuBar() {
        boolean isPlayButtonDisabled = Window.isRuntimePlaying();
        boolean isStopButtonDisabled = !Window.isRuntimePlaying();

        ImGui.setCursorPosX(ImGui.getCursorStartPosX() + (ImGui.getContentRegionMaxX() / 2) - (ImGui.getFrameHeight() * 2));
        if (isPlayButtonDisabled)
            ImGui.beginDisabled();
        if (drawSquareButton("\uEC74", isPlayButtonDisabled, isPlayButtonDisabled ? ImGui.getStyle().getColor(ImGuiCol.Text) : EditorThemeSystem.activeColor))
            EventSystem.notify(null, new Event(EventType.GameEngine_StartPlay));
        if (isPlayButtonDisabled)
            ImGui.endDisabled();

        if (isStopButtonDisabled)
            ImGui.beginDisabled();
        if (drawSquareButton("\uEFFC", isStopButtonDisabled, isStopButtonDisabled ? ImGui.getStyle().getColor(ImGuiCol.Text) : new ImVec4(0.889f, 0.191f, 0.062f, 1.0f)))
            EventSystem.notify(null, new Event(EventType.GameEngine_StopPlay));
        if (isStopButtonDisabled)
            ImGui.endDisabled();
    }
    private static boolean drawSquareButton(String text, boolean isDisabled, ImVec4 textColor) {
        boolean isClick = false;

        float buttonSize = ImGui.getFrameHeight() / 1.2f;
        float startPosY = ImGui.getCursorPosY();
        ImGui.setCursorPosY(ImGui.getCursorPosY() + (ImGui.getFrameHeight() - buttonSize) / 2);

        if (!isDisabled)
            if (ImGui.isMouseHoveringRect(ImGui.getCursorScreenPosX(), ImGui.getCursorScreenPosY(), ImGui.getCursorScreenPosX() + buttonSize, ImGui.getCursorScreenPosY() + buttonSize)) {
                int currentColor = ImGui.getColorU32(ImGuiCol.ButtonHovered);

                if (ImGui.isMouseDown(ImGuiMouseButton.Left))
                    currentColor = ImGui.getColorU32(ImGuiCol.ButtonActive);
                if (ImGui.isMouseReleased(ImGuiMouseButton.Left))
                    isClick = true;

                ImGui.getWindowDrawList().addRectFilled(
                        ImGui.getCursorScreenPosX(),
                        ImGui.getCursorScreenPosY(),
                        ImGui.getCursorScreenPosX() + buttonSize,
                        ImGui.getCursorScreenPosY() + buttonSize,
                        currentColor,
                        ImGui.getStyle().getFrameRounding()
                );
            }

        ImGui.setCursorPos(ImGui.getCursorPosX() + ImGui.getStyle().getFramePaddingX(), ImGui.getCursorPosY() + ImGui.getStyle().getFramePaddingY() / 2);
        ImGui.textColored(textColor.x, textColor.y, textColor.z, textColor.w, text);
        ImGui.sameLine();
        ImGui.setCursorPos(ImGui.getCursorPosX() + ImGui.getStyle().getFramePaddingX(), startPosY);

        return isClick;
    }

    private static void drawWindowMenuBar() {
        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("Save"))
                EventSystem.notify(null, new Event(EventType.GameEngine_SaveScene));
            if (ImGui.menuItem("Reload"))
                EventSystem.notify(null, new Event(EventType.GameEngine_ReloadScene));
            ImGui.endMenu();
        }
    }
}
