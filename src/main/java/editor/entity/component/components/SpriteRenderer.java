package editor.entity.component.components;

import editor.entity.component.Component;
import editor.renderer.renderer2D.sprite.Sprite;
import editor.renderer.Texture;
import editor.stuff.customVariables.Color;
import imgui.ImGui;
import org.joml.Vector2f;

public class SpriteRenderer extends Component {

    private final Color color = Color.WHITE.copy();
    private Sprite sprite = new Sprite();

    private transient Transform lastTransform;
    private transient boolean isDirty = true;

    public SpriteRenderer() { }

    @Override
    public void start() { this.lastTransform = gameObject.transform.copy(); }

    @Override
    public void update() {
        if (!this.lastTransform.equals(this.gameObject.transform)) {
            this.gameObject.transform.copy(this.lastTransform);
            this.isDirty = true;
        }
    }

//    @Override
//    public void imgui() {
//        float[] imColors = { color.r / 255.0f, color.g / 255.0f, color.b / 255.0f };
//        if (ImGui.colorPicker3("Color Picker: ", imColors)) {
//            this.color.set(imColors[0] * 255.0f, imColors[1] * 255.0f, imColors[2] * 255.0f);
//            this.isDirty = true;
//        }
//    }

    public Color getColor() { return this.color; }

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

    public void setTexture(Texture texture) { this.sprite.setTexture(texture); }

    public Vector2f[] getTextureCoordinates() { return this.sprite.getTextureCoordinates(); }

    public boolean isDirty() { return this.isDirty; }

    public void setDirty(boolean dirty) { this.isDirty = dirty; }
}
