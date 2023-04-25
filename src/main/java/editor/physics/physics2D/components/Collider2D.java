package editor.physics.physics2D.components;

import editor.entity.component.Component;
import org.joml.Vector2f;

public class Collider2D extends Component {

    private final Vector2f offset = new Vector2f(0.0f);

    public void setOffset(Vector2f offset) { this.offset.set(offset); }

    public Vector2f getOffset() { return this.offset; }
}
