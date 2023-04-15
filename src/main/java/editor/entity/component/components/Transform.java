package editor.entity.component.components;

import editor.entity.component.Component;
import org.joml.Vector3f;

public class Transform extends Component {

    public Vector3f position = new Vector3f(0.0f);
    public Vector3f scale = new Vector3f(1.0f);

    public Transform() { }

    public Transform(Vector3f position) { set(position, new Vector3f(1.0f)); }

    public Transform(Vector3f position, Vector3f scale) { set(position, scale); }

    public void set(Vector3f position, Vector3f scale) {
        this.position.set(position);
        this.scale.set(scale);
    }

    @Override
    public void start() {

    }

    @Override
    public void update() {

    }

    public Transform copy() { return new Transform(new Vector3f(this.position), new Vector3f(this.scale)); }

    public void copy(Transform to) { to.set(this.position, this.scale); }

    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        if (!(object instanceof Transform)) return false;

        if (object == this) return true;

        Transform t = (Transform) object;
        return t.position.equals(this.position) && t.scale.equals(this.scale);
    }
}
