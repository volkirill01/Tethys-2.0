package engine.editor.windows;

import engine.editor.gui.EditorGuiWindow;
import engine.entity.GameObject;
import engine.entity.GameObject_Manager;
import engine.scenes.SceneManager;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.*;

import java.util.List;

public class SceneHierarchy_Window extends EditorGuiWindow {

    public SceneHierarchy_Window() { super("\uEF74 Hierarchy");}

    @Override
    public void drawWindow() {
        if (ImGui.isMouseClicked(ImGuiMouseButton.Left) && ImGui.isWindowHovered())
            Outliner_Window.clearSelected();

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

        // Right-click on blank space(Window)
        if (ImGui.beginPopupContextWindow(ImGuiPopupFlags.NoOpenOverItems | ImGuiPopupFlags.MouseButtonDefault)) {
            drawWindowContextPopup();
            ImGui.endPopup();
        }
    }

    private boolean drawTreeNode(GameObject obj, int index) {
        //<editor-fold desc="Header">
        float alpha = index % 2 == 0.0f ? 0.02f : 0.04f;
        float alphaHovered = index % 2 == 0.0f ? 0.07f : 0.09f;

        ImGui.pushStyleColor(ImGuiCol.Header, 1.0f, 1.0f, 1.0f, alpha);
        ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 1.0f, 1.0f, 1.0f, alphaHovered);
        ImGui.pushStyleColor(ImGuiCol.HeaderActive, 1.0f, 1.0f, 1.0f, alphaHovered);
        if (Outliner_Window.getActiveGameObjects().contains(obj)) {
            ImGui.popStyleColor(3);

            alpha = index % 2 == 0.0f ? 0.12f : 0.17f;
            alphaHovered = index % 2 == 0.0f ? 0.22f : 0.27f;
            ImVec4 selectedObjectColor = ImGui.getStyle().getColor(ImGuiCol.DragDropTarget);
            ImGui.pushStyleColor(ImGuiCol.Header, selectedObjectColor.x, selectedObjectColor.y, selectedObjectColor.z, alpha);
            ImGui.pushStyleColor(ImGuiCol.HeaderHovered, selectedObjectColor.x, selectedObjectColor.y, selectedObjectColor.z, alphaHovered);
            ImGui.pushStyleColor(ImGuiCol.HeaderActive, selectedObjectColor.x, selectedObjectColor.y, selectedObjectColor.z, alphaHovered);
        }
        boolean treeNodeOpen = ImGui.treeNodeEx("" + obj, ImGuiTreeNodeFlags.Selected | ImGuiTreeNodeFlags.FramePadding  | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth, obj.getName());
        if (ImGui.isItemClicked())
            Outliner_Window.setActiveGameObject(obj);
        ImGui.popStyleColor(3);
        //</editor-fold>

        //<editor-fold desc="Drag-Drop Stuff">
//        if (ImGui.getDragDropPayload() == null) {
//            ImGui.sameLine();
//            ImString ImString = new ImString(obj.getName(), 256);
//            ImGui.pushStyleColor(ImGuiCol.FrameBg, 0.0f, 0.0f, 0.0f, 0.0f);
//            ImGui.pushStyleColor(ImGuiCol.FrameBgHovered, 0.0f, 0.0f, 0.0f, 0.0f);
//            ImGui.pushStyleColor(ImGuiCol.FrameBgActive, 0.0f, 0.0f, 0.0f, 0.0f);
//            ImGui.pushStyleColor(ImGuiCol.Border, 0.0f, 0.0f, 0.0f, 0.0f);
//            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingX() * 2, ImGui.getStyle().getFramePaddingY());
//            ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
//            if (ImGui.getDragDropPayload() == null)
//                if (ImGui.inputText("##SceneHierarchy_GameObject_" + index, ImString))
//                    obj.setName(ImString.get());
//            ImGui.popStyleVar();
//            ImGui.popStyleColor(4);
//        }

        if (ImGui.beginDragDropSource()) {
            System.out.println("On DragDrop");
            ImGui.setDragDropPayload("GameObject", obj);
            ImGui.text(obj.getName()); // Preview of DragDrop
            ImGui.endDragDropSource();
        }

        if (ImGui.beginDragDropTarget()) {
            Object payloadObject = ImGui.acceptDragDropPayload("GameObject");

            if (payloadObject != null) {
                if (payloadObject.getClass().isAssignableFrom(GameObject.class)) {
                    GameObject gameObject = (GameObject) payloadObject;
                    System.out.println("Payload Accepted Drop '" + gameObject.getName() + "' to '" + obj.getName() + "'");
                }
            }
            ImGui.endDragDropTarget();
        }
        //</editor-fold>

        // Right-click on GameObject(ImGui Item)
        if (ImGui.beginPopupContextItem("GameObject_Context_Popup_" + obj)) {
            drawObjectContextPopup(obj);
            ImGui.endPopup();
        }

        return treeNodeOpen;
    }

    private void drawWindowContextPopup() {
        if (ImGui.selectable("Create Empty"))
            GameObject_Manager.createEmpty("Empty", true);
    }

    private void drawObjectContextPopup(GameObject obj) {
        if (ImGui.selectable("Delete"))
            obj.destroy();
    }
}
