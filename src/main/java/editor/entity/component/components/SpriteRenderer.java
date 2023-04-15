package editor.entity.component.components;

import editor.entity.component.Component;
import editor.renderer.renderer2D.sprite.Sprite;
import editor.renderer.Texture;
import editor.stuff.customVariables.Color;
import org.joml.Vector2f;

public class SpriteRenderer extends Component {

    private Color color = Color.WHITE.copy();
    private Sprite sprite;

    private Transform lastTransform;
    private boolean isDirty = true;

    public SpriteRenderer() { }

    public SpriteRenderer(Color color) {
        this.sprite = new Sprite(null);
        this.color = color;
    }

    public SpriteRenderer(Sprite sprite) {
        this.sprite = sprite;
        this.color = Color.WHITE.copy();
    }

    @Override
    public void start() {
        this.lastTransform = gameObject.transform.copy();
    }

    @Override
    public void update() {
        if (!this.lastTransform.equals(this.gameObject.transform)) {
            this.gameObject.transform.copy(this.lastTransform);
            this.isDirty = true;
        }
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        if (!this.color.equals(color)) {
            this.color.set(color);
            this.isDirty = true;
        }
    }

    public Sprite getSprite() { return this.sprite; }

    public void setSprite(Sprite sprite) {
        // TODO CHECK IF SPRITE CHANGED, THEN UPDATE SPRITE AND DIRTY FLAG
//        if (!this.color.equals(color)) {
//            this.color.set(color);
//            this.isDirty = true;
//        }

        this.sprite = sprite;
        this.isDirty = true;
    }

    public Texture getTexture() { return this.sprite.getTexture(); }

    public Vector2f[] getTextureCoordinates() { return this.sprite.getTextureCoordinates(); }

    public boolean isDirty() { return this.isDirty; }

    public void setDirty(boolean dirty) { this.isDirty = dirty; }
}
