package engine.renderer.renderer2D;

import engine.assets.Asset;
import engine.assets.AssetPool;
import engine.editor.gui.EditorGUI;
import engine.renderer.EntityRenderer;
import engine.renderer.Texture2D;
import engine.renderer.renderer2D.sprite.Sprite;
import engine.stuff.customVariables.Color;
import org.joml.Vector2f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class SpriteRenderer extends ed_Renderer {
    // TODO FIX BUG, THEN CHANGING TEXTURE IN SPRITE, ITS DISAPPEARS

    private final Color color = Color.WHITE.copy();
    private Sprite sprite = AssetPool.getDefaultSprite();

//    private transient Transform lastTransform; // TODO ADD DIRTY FLAG SYSTEM

    private final Vector2f tiling = new Vector2f(1.0f); // TODO MOVE THIS IN MATERIAL

    @Override
    public void start() {
        super.start();
        if (this.sprite.getTexture() != null) {
            if (this.sprite.getTexture().equalsWithoutID(AssetPool.getWhiteTexture()))
                this.sprite.setTexture(AssetPool.getWhiteTexture());
            else
                this.sprite.setTexture(AssetPool.getTexture(this.sprite.getTexture().getFilepath()));
        }
//        this.lastTransform = gameObject.transform.copy();
    }

//    @Override
//    public void editorUpdate() {
//        if (this.lastTransform != null)
//            if (!this.lastTransform.equals(this.gameObject.transform))
//                this.gameObject.transform.copy(this.lastTransform);
//    }
//
//    @Override
//    public void update() {
//        if (this.lastTransform != null)
//            if (!this.lastTransform.equals(this.gameObject.transform))
//                this.gameObject.transform.copy(this.lastTransform);
//    }

    @Override
    public void destroy() { EntityRenderer.destroyGameObject(this.gameObject, SpriteRenderer.class); }

    @Override
    public void imgui() {
        EditorGUI.field_Color("Color", this.color);

        try {
            for (Field field : this.sprite.getClass().getDeclaredFields()) {
                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                if (isPrivate)
                    field.setAccessible(true);

                if (field.get(this.sprite) != null)
                    if (field.get(this.sprite).getClass() != Texture2D.class) {
                        if (isPrivate)
                            field.setAccessible(false);
                        continue;
                    }

                EditorGUI.field_Asset("Texture", this.sprite, field, Asset.AssetType.Texture);

                if (isPrivate)
                    field.setAccessible(false);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        EditorGUI.field_Vector2f("Tiling", this.tiling, new Vector2f(1.0f), new Vector2f(0.0f));
    }

    @Override
    public void reset() {
        this.color.set(Color.WHITE);
        this.sprite = AssetPool.getDefaultSprite();
        this.tiling.set(0.0f);
    }

    public Color getColor() { return this.color; }

    public void setColor(Color color) {
        if (!this.color.equals(color))
            this.color.set(color);
    }

    public Sprite getSprite() { return this.sprite; }

    public void setSprite(Sprite sprite) {
        if (!this.sprite.equals(sprite))
            this.sprite = (sprite);
    }

    public Vector2f getTiling() { return this.tiling; }
}
