package engine.renderer.debug;

import engine.stuff.customVariables.Color;
import org.joml.Vector3f;

public class DebugLine {

    private final Vector3f from;
    private final Vector3f to;
    private final Color color;
    private int lifetime;

    public DebugLine(Vector3f from, Vector3f to, Color color, int lifetime) {
        this.from = from;
        this.to = to;
        this.color = color;
        this.lifetime = lifetime;
    }

    public int beginFrame() {
        this.lifetime--;
        return this.lifetime;
    }

    public Vector3f getFrom() { return this.from; }

    public Vector3f getTo() { return this.to; }

    public Color getColor() { return this.color; }
}
