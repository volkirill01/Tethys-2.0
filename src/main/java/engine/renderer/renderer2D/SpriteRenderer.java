package engine.renderer.renderer2D;

import engine.editor.gui.EditorGUI;
import engine.entity.component.Component;
import engine.entity.component.Transform;
import engine.renderer.Texture2D;
import engine.renderer.renderer2D.sprite.Sprite;
import engine.stuff.customVariables.Color;
import org.joml.Vector2f;

public class SpriteRenderer extends Component {

    private final Color color = Color.WHITE.copy();
    private Sprite sprite = new Sprite();

    private transient Transform lastTransform;
    private transient boolean isDirty = true;

    @Override
    public void start() { this.lastTransform = gameObject.transform.copy(); }

    @Override
    public void editorUpdate() {
        if (this.lastTransform != null)
            if (!this.lastTransform.equals(this.gameObject.transform)) {
                this.gameObject.transform.copy(this.lastTransform);
                this.isDirty = true;
            }
    }

    @Override
    public void update() {
        if (this.lastTransform != null)
            if (!this.lastTransform.equals(this.gameObject.transform)) {
                this.gameObject.transform.copy(this.lastTransform);
                this.isDirty = true;
            }
    }

    @Override
    public void imgui() {
        if (EditorGUI.field_Color("Color", this.color))
            this.isDirty = true;
    }

    public Color getColor() { return this.color; }

    public void setColor(Color color) {
        if (!this.color.equals(color)) {
            this.color.set(color);
            this.isDirty = true;
        }
    }

    public void setColor(float scalar) {
        if (!this.color.equals(new Color(scalar, scalar, scalar, scalar))) {
            this.color.set(scalar, scalar, scalar, scalar);
            this.isDirty = true;
        }
    }

    public Sprite getSprite() { return this.sprite; }

    public void setSprite(Sprite sprite) {
        if (!this.sprite.equals(sprite)) {
            this.sprite = (sprite);
            this.isDirty = true;
        }
    }

    public Texture2D getTexture() { return this.sprite.getTexture(); }

    public void setTexture(Texture2D texture) { this.sprite.setTexture(texture); }

    public Vector2f[] getTextureCoordinates() { return this.sprite.getTextureCoordinates(); }

    public boolean isDirty() { return this.isDirty; }

    public void setDirty(boolean dirty) { this.isDirty = dirty; }
}
