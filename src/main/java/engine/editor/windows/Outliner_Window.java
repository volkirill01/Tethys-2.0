package engine.editor.windows;

import engine.editor.gui.CustomImGuiWindowFlags;
import engine.editor.gui.EditorGuiWindow;
import engine.entity.GameObject;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

import java.util.ArrayList;
import java.util.List;

public class Outliner_Window extends EditorGuiWindow {

    protected static final List<GameObject> activeGameObjects = new ArrayList<>();
    protected static GameObject activeGameObject = null;

    public Outliner_Window() { super("\uEF4E Outliner", ImGuiWindowFlags.None, CustomImGuiWindowFlags.NoWindowPadding); }

    @Override
    public void drawWindow() {
        if (activeGameObjects.size() == 1 && activeGameObjects.get(0) != null) {
            activeGameObject = activeGameObjects.get(0);
            activeGameObject.imgui();
        } else {
            ImGui.setCursorPos(ImGui.getCursorStartPosX() + ImGui.getContentRegionMaxX() / 2 - ImGui.calcTextSize("No selected Object").x / 2, ImGui.getCursorPosY() + ImGui.getStyle().getItemSpacingY() * 4);
            ImGui.text("No selected Object");
        }
    }

    public static GameObject getActiveGameObject() { return activeGameObjects.size() == 1 ? activeGameObjects.get(0) : null; }

    public static List<GameObject> getActiveGameObjects() { return activeGameObjects; }

    public static void clearSelected() { activeGameObjects.clear(); }

    public static void setActiveGameObject(GameObject obj) {
        if (obj != null) {
            clearSelected();
            activeGameObjects.add(obj);
        }
    }

    public static void addActiveGameObject(GameObject obj) { activeGameObjects.add(obj); }

    public static void removeActiveGameObject(GameObject obj) { activeGameObjects.remove(obj); }
}
