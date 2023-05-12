package engine.entity;

import com.google.gson.Gson;
import engine.TestFieldsWindow;
import engine.assets.AssetPool;
import engine.editor.console.Console;
import engine.editor.console.LogType;
import engine.editor.gui.EditorGUI;
import engine.entity.component.Component;
import engine.renderer.renderer2D.SpriteRenderer;
import engine.entity.component.Transform;
import engine.stuff.utils.EditorGson;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

    private static int ID_COUNTER = 1;
    private int uid;

    public String name;
    public transient Transform transform;
    private final List<Component> components = new ArrayList<>();
    private boolean clickable = true;

    private transient boolean doSerialization = true;
    private transient boolean isDeath = false;

    public GameObject(String name) {
        this.name = name;
        this.uid = ID_COUNTER++;
    }

    public <T extends Component> boolean hasComponent(Class<T> componentClass) { return getComponent(componentClass) != null; }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : this.components) {
            if (componentClass.isAssignableFrom(c.getClass()))
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException e) {
                    throw new ClassCastException(String.format("Error of casting component - '%s' to '%s'", componentClass.getName(), c.getClass().getName()));
                }
        }
        return null;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        for (int i = 0; i < this.components.size(); i++) {
            Component c = this.components.get(i);

            if (componentClass.isAssignableFrom(c.getClass())) {
                this.components.remove(i);
                return;
            } else
                Console.log(String.format("GameObject (%s) not has component - '%s'", this.name, componentClass.getName()), LogType.Error);
        }
    }

    public void addComponent(Component c) {
        c.generateID();
        this.components.add(c);
        c.gameObject = this;
    }

    public void imgui() {
        this.name = EditorGUI.textFieldNoLabel("GameObject_Name_" + this.uid, this.name, "Name", ImGui.getContentRegionAvailX() / 1.5f);
        ImGui.sameLine();

        //<editor-fold desc="Add Component Button">
        float width = (ImGui.getContentRegionAvailX() / 2.0f) - (ImGui.calcTextSize("Add component").x / 2.0f);
        float buttonCenter = ImGui.getCursorScreenPosX() + width + ImGui.getStyle().getFramePaddingX() + TestFieldsWindow.getFloats[0];
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, width, ImGui.getStyle().getFramePaddingY());
        if (ImGui.button("Add component"))
            ImGui.openPopup("ComponentAdder");
        ImGui.popStyleVar();
        //</editor-fold>

        //<editor-fold desc="Add Component Popup">
        ImVec2 popupPosition = new ImVec2(buttonCenter - ImGui.getStateStorage().getFloat(ImGui.getID("addComponentPopupWidth")), ImGui.getWindowPosY() + ImGui.getCursorPosY());
        if (ImGui.isPopupOpen("ComponentAdder"))
            ImGui.setNextWindowPos(popupPosition.x, popupPosition.y);
        if (ImGui.beginPopup("ComponentAdder")) {
            for (Component c : Component.allComponents()) {
                if (!hasComponent(c.getClass()))
                    if (ImGui.menuItem(c.getClass().getSimpleName())) {
                        Gson gson = EditorGson.getGsonBuilder();
                        String gsonString = gson.toJson(c, Component.class);
                        Component copy = gson.fromJson(gsonString, Component.class);
                        addComponent(copy);
                    }
            }
            ImGui.getStateStorage().setFloat(ImGui.getID("addComponentPopupWidth"), ImGui.getContentRegionMaxX());
            ImGui.endPopup();
        }
        //</editor-fold>
        ImGui.separator();

        for (Component c : this.components)
            if (EditorGUI.beginCollapsingHeader(c.getClass().getSimpleName(), c.getClass() == Transform.class ? ImGuiTreeNodeFlags.DefaultOpen : ImGuiTreeNodeFlags.None)) {
                c.imgui();
                EditorGUI.endCollapsingHeader();
            }
    }

    public void start() {
        for (Component c : this.components)
            c.start();
    }

    public void editorUpdate() {
        for (Component c : this.components)
            c.editorUpdate();
    }

    public void update() {
        for (Component c : this.components)
            c.update();
    }

    public void destroy() {
        this.isDeath = true;
        for (Component component : this.components)
            component.destroy();
    }

    public GameObject copy() {
        Gson gson = EditorGson.getGsonBuilder();

        String objAsGson = gson.toJson(this);
        GameObject copy = gson.fromJson(objAsGson, GameObject.class);
        
        copy.generateUid();
        for (Component c : copy.components)
            c.generateID();

        if (copy.hasComponent(SpriteRenderer.class)) {
            SpriteRenderer renderer = copy.getComponent(SpriteRenderer.class);
            if (renderer.getTexture() != null)
                renderer.setTexture(AssetPool.getTexture(renderer.getTexture().getFilepath()));
        }
        return copy;
    }

    public void setSerialize(boolean serialize) { this.doSerialization = serialize; }

    public boolean isDoSerialization() { return this.doSerialization; }

    public static void init(int maxID) { ID_COUNTER = maxID; }

    public int getUid() { return this.uid; }

    private void generateUid() { this.uid = ID_COUNTER++; }

    public List<Component> getAllComponents() { return this.components; }

    public boolean isClickable() { return this.clickable; }

    public void setClickable(boolean clickable) { this.clickable = clickable; }

    public boolean isDeath() { return this.isDeath; }
}
