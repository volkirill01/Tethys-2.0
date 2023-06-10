package engine.physics.physics2D.components.colliders;

import engine.editor.gui.EditorGUI;
import engine.physics.physics2D.components.ed_Collider2D;
import engine.renderer.debug.DebugRenderer;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Circle2DCollider extends ed_Collider2D {

    private float radius = 0.5f;
    private final Vector2f offset = new Vector2f(0.0f);

    @Override
    public void editorUpdate() { update(); }

    @Override
    public void update() {
        Vector3f center = new Vector3f(this.gameObject.transform.position).add(this.getOffset().x, this.getOffset().y, 0.0f);
        DebugRenderer.addCircle2D(center, this.gameObject.transform.position, this.radius * Math.max(this.gameObject.transform.scale.x, this.gameObject.transform.scale.y), this.gameObject.transform.rotation);
    }

    @Override
    public void imgui() {
        this.radius = EditorGUI.field_Float("Radius", this.radius, EditorGUI.DEFAULT_FLOAT_FORMAT + "m");
        EditorGUI.field_Vector2f("Offset", this.offset, EditorGUI.DEFAULT_FLOAT_FORMAT + "m");
    }

    @Override
    public void reset() {
        this.radius = 0.5f;
        this.offset.set(0.0f);
    }

    public float getRadius() { return this.radius; }

    public void setRadius(float radius) { this.radius = radius; }

    public Vector2f getOffset() { return this.offset; }

    public void setOffset(Vector2f offset) { this.offset.set(offset); }
}
