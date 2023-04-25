package editor.entity.component.components;

import editor.editor.gui.EditorGUI;
import editor.entity.component.Component;
import org.joml.Vector3f;

public class Transform extends Component {

    public Vector3f position = new Vector3f(0.0f);
    public Vector3f scale = new Vector3f(1.0f);
    public float rotation = 0.0f;

    private int zIndex = 0;

    public Transform() { }

    public Transform(Vector3f position) {
        set(position, new Vector3f(1.0f));
        this.zIndex = 0;
    }

    public Transform(Vector3f position, Vector3f scale) {
        set(position, scale);
        this.zIndex = 0;
    }

    public void set(Vector3f position, Vector3f scale) {
        this.position.set(position);
        this.scale.set(scale);
    }

    @Override
    public void imgui() {
        EditorGUI.field_Vector3f("Position", this.position);
        EditorGUI.field_Vector3f("Scale", this.scale, new Vector3f(32.0f));
        this.rotation = EditorGUI.field_Float("Rotation", this.rotation);
        this.zIndex = EditorGUI.field_Int("ZIndex", this.zIndex);
    }

    public Transform copy() { return new Transform(new Vector3f(this.position), new Vector3f(this.scale)); }

    public void copy(Transform to) { to.set(this.position, this.scale); }

    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        if (!(object instanceof Transform)) return false;

        if (object == this) return true;

        Transform t = (Transform) object;
        return t.position.equals(this.position) && t.scale.equals(this.scale) && t.rotation == this.rotation && t.zIndex == this.zIndex;
    }

    public int getZIndex() { return this.zIndex; }

    public void setZIndex(int zIndex) { this.zIndex = zIndex; }
}
