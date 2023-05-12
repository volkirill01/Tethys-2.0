package engine.editor.windows;

import engine.editor.gui.EditorThemeSystem;
import imgui.ImGui;

public class ThemeChanger_Window {

    public static void imgui() {
        ImGui.begin("Theme Changer");
        if (ImGui.button("Dark"))
            EditorThemeSystem.setDarkTheme();
        if (ImGui.button("Light"))
            EditorThemeSystem.setLightTheme();
        ImGui.end();
    }
}
