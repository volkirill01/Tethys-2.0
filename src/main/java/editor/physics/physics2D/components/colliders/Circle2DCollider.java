package editor.physics.physics2D.components.colliders;

import editor.physics.physics2D.components.Collider2D;

public class Circle2DCollider extends Collider2D {

    private float radius = 1.0f;

    public float getRadius() { return this.radius; }

    public void setRadius(float radius) { this.radius = radius; }
}
