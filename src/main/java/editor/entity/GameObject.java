package editor.entity;

import editor.entity.component.Component;
import editor.entity.component.components.Transform;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

    public String name;
    public Transform transform;
    private List<Component> components = new ArrayList<>();

    public GameObject(String name) {
        this.name = name;
        this.transform = new Transform();
        this.transform.gameObject = this;
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
        this.components.add(c);
        c.gameObject = this;
    }

    public void start() {
        for (Component c : this.components)
            c.start();
    }

    public void update() {
        for (Component c : this.components)
            c.update();
    }
}
