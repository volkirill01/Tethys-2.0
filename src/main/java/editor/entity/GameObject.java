package editor.entity;

import com.google.gson.Gson;
import editor.assets.AssetPool;
import editor.editor.gui.EditorGUI;
import editor.entity.component.Component;
import editor.renderer.renderer2D.SpriteRenderer;
import editor.entity.component.Transform;
import editor.stuff.utils.EditorGson;
import imgui.ImGui;
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
            }
//            } else
//                Console.log(LogType.Error, "GameObject (" + this.name + ") not has that component - '" + componentClass.getName() + "'.");
        }
    }

    public void addComponent(Component c) {
        c.generateID();
        this.components.add(c);
        c.gameObject = this;
    }

    public void imgui() {
        this.name = EditorGUI.textFieldNoLabel("GameObject_Name_" + this.uid, this.name, "Name");

        for (Component c : this.components)
            if (ImGui.collapsingHeader(c.getClass().getSimpleName(), c.getClass() == Transform.class ? ImGuiTreeNodeFlags.DefaultOpen : ImGuiTreeNodeFlags.None))
                c.imgui();
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
