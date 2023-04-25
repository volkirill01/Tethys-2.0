package editor.editor.windows;

import editor.observers.EventSystem;
import editor.observers.events.Event;
import editor.observers.events.EventType;
import editor.stuff.Window;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

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

        if (isPlayButtonDisabled)
            ImGui.beginDisabled();
        if (ImGui.button("Play"))
            EventSystem.notify(null, new Event(EventType.GameEngine_StartPlay));
        if (isPlayButtonDisabled)
            ImGui.endDisabled();

        ImGui.sameLine();
        if (isStopButtonDisabled)
            ImGui.beginDisabled();
        if (ImGui.button("Stop"))
            EventSystem.notify(null, new Event(EventType.GameEngine_StopPlay));
        if (isStopButtonDisabled)
            ImGui.endDisabled();
    }

    private static void drawWindowMenuBar() {
        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("Save"))
                EventSystem.notify(null, new Event(EventType.GameEngine_SaveScene));
            if (ImGui.menuItem("Load"))
                EventSystem.notify(null, new Event(EventType.GameEngine_LoadScene));
            ImGui.endMenu();
        }
    }
}
