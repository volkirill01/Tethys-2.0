package engine.physics.physics2D.components.colliders;

import engine.editor.gui.EditorGUI;
import engine.physics.physics2D.components.ed_Collider2D;
import engine.renderer.debug.DebugDraw;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Circle2DCollider extends ed_Collider2D {

    private float radius = 0.25f;
    private final Vector2f offset = new Vector2f(0.0f);

    @Override
    public void editorUpdate() {
        Vector3f center = new Vector3f(this.gameObject.transform.position).add(this.getOffset().x, this.getOffset().y, 0.0f);
        DebugDraw.addCircle2D(center, this.radius,  this.gameObject.transform.rotation);
    }

    @Override
    public void update() {
        Vector3f center = new Vector3f(this.gameObject.transform.position).add(this.getOffset().x, this.getOffset().y, 0.0f);
        DebugDraw.addCircle2D(center, this.radius, this.gameObject.transform.rotation);
    }

    @Override
    public void imgui() {
        this.radius = EditorGUI.field_Float("Radius", this.radius, EditorGUI.DEFAULT_FLOAT_FORMAT + "m");
        EditorGUI.field_Vector2f("Offset", this.offset, EditorGUI.DEFAULT_FLOAT_FORMAT + "m");
    }

    public float getRadius() { return this.radius; }

    public void setRadius(float radius) { this.radius = radius; }

    public Vector2f getOffset() { return this.offset; }

    public void setOffset(Vector2f offset) { this.offset.set(offset); }
}
