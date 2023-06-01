package engine.renderer.renderer2D;

import engine.assets.Asset;
import engine.assets.AssetPool;
import engine.editor.gui.EditorGUI;
import engine.entity.component.Transform;
import engine.renderer.Texture;
import engine.renderer.Texture2D;
import engine.renderer.renderer2D.sprite.Sprite;
import engine.stuff.customVariables.Color;
import org.joml.Vector2f;

public class SpriteRenderer extends ed_Renderer {

    private final Color color = Color.WHITE.copy();
    private Sprite sprite = AssetPool.getDefaultSprite();

    private transient Transform lastTransform;

    private final Vector2f tiling = new Vector2f(1.0f); // TODO MOVE THIS IN MATERIAL

    @Override
    public void start() { this.lastTransform = gameObject.transform.copy(); }

    @Override
    public void editorUpdate() {
        if (this.lastTransform != null)
            if (!this.lastTransform.equals(this.gameObject.transform)) {
                this.gameObject.transform.copy(this.lastTransform);
                setDirty(true);
            }
    }

    @Override
    public void update() {
        if (this.lastTransform != null)
            if (!this.lastTransform.equals(this.gameObject.transform)) {
                this.gameObject.transform.copy(this.lastTransform);
                setDirty(true);
            }
    }

    @Override
    public void imgui() {
        if (EditorGUI.field_Color("Color", this.color))
            setDirty(true);
        Texture oldTexture = this.getSprite().getTexture();
        this.sprite.setTexture((Texture2D) EditorGUI.field_Asset("Texture", this.sprite.getTexture(), Asset.AssetType.Texture));
        if (!oldTexture.equals(this.sprite.getTexture()))
            setDirty(true);

        if (EditorGUI.field_Vector2f("Tiling", this.tiling, new Vector2f(1.0f), new Vector2f(0.0f)))
            setDirty(true);
    }

    public Color getColor() { return this.color; }

    public void setColor(Color color) {
        if (!this.color.equals(color)) {
            this.color.set(color);
            setDirty(true);
        }
    }

    public Sprite getSprite() { return this.sprite; }

    public void setSprite(Sprite sprite) {
        if (!this.sprite.equals(sprite)) {
            this.sprite = (sprite);
            setDirty(true);
        }
    }

    public Vector2f getTiling() { return this.tiling; }
}
