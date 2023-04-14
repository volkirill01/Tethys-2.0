package editor.entity.component;

import editor.entity.GameObject;

public abstract class Component {

    public GameObject gameObject = null;

    public abstract void start();

    public abstract void update();
}
