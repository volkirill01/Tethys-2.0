package engine.entity;

import com.google.gson.Gson;
import engine.editor.console.Console;
import engine.entity.component.Component;
import engine.entity.component.TagComponent;
import engine.logging.DebugLog;
import engine.profiling.Profiler;
import engine.entity.component.Transform;
import engine.editor.UUID;
import engine.stuff.utils.EditorGson;
import org.joml.Random;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

    private static int ID_COUNTER = 1;
    private transient int incrementedID;

    public transient Transform transform;
    public TagComponent tagComponent = new TagComponent();
    private final List<Component> components = new ArrayList<>();
    private boolean clickable = true;

    private transient boolean doSerialization = true;
    private transient boolean isDeath = false;

    public GameObject(String name) {
        this.tagComponent.name = name;
        this.incrementedID = ID_COUNTER++;
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
        DebugLog.log("GameObject:RemoveComponent: ", getName(), ", component: ", componentClass.getName());
        for (int i = 0; i < this.components.size(); i++) {
            Component c = this.components.get(i);

            if (componentClass.isAssignableFrom(c.getClass())) {
                this.components.get(i).destroy();
                this.components.remove(i);
                return;
            }
        }
        DebugLog.logError("GameObject:RemoveComponent: ", getName(), ", not has Component: ", componentClass.getName());
        Console.logError(String.format("GameObject (%s) not has component - '%s'", getName(), componentClass.getName()));
    }

    public void addComponent(Component c) {
        DebugLog.log("GameObject:AddComponent: ", getName(), ", component: ", c.getClass().getName());

        c.generateID();
        this.components.add(c);
        c.gameObject = this;

        c.start();
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
        DebugLog.log("GameObject:Destroy: ", getName());

        Profiler.startTimer(String.format("Destroy GameObject - '%s'", getName()));
        this.isDeath = true;
        for (Component component : this.components)
            component.destroy();
        Profiler.stopTimer(String.format("Destroy GameObject - '%s'", getName()));
    }

    public GameObject copy() {
        DebugLog.log("GameObject:Copy: ", getName());

        Profiler.startTimer(String.format("Copy GameObject - '%s'", getName()));
        Gson gson = EditorGson.getGsonBuilder();

        String objAsGson = gson.toJson(this);
        GameObject copy = gson.fromJson(objAsGson, GameObject.class);

        copy.tagComponent.id = new UUID();
        copy.generateIncrementedID();
        for (Component c : copy.components)
            c.generateID();

        Profiler.stopTimer(String.format("Copy GameObject - '%s'", getName()));
        return copy;
    }

    public String getName() { return this.tagComponent.name; }

    public void setName(String name) { this.tagComponent.name = name; }

    public void setSerialize(boolean serialize) { this.doSerialization = serialize; }

    public boolean isDoSerialization() { return this.doSerialization; }

    public static void init(int maxID) { ID_COUNTER = maxID; }

    public long getUUID() { return this.tagComponent.id.get(); }

    public int getIncrementedID() { return this.incrementedID; }

    private void generateIncrementedID() { this.incrementedID = ID_COUNTER++; }

    public List<Component> getAllComponents() { return this.components; }

    public boolean isClickable() { return this.clickable; }

    public void setClickable(boolean clickable) { this.clickable = clickable; }

    public boolean isDeath() { return this.isDeath; }

    private final boolean hasChildren = new Random().nextFloat() > 0.6f; // Temporary for tweaking GUI
    public boolean hasChildren() { return hasChildren; }
}
