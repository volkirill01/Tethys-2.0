package editor.editor.windows;

import editor.entity.GameObject;
import editor.scenes.SceneManager;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.List;

public class SceneHierarchy_Window {

    public void imgui() {
        ImGui.begin("Scene Hierarchy");

        List<GameObject> gameObjects = SceneManager.getCurrentScene().getAllGameObjects();
        int index = 0;
        for (GameObject obj : gameObjects) {
            if (!obj.isDoSerialization())
                continue;

            ImGui.pushID("SceneHierarchy_GO_" + index);
            boolean treeNodeOpen = drawTreeNode(obj, index);
            ImGui.popID();

            if (treeNodeOpen)
                ImGui.treePop();
            index++;
        }

        ImGui.end();
    }

    private boolean drawTreeNode(GameObject obj, int index) {
        boolean treeNodeOpen = ImGui.treeNodeEx(obj.name, ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth);

        if (ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload("GameObject", obj);
            ImGui.text(obj.name); // Preview of Drag drop
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
