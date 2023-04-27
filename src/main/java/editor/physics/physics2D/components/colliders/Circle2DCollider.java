package editor.physics.physics2D.components.colliders;

import editor.physics.physics2D.components.Collider2D;
import editor.renderer.debug.DebugDraw;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Circle2DCollider extends Collider2D {

    private float radius = 1.0f;
    private final Vector2f offset = new Vector2f(0.0f);

    @Override
    public void editorUpdate() {
        Vector3f center = new Vector3f(this.gameObject.transform.position).add(this.getOffset().x, this.getOffset().y, 0.0f);
        DebugDraw.addCircle2D(center, this.radius, new Vector3f(0.0f, 0.0f, this.gameObject.transform.rotation));
    }

    @Override
    public void update() {
        Vector3f center = new Vector3f(this.gameObject.transform.position).add(this.getOffset().x, this.getOffset().y, 0.0f);
        DebugDraw.addCircle2D(center, this.radius, new Vector3f(0.0f, 0.0f, this.gameObject.transform.rotation));
    }

    public float getRadius() { return this.radius; }

    public void setRadius(float radius) { this.radius = radius; }

    public Vector2f getOffset() { return this.offset; }

    public void setOffset(Vector2f offset) { this.offset.set(offset); }
}
