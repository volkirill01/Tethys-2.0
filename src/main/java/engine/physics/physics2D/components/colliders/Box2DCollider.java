package engine.physics.physics2D.components.colliders;

import engine.editor.gui.EditorGUI;
import engine.physics.physics2D.components.ed_Collider2D;
import engine.renderer.debug.DebugDraw;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Box2DCollider extends ed_Collider2D {

    private final Vector2f size = new Vector2f(1.0f);
    private final Vector2f offset = new Vector2f(0.0f);

    @Override
    public void editorUpdate() {
        Vector3f center = new Vector3f(this.gameObject.transform.position).add(this.getOffset().x, this.getOffset().y, 0.0f);
        DebugDraw.addBox2D(center, new Vector2f(this.size).mul(this.gameObject.transform.scale.x, this.gameObject.transform.scale.y), this.gameObject.transform.rotation);
    }

    @Override
    public void update() {
        Vector3f center = new Vector3f(this.gameObject.transform.position).add(this.getOffset().x, this.getOffset().y, 0.0f);
        DebugDraw.addBox2D(center, new Vector2f(this.size).mul(this.gameObject.transform.scale.x, this.gameObject.transform.scale.y), this.gameObject.transform.rotation);
    }

    @Override
    public void imgui() {
        EditorGUI.field_Vector2f("Size", this.size, new Vector2f(1.0f), EditorGUI.DEFAULT_FLOAT_FORMAT + "m");
        EditorGUI.field_Vector2f("Offset", this.offset, EditorGUI.DEFAULT_FLOAT_FORMAT + "m");
    }

    public Vector2f getSize() { return this.size; }

    public void setSize(Vector2f size) { this.size.set(size); }

    public Vector2f getOffset() { return this.offset; }

    public void setOffset(Vector2f offset) { this.offset.set(offset); }
}
