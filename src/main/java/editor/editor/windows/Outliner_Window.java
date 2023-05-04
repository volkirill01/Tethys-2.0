package editor.editor.windows;

import editor.editor.gui.EditorImGuiWindow;
import editor.entity.GameObject;
import editor.physics.physics2D.components.RigidBody2D;
import editor.physics.physics2D.components.colliders.Box2DCollider;
import editor.physics.physics2D.components.colliders.Circle2DCollider;
import editor.physics.physics2D.components.colliders.Pillbox2DCollider;
import editor.renderer.camera.Camera;
import editor.renderer.renderer3D.MeshRenderer;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

public class Outliner_Window extends EditorImGuiWindow {

    protected static final List<GameObject> activeGameObjects = new ArrayList<>();
    protected static GameObject activeGameObject = null;

    public Outliner_Window() { super("\uEF4E Outliner"); }

    @Override
    public void drawWindow() {
        if (activeGameObjects.size() == 1 && activeGameObjects.get(0) != null) {
            activeGameObject = activeGameObjects.get(0);

            if (ImGui.beginPopupContextWindow("ComponentAdder")) {
                if (!activeGameObject.hasComponent(RigidBody2D.class))
                    if (ImGui.menuItem("Add RigidBody2D"))
                        activeGameObject.addComponent(new RigidBody2D());

                if (!activeGameObject.hasComponent(Box2DCollider.class))
                    if (ImGui.menuItem("Add Box2DCollider"))
                        activeGameObject.addComponent(new Box2DCollider());

                if (!activeGameObject.hasComponent(Circle2DCollider.class))
                    if (ImGui.menuItem("Add Circle2DCollider"))
                        activeGameObject.addComponent(new Circle2DCollider());

                if (!activeGameObject.hasComponent(Pillbox2DCollider.class))
                    if (ImGui.menuItem("Add Pillbox2DCollider"))
                        activeGameObject.addComponent(new Pillbox2DCollider());

                if (!activeGameObject.hasComponent(Camera.class))
                    if (ImGui.menuItem("Add Camera"))
                        activeGameObject.addComponent(new Camera());

                if (!activeGameObject.hasComponent(MeshRenderer.class))
                    if (ImGui.menuItem("Add MeshRender"))
                        activeGameObject.addComponent(new MeshRenderer());

                ImGui.endPopup();
            }

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
}
