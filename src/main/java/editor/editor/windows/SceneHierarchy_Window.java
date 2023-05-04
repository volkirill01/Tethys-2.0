package editor.editor.windows;

import editor.editor.gui.EditorImGuiWindow;
import editor.entity.GameObject;
import editor.scenes.SceneManager;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImString;

import java.util.List;

public class SceneHierarchy_Window extends EditorImGuiWindow {

    public SceneHierarchy_Window() { super("\uEF74 Hierarchy");}

    @Override
    public void drawWindow() {
        List<GameObject> gameObjects = SceneManager.getCurrentScene().getAllGameObjects();
        int index = 0;
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0.0f, 0.0f);
        for (GameObject obj : gameObjects) {
            if (!obj.isDoSerialization())
                continue;

            ImGui.pushID("SceneHierarchy_GO_" + index);
            boolean treeNodeOpen = drawTreeNode(obj, index);
            // GameObject1
            // =========== // TODO ADD SEPARATORS
            // GameObject2
            ImGui.popID();

            if (treeNodeOpen)
                ImGui.treePop();
            index++;
        }
        ImGui.popStyleVar();
    }

    private boolean drawTreeNode(GameObject obj, int index) {
        float alpha = index % 2 == 0.0f ? 0.04f : 0.08f;
        float alphaHovered = index % 2 == 0.0f ? 0.1f : 0.13f;
        float alphaActive = index % 2 == 0.0f ? 0.12f : 0.15f;

        ImGui.pushStyleColor(ImGuiCol.Header, 1.0f, 1.0f, 1.0f, alpha);
        ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 1.0f, 1.0f, 1.0f, alphaHovered);
        ImGui.pushStyleColor(ImGuiCol.HeaderActive, 1.0f, 1.0f, 1.0f, alphaActive);
        boolean treeNodeOpen = ImGui.treeNodeEx(ImGui.getDragDropPayload() == null ? "##" + obj.name : obj.name, ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.Selected);
        ImGui.setItemAllowOverlap();
        ImGui.popStyleColor(3);

        if (ImGui.getDragDropPayload() == null) {
            ImGui.sameLine();
            ImString ImString = new ImString(obj.name, 256);
            ImGui.pushStyleColor(ImGuiCol.FrameBg, 0.0f, 0.0f, 0.0f, 0.0f);
            ImGui.pushStyleColor(ImGuiCol.FrameBgHovered, 0.0f, 0.0f, 0.0f, 0.0f);
            ImGui.pushStyleColor(ImGuiCol.FrameBgActive, 0.0f, 0.0f, 0.0f, 0.0f);
            ImGui.pushStyleColor(ImGuiCol.Border, 0.0f, 0.0f, 0.0f, 0.0f);
            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingX() * 2, ImGui.getStyle().getFramePaddingY());
            ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
            if (ImGui.getDragDropPayload() == null)
                if (ImGui.inputText("##SceneHierarchy_GameObject_" + index, ImString))
                    obj.name = ImString.get();
            ImGui.popStyleVar();
            ImGui.popStyleColor(4);
        }

        if (ImGui.beginDragDropSource()) {
            System.out.println("Begin DragDrop");
            ImGui.setDragDropPayload("GameObject", obj);
            ImGui.text(obj.name); // Preview of DragDrop
            ImGui.endDragDropSource();
        }

        if (ImGui.beginDragDropTarget()) {
            Object payloadObject = ImGui.acceptDragDropPayload("GameObject");

            if (payloadObject != null) {
                if (payloadObject.getClass().isAssignableFrom(GameObject.class)) {
                    GameObject gameObject = (GameObject) payloadObject;
                    System.out.println("Payload Accepted Drop '" + gameObject.name + "' to '" + obj.name + "'");
                }
            }
            ImGui.endDragDropTarget();
        }

        return treeNodeOpen;
    }
}
