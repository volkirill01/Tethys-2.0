package editor.editor.windows;

import editor.editor.gui.EditorThemeSystem;
import imgui.ImGui;

public class ThemeChanger_Window {

    public static void imgui() {
        if (ImGui.button("Dark"))
            EditorThemeSystem.changeTheme(EditorThemeSystem.darkTheme());
        if (ImGui.button("Light"))
            EditorThemeSystem.changeTheme(EditorThemeSystem.lightTheme());
    }
}
