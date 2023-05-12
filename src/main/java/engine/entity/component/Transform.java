package engine.entity.component;

import engine.editor.gui.EditorGUI;
import org.joml.Vector3f;

public class Transform extends Component { // TODO ADD TRANSFORM CONSTRAINS(LIMIT POSITION, LOCK SCALE, ...)

    public final Vector3f position = new Vector3f(0.0f);
    public final Vector3f scale = new Vector3f(1.0f);
    public final Vector3f rotation = new Vector3f(0.0f);

    private int zIndex = 0;

    public Transform() { }

    public Transform(Vector3f position) {
        set(position, new Vector3f(1.0f), new Vector3f(0.0f));
        this.zIndex = 0;
    }

    public Transform(Vector3f position, Vector3f scale) {
        set(position, scale, new Vector3f(0.0f));
        this.zIndex = 0;
    }

    public void set(Vector3f position, Vector3f scale, Vector3f rotation) {
        this.position.set(position);
        this.scale.set(scale);
        this.rotation.set(rotation);
    }

    @Override
    public void imgui() { // Overriding function for custom formatting and reset value of scale vector
        EditorGUI.field_Vector3f("Position", this.position, EditorGUI.DEFAULT_FLOAT_FORMAT + "m");
        EditorGUI.field_Vector3f("Rotation", this.rotation, EditorGUI.DEFAULT_FLOAT_FORMAT + "deg"); // TODO REPLACE 'deg' WITH DEGREES SIGN '°' AND FIX DRAWING OF IT
        EditorGUI.field_Vector3f("Scale", this.scale, new Vector3f(0.25f), EditorGUI.DEFAULT_FLOAT_FORMAT + "m");
        this.zIndex = EditorGUI.field_Int("ZIndex", this.zIndex);
    }

    public Transform copy() { return new Transform(new Vector3f(this.position), new Vector3f(this.scale)); }

    public void copy(Transform to) { to.set(this.position, this.scale, this.rotation); }

    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        if (!(object instanceof Transform t)) return false;

        if (object == this) return true;

        return t.position.equals(this.position) && t.scale.equals(this.scale) && t.rotation.equals(this.rotation) && t.zIndex == this.zIndex;
    }

    public int getZIndex() { return this.zIndex; }

    public void setZIndex(int zIndex) { this.zIndex = zIndex; }
}
