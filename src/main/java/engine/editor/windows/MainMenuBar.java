package engine.editor.windows;

import engine.editor.gui.EditorThemeSystem;
import engine.editor.gui.ImGuiLayer;
import engine.observers.EventSystem;
import engine.observers.events.Event;
import engine.observers.events.EventType;
import engine.profiling.Profiler_Window;
import engine.renderer.camera.ed_BaseCamera;
import engine.scenes.SceneManager;
import engine.stuff.Window;
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
        ImGui.pushStyleColor(ImGuiCol.WindowBg, ImGui.getColorU32(ImGuiCol.FrameBg));
        ImGui.begin("MainMenuBar", windowFlags);
        ImGui.popStyleVar(3);
        ImGui.popStyleColor();

        drawEditorMenuBar();

        ImGui.beginMenuBar();
        drawWindowMenuBar();
        ImGui.endMenuBar();

        ImGui.end();
        return menuBarHeight;
    }

    private static void drawEditorMenuBar() {
        //<editor-fold desc="Camera Mode Button">
        ImGui.pushStyleColor(ImGuiCol.Border, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingX(), ImGui.getStyle().getFramePaddingY() / 2.0f);
        ImGui.setCursorPos(ImGui.getCursorStartPosX() + ImGui.getStyle().getFramePaddingX() + ImGui.getContentRegionMaxX() / 8, ImGui.getCursorStartPosY() + ImGui.getStyle().getFramePaddingY());
        if (ImGui.button(SceneManager.getCurrentScene().getEditorCamera().getCameraType().name())) {
            if (SceneManager.getCurrentScene().getEditorCamera().getCameraType() == ed_BaseCamera.CameraType.Perspective)
                SceneManager.getCurrentScene().getEditorCamera().setCameraType(ed_BaseCamera.CameraType.Orthographic);
            else
                SceneManager.getCurrentScene().getEditorCamera().setCameraType(ed_BaseCamera.CameraType.Perspective);
        }
        ImGui.setCursorPosY(ImGui.getCursorStartPosY());
        ImGui.popStyleVar();
        ImGui.popStyleColor();
        //</editor-fold>

        boolean isPlayButtonDisabled = Window.isRuntimePlaying();
        boolean isStopButtonDisabled = !Window.isRuntimePlaying();

        //<editor-fold desc="Play Button">
        ImGui.setCursorPosX(ImGui.getCursorStartPosX() + (ImGui.getContentRegionMaxX() / 2) - (ImGui.getFrameHeight() * 2) - ImGui.getStyle().getItemInnerSpacingX());
        if (isPlayButtonDisabled)
            ImGui.beginDisabled();
        ImVec4 playButtonColor = isPlayButtonDisabled ? ImGui.getStyle().getColor(ImGuiCol.Text) : new ImVec4(EditorThemeSystem.activeColor.r / 255.0f, EditorThemeSystem.activeColor.g / 255.0f, EditorThemeSystem.activeColor.b / 255.0f, EditorThemeSystem.activeColor.a / 255.0f);
        if (drawSquareButton("\uEC74", isPlayButtonDisabled, playButtonColor))
            EventSystem.notify(null, new Event(EventType.GameEngine_StartPlay));
        if (isPlayButtonDisabled)
            ImGui.endDisabled();
        //</editor-fold>

        //<editor-fold desc="Stop Button">
        ImGui.setCursorPosX(ImGui.getCursorPosX() - ImGui.getStyle().getItemSpacingX() + ImGui.getStyle().getItemInnerSpacingX() * 2);
        if (isStopButtonDisabled)
            ImGui.beginDisabled();
        if (drawSquareButton("\uEFFC", isStopButtonDisabled, isStopButtonDisabled ? ImGui.getStyle().getColor(ImGuiCol.Text) : new ImVec4(0.889f, 0.191f, 0.062f, 1.0f)))
            EventSystem.notify(null, new Event(EventType.GameEngine_StopPlay));
        if (isStopButtonDisabled)
            ImGui.endDisabled();
        //</editor-fold>
    }

    private static boolean drawSquareButton(String text, boolean isDisabled, ImVec4 textColor) {
        boolean isClick = false;

        float buttonSize = ImGui.getFrameHeight() / 1.2f;
        float startPosY = ImGui.getCursorPosY();
        ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY() + (ImGui.getFrameHeight() - buttonSize) / 2);

        //<editor-fold desc="Button">
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
        //</editor-fold>

        //<editor-fold desc="Button Text">
        ImGui.setCursorPos(ImGui.getCursorPosX() + ImGui.getStyle().getFramePaddingY() / 1.2f + 1.2f, ImGui.getCursorPosY() + ImGui.getStyle().getFramePaddingY() / 1.3f - 1.5f);
        ImGui.textColored(textColor.x, textColor.y, textColor.z, textColor.w, text);
        ImGui.sameLine();
        ImGui.setCursorPos(ImGui.getCursorPosX() + ImGui.getStyle().getFramePaddingY(), startPosY);
        //</editor-fold>

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
        if (ImGui.beginMenu("Windows")) {
            if (ImGui.menuItem("Open Profiler"))
                ImGuiLayer.setWindowOpen(Profiler_Window.class, true);
            ImGui.endMenu();
        }

        if (ImGui.beginMenu("Editor")) {
            if (ImGui.menuItem("Minimize"))
                Window.minimize();
            if (ImGui.menuItem("Maximize"))
                Window.maximize();
            if (ImGui.menuItem("Close"))
                Window.close();
            ImGui.endMenu();
        }
    }
}
