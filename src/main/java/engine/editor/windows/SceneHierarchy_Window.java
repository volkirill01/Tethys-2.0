package engine.editor.windows;

import engine.assets.AssetPool;
import engine.editor.gui.CustomImGuiWindowFlags;
import engine.editor.gui.EditorGUI;
import engine.editor.gui.EditorGuiWindow;
import engine.editor.gui.EditorThemeSystem;
import engine.entity.GameObject;
import engine.entity.GameObject_Manager;
import engine.renderer.camera.Camera;
import engine.renderer.renderer2D.ShapeRenderer;
import engine.renderer.renderer2D.SpriteRenderer;
import engine.renderer.renderer3D.MeshRenderer;
import engine.scenes.SceneManager;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.*;
import org.joml.Vector3f;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SceneHierarchy_Window extends EditorGuiWindow {

    private String searchFilter = "";
    //          Filepath    Name
    private static final Map<String,     String> defaultMeshes = new HashMap<>();

    static {
        File defaultMeshesDirectory = new File("Resources/meshes/defaultMeshes");
        for (File file : defaultMeshesDirectory.listFiles())
            defaultMeshes.put(file.getAbsolutePath(), file.getName().replace("default", "").replace(".obj", ""));
    }

    public SceneHierarchy_Window() { super("\uEF74 Hierarchy", ImGuiWindowFlags.None, CustomImGuiWindowFlags.NoWindowPadding);}

    @Override
    public void drawWindow() {
        if (ImGui.isMouseClicked(ImGuiMouseButton.Left) && ImGui.isWindowHovered())
            Outliner_Window.clearSelected();

        //<editor-fold desc="Search Field">
        ImGui.setCursorPos(ImGui.getCursorStartPosX() + ImGui.getStyle().getWindowPaddingX() / 2, ImGui.getCursorStartPosY() + ImGui.getStyle().getWindowPaddingY() / 2);
        this.searchFilter = EditorGUI.field_TextNoLabel("SceneHierarchy_SearchField", this.searchFilter, "Search...", '\uED1B', ImGui.getContentRegionAvailX() - ImGui.getFrameHeight() - ImGui.getStyle().getItemSpacingX() - ImGui.getStyle().getWindowPaddingX() / 2);
        //</editor-fold>

        //<editor-fold desc="Add Button">
        if (ImGui.button("+", ImGui.getFrameHeight(), ImGui.getFrameHeight()))
            ImGui.openPopup("SceneHierarchy_AddPopup");
        ImGui.setCursorPosY(ImGui.getCursorPosY() + ImGui.getStyle().getItemSpacingY());

        if (ImGui.beginPopup("SceneHierarchy_AddPopup")) {
            drawCreateObjectPopup(new Vector3f(0.0f));
            ImGui.endPopup();
        }
        //</editor-fold>

        //<editor-fold desc="GameObjects List">
        List<GameObject> gameObjects = SceneManager.getCurrentScene().getAllGameObjects();
        int index = 0;
        for (GameObject obj : gameObjects) {
            if (!obj.isDoSerialization())
                continue;

            if (!this.searchFilter.equals("") && !obj.getName().contains(this.searchFilter))
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
        //</editor-fold>

        // Right-click on blank space(Window)
        if (ImGui.beginPopupContextWindow(ImGuiPopupFlags.NoOpenOverItems | ImGuiPopupFlags.MouseButtonDefault)) {
            drawWindowContextPopup(new Vector3f(0.0f));
            ImGui.endPopup();
        }
    }

    private boolean drawTreeNode(GameObject obj, int index) {
        //<editor-fold desc="Header">
        float alpha = index % 2 == 0.0f ? 0.02f : 0.04f;
        float alphaHovered = index % 2 == 0.0f ? 0.07f : 0.09f;

        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingX() / 2, ImGui.getStyle().getFramePaddingY() / 2);
        ImGui.pushStyleColor(ImGuiCol.Header, 1.0f, 1.0f, 1.0f, alpha);
        ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 1.0f, 1.0f, 1.0f, alphaHovered);
        ImGui.pushStyleColor(ImGuiCol.HeaderActive, 1.0f, 1.0f, 1.0f, alphaHovered);
        ImGui.pushStyleColor(ImGuiCol.Text, ImGui.getStyle().getColor(ImGuiCol.Text).x, ImGui.getStyle().getColor(ImGuiCol.Text).y, ImGui.getStyle().getColor(ImGuiCol.Text).z, ImGui.getStyle().getColor(ImGuiCol.Text).w);
        if (Outliner_Window.getActiveGameObjects().contains(obj)) {
            ImGui.popStyleColor(4);

            alpha = index % 2 == 0.0f ? 0.6f : 0.7f;
            alphaHovered = index % 2 == 0.0f ? 0.76f : 0.86f;
            ImVec4 selectedObjectColor = new ImVec4(EditorThemeSystem.selectionColor.r / 255.0f, EditorThemeSystem.selectionColor.g / 255.0f, EditorThemeSystem.selectionColor.b / 255.0f, EditorThemeSystem.selectionColor.a / 255.0f);
            ImGui.pushStyleColor(ImGuiCol.Header, selectedObjectColor.x, selectedObjectColor.y, selectedObjectColor.z, alpha);
            ImGui.pushStyleColor(ImGuiCol.HeaderHovered, selectedObjectColor.x, selectedObjectColor.y, selectedObjectColor.z, alphaHovered);
            ImGui.pushStyleColor(ImGuiCol.HeaderActive, selectedObjectColor.x, selectedObjectColor.y, selectedObjectColor.z, alphaHovered);
            ImGui.pushStyleColor(ImGuiCol.Text, EditorThemeSystem.textColor_Opposite.r / 255.0f, EditorThemeSystem.textColor_Opposite.g / 255.0f, EditorThemeSystem.textColor_Opposite.b / 255.0f, EditorThemeSystem.textColor_Opposite.a / 255.0f);
        }
        boolean treeNodeOpen = ImGui.treeNodeEx("" + obj, ImGuiTreeNodeFlags.Selected | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.SpanAvailWidth | (obj.hasChildren() ? ImGuiTreeNodeFlags.OpenOnArrow : ImGuiTreeNodeFlags.Leaf), obj.getName());
        if (ImGui.isItemClicked())
            Outliner_Window.setActiveGameObject(obj);
        ImGui.popStyleColor(4);
        ImGui.popStyleVar();

        ImGui.setCursorPosY(ImGui.getCursorPosY() - ImGui.getStyle().getItemSpacingY());
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

    public static void drawWindowContextPopup(Vector3f createdObjectPosition) {
        drawCreateObjectPopup(createdObjectPosition);
    }

    private static void drawObjectContextPopup(GameObject obj) {
        if (ImGui.selectable("Delete"))
            obj.destroy();

        ImGui.separator();
        drawCreateObjectPopup(new Vector3f(0.0f));
    }

    private static void drawCreateObjectPopup(Vector3f createdObjectPosition) {
        if (ImGui.menuItem("Create Empty"))
            GameObject_Manager.createEmpty("Empty", true);

        ImGui.separator();
        if (ImGui.beginMenu("Create 2D")) {
            if (ImGui.menuItem("Create Sprite")) {
                GameObject empty = GameObject_Manager.createEmpty("Sprite", createdObjectPosition, true);
                empty.addComponent(new SpriteRenderer());
            }
            if (ImGui.menuItem("Create Shape")) {
                GameObject empty = GameObject_Manager.createEmpty("Shape", createdObjectPosition, true);
                empty.addComponent(new ShapeRenderer());
            }
            ImGui.endMenu();
        }

        if (ImGui.beginMenu("Create 3D")) {
            if (ImGui.menuItem("Create Mesh")) {
                GameObject empty = GameObject_Manager.createEmpty("Mesh", createdObjectPosition, true);
                empty.addComponent(new MeshRenderer());
            }

            ImGui.separator();
            for (String filepath : defaultMeshes.keySet()) {
                if (ImGui.menuItem("Create " + defaultMeshes.get(filepath))) {
                    GameObject empty = GameObject_Manager.createEmpty(defaultMeshes.get(filepath), createdObjectPosition, true);
                    empty.addComponent(new MeshRenderer());
                    empty.getComponent(MeshRenderer.class).setMesh(AssetPool.getMesh(filepath));
                }
            }
            ImGui.endMenu();
        }

        ImGui.separator();
        if (ImGui.menuItem("Create Camera")) {
            GameObject empty = GameObject_Manager.createEmpty("Camera", true);
            empty.addComponent(new Camera());
        }
    }
}
