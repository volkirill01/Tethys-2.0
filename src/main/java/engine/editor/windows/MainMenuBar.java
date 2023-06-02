package engine.editor.windows;

import engine.editor.gui.EditorThemeSystem;
import engine.editor.gui.EngineGuiLayer;
import engine.observers.EventSystem;
import engine.observers.events.Event;
import engine.observers.events.EventType;
import engine.profiling.Profiler_Window;
import engine.renderer.camera.ed_BaseCamera;
import engine.renderer.debug.DebugRenderer;
import engine.scenes.SceneManager;
import engine.stuff.Window;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.*;

import java.util.Map;

public class MainMenuBar {

    private static void drawEditorMenuBar() {
        //<editor-fold desc="Camera Mode Button">
        ImGui.setCursorPosX(ImGui.getCursorStartPosX() + ImGui.getStyle().getFramePaddingX() + ImGui.getContentRegionMaxX() / 8);
        if (drawRectangleButton(SceneManager.getCurrentScene().getEditorCamera().getProjectionType().name(), true)) {
            if (SceneManager.getCurrentScene().getEditorCamera().getProjectionType() == ed_BaseCamera.ProjectionType.Perspective)
                SceneManager.getCurrentScene().getEditorCamera().setProjectionType(ed_BaseCamera.ProjectionType.Orthographic);
            else
                SceneManager.getCurrentScene().getEditorCamera().setProjectionType(ed_BaseCamera.ProjectionType.Perspective);
        }
        //</editor-fold>

        ImGui.sameLine();
        ImGui.setCursorPosY(ImGui.getCursorStartPosY());
        drawPlayPauseButtons();

        drawDebugDrawOptionsButton();
    }

    private static void drawWindowMenuBar() {
        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("Save Scene"))
                EventSystem.notify(new Event(EventType.Engine_SaveScene));
            if (ImGui.menuItem("Save Scene As"))
                EventSystem.notify(new Event(EventType.Engine_SaveSceneAs));
            if (ImGui.menuItem("Open Scene"))
                EventSystem.notify(new Event(EventType.Engine_OpenScene));
            if (ImGui.menuItem("Reload Scene"))
                EventSystem.notify(new Event(EventType.Engine_ReloadScene));
            ImGui.endMenu();
        }
        if (ImGui.beginMenu("Windows")) {
            if (ImGui.menuItem("Open Profiler"))
                EngineGuiLayer.setWindowOpen(Profiler_Window.class, true);
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

    private static void drawPlayPauseButtons() {
        boolean isPlayButtonEnabled = !Window.isRuntimePlaying();
        boolean isPauseButtonEnabled = !Window.isRuntimePause();
        boolean isNextFrameButtonEnabled = Window.isRuntimePause() && Window.isRuntimePlaying();

//        ImGui.getWindowDrawList().addRectFilled(                                          // Debug line in center of menu bar
//                ImGui.getCursorStartPosX() + ImGui.getContentRegionMaxX() / 2.0f - 0.5f,
//                ImGui.getCursorStartPosY(),
//                ImGui.getCursorStartPosX() + ImGui.getContentRegionMaxX() / 2.0f + 1.0f,
//                ImGui.getCursorStartPosY() + 50.0f,
//                ImGui.getColorU32(1.0f, 0.0f, 0.0f, 1.0f)
//        );

        //<editor-fold desc="Play Button">
        ImGui.setCursorPosX(ImGui.getCursorStartPosX() + (ImGui.getContentRegionMaxX() / 2) - (ImGui.getFrameHeight() * 3 + ImGui.getStyle().getItemInnerSpacingX() * 2) / 2 + 8.0f); // TODO REPLACE THIS + 8.0f VALUE TO IMGUI VALUE
        ImVec4 playButtonColor = isPlayButtonEnabled ? new ImVec4(EditorThemeSystem.activeColor.r / 255.0f, EditorThemeSystem.activeColor.g / 255.0f, EditorThemeSystem.activeColor.b / 255.0f, EditorThemeSystem.activeColor.a / 255.0f) : new ImVec4(0.889f, 0.191f, 0.062f, 1.0f);
        if (drawSquareButton(isPlayButtonEnabled ? "\uEC74" : "\uEFFC", true, playButtonColor))
            EventSystem.notify(new Event(isPlayButtonEnabled ? EventType.Engine_StartPlay : EventType.Engine_StopPlay));
        //</editor-fold>

        //<editor-fold desc="Pause Button">
        ImGui.setCursorPosX(ImGui.getCursorPosX() - ImGui.getStyle().getItemSpacingX() + ImGui.getStyle().getItemInnerSpacingX());
        ImVec4 pauseButtonColor = isPauseButtonEnabled ? ImGui.getStyle().getColor(ImGuiCol.Text) : ImGui.getStyle().getColor(ImGuiCol.TextDisabled);
        if (drawSquareButton("\uEC72", true, pauseButtonColor))
            EventSystem.notify(new Event(Window.isRuntimePause() ? EventType.Engine_Play : EventType.Engine_Pause));
        //</editor-fold>

        //<editor-fold desc="NextFrame Button">
        if (!isNextFrameButtonEnabled)
            ImGui.beginDisabled();
        ImGui.setCursorPosX(ImGui.getCursorPosX() - ImGui.getStyle().getItemSpacingX() + ImGui.getStyle().getItemInnerSpacingX());
        ImVec4 nextFrameButtonColor = ImGui.getStyle().getColor(ImGuiCol.Text);
        if (drawSquareButton("\uEC6E", isNextFrameButtonEnabled, nextFrameButtonColor))
            EventSystem.notify(new Event(EventType.Engine_NextFrame));
        if (!isNextFrameButtonEnabled)
            ImGui.endDisabled();
        //</editor-fold>
    }

    private static void drawDebugDrawOptionsButton() {
        float buttonWidth = ImGui.calcTextSize("Debug Drawing").x + ImGui.getStyle().getFramePaddingX() * 2;

        ImGui.setCursorPosX(ImGui.getCursorStartPosX() + ImGui.getWindowSizeX() - (ImGui.getContentRegionMaxX() / 3) - buttonWidth / 2 + 8.0f); // TODO REPLACE THIS + 8.0f VALUE TO IMGUI VALUE
        if (beginComboButton("Debug Drawing")) {
            Map<String, Boolean> debugDrawOptions = DebugRenderer.getDebugDrawOptions();

            for (String key : debugDrawOptions.keySet()) {
                ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingX() / 2, ImGui.getStyle().getFramePaddingY() / 2);
                if (key.split("/").length == 1) {
                    if (ImGui.checkbox(key, debugDrawOptions.get(key)))
                        debugDrawOptions.replace(key, !debugDrawOptions.get(key));
                    ImGui.popStyleVar();
                } else {
                    if (!debugDrawOptions.get(key.split("/")[0]))
                        ImGui.beginDisabled();

                    ImGui.indent();
                    if (ImGui.checkbox(key.split("/")[1], debugDrawOptions.get(key)))
                        debugDrawOptions.replace(key, !debugDrawOptions.get(key));
                    ImGui.unindent();
                    ImGui.popStyleVar();

                    if (!debugDrawOptions.get(key.split("/")[0]))
                        ImGui.endDisabled();
                }
            }
            endComboButton();
        }
    }

    private static boolean drawSquareButton(String text, boolean isEnabled, ImVec4 textColor) {
        boolean isClick = false;

        float buttonSize = ImGui.getFrameHeight() / 1.2f;
        float startPosY = ImGui.getCursorPosY();
        ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY() + (ImGui.getFrameHeight() - buttonSize) / 2);

        //<editor-fold desc="Button">
        if (isEnabled)
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

    private static boolean drawRectangleButton(String text, boolean isEnabled) {
        boolean isClick = false;

        ImGui.pushStyleColor(ImGuiCol.Border, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.Text, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.setCursorPosY(ImGui.getCursorStartPosY() + ImGui.getStyle().getFramePaddingY() / 2);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingX(), ImGui.getStyle().getFramePaddingY() / 1.7f);
        ImVec2 startCursorPos = ImGui.getCursorPos();
        if (!isEnabled)
            ImGui.beginDisabled();
        if (ImGui.button(text))
            isClick = true;
        if (!isEnabled)
            ImGui.endDisabled();
        ImGui.setCursorPosY(ImGui.getCursorStartPosY());
        ImGui.setCursorPos(startCursorPos.x + ImGui.getStyle().getFramePaddingX(), startCursorPos.y + ImGui.getStyle().getFramePaddingY() / 2);
        ImGui.popStyleVar();
        ImGui.popStyleColor(2);
        ImGui.text(text);

        return isClick;
    }

    private static boolean beginComboButton(String label) {
        boolean isOpen = false;

        ImVec4 tmp = ImGui.getStyle().getColor(ImGuiCol.Button);
        ImGui.pushStyleColor(ImGuiCol.FrameBg, tmp.x, tmp.y, tmp.z, tmp.w);
        tmp = ImGui.getStyle().getColor(ImGuiCol.ButtonHovered);
        ImGui.pushStyleColor(ImGuiCol.FrameBgHovered, tmp.x, tmp.y, tmp.z, tmp.w);
        tmp = ImGui.getStyle().getColor(ImGuiCol.ButtonActive);
        ImGui.pushStyleColor(ImGuiCol.FrameBgActive, tmp.x, tmp.y, tmp.z, tmp.w);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0.0f);
        ImGui.setCursorPosY(ImGui.getCursorStartPosY() + ImGui.getStyle().getFramePaddingY() / 2);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingX(), ImGui.getStyle().getFramePaddingY() / 1.7f);

        ImGui.setNextItemWidth(ImGui.calcTextSize(label).x + ImGui.getStyle().getFramePaddingX() * 2 + ImGui.getFrameHeight());
        ImVec2 startCursorPos = ImGui.getCursorScreenPos(); // TODO FIX PADDING OF DROPDOWN BUTTON
        if (ImGui.beginCombo("##" + label, ""))
            isOpen = true;

        ImGui.getForegroundDrawList().addText(
                startCursorPos.x + ImGui.getStyle().getFramePaddingX(),
                startCursorPos.y + ImGui.getStyle().getFramePaddingY() / 2,
                ImGui.getColorU32(ImGuiCol.Text),
                label
        );

        ImGui.setCursorPosY(ImGui.getCursorStartPosY());
        ImGui.popStyleVar(2);
        ImGui.popStyleColor(3);

        return isOpen;
    }
    private static void endComboButton() { ImGui.endCombo(); }
}
