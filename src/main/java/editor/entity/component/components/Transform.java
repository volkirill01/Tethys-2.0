package editor.entity.component.components;

import editor.entity.component.Component;
import org.joml.Vector2f;

public class Transform extends Component {

    public Vector2f position = new Vector2f(0.0f);
    public Vector2f scale = new Vector2f(1.0f);

    public void set(Vector2f position, Vector2f scale) {
        this.position.set(position);
        this.scale.set(scale);
    }

    @Override
    public void start() {

    }

    @Override
    public void update() {

    }
}
