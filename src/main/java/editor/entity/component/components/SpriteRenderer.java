package editor.entity.component.components;

import editor.entity.component.Component;
import editor.stuff.customVariables.Color;

public class SpriteRenderer extends Component {

    private Color color = Color.WHITE.copy();

    public SpriteRenderer() { }

    public SpriteRenderer(Color color) { this.color = color; }

    @Override
    public void start() {

    }

    @Override
    public void update() {

    }

    public Color getColor() { return this.color; }
}
