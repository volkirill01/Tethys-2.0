package editor.entity;

import editor.entity.component.Component;
import editor.entity.component.components.Transform;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

    private static int ID_COUNTER = 0;
    private int uid = -1;

    public String name;
    public Transform transform;
    private final List<Component> components = new ArrayList<>();
    private int zIndex = 0;
    private boolean clickable = true;

    private boolean doSerialization = true;

    public GameObject(String name) {
        this.name = name;
        this.transform = new Transform();
        this.transform.gameObject = this;

        this.uid = ID_COUNTER++;
    }

    public GameObject(String name, Transform transform, int zIndex) {
        this.name = name;
        this.transform = transform;
        this.transform.gameObject = this;
        this.zIndex = zIndex;

        this.uid = ID_COUNTER++;
    }

    public <T extends Component> boolean hasComponent(Class<T> componentClass) {
        for (Component c : this.components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                if (c.getClass() == componentClass)
                    return true;
            } else
                throw new ClassCastException("Error of casting component - '" + componentClass.getName() + "' to '" + c.getClass().getName() + "'.");
        }
        return false;
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : this.components) {
            if (componentClass.isAssignableFrom(c.getClass()))
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException e) {
                    throw new ClassCastException("Error of casting component - '" + componentClass.getName() + "' to '" + c.getClass().getName() + "'.");
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
        for (Component c : this.components)
            c.imgui();
    }

    public void start() {
        for (Component c : this.components)
            c.start();
    }

    public void update() {
        for (Component c : this.components)
            c.update();
    }

    public void setDoSerialize() { this.doSerialization = true; }

    public void setNoSerialize() { this.doSerialization = false; }

    public boolean isDoSerialization() { return this.doSerialization; }

    public int getZIndex() { return this.zIndex; }

    public static void init(int maxID) { ID_COUNTER = maxID; }

    public int getUid() { return this.uid; }

    public List<Component> getAllComponents() { return this.components; }

    public boolean isClickable() { return this.clickable; }

    public void setClickable(boolean clickable) { this.clickable = clickable; }
}
