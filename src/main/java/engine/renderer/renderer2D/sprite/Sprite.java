package engine.renderer.renderer2D.sprite;

import engine.renderer.Texture2D;
import org.joml.Vector2f;

import java.util.Arrays;

public class Sprite {

    private float width, height;

    private Texture2D texture = null;
    private Vector2f[] textureCoordinates = {
            new Vector2f(1.0f, 1.0f),
            new Vector2f(1.0f, 0.0f),
            new Vector2f(0.0f, 0.0f),
            new Vector2f(0.0f, 1.0f)
    };

    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        if (!(object instanceof Sprite s)) return false;

        if (object == this) return true;

        return s.width == this.width && s.height == this.height && s.texture.equals(this.texture) && Arrays.equals(s.textureCoordinates, this.textureCoordinates);
    }

    public Texture2D getTexture() { return this.texture; }

    public void setTexture(Texture2D texture) { this.texture = texture; }

    public Vector2f[] getTextureCoordinates() { return this.textureCoordinates; }

    public void setTextureCoordinates(Vector2f[] textureCoordinates) { this.textureCoordinates = textureCoordinates; }

    public float getWidth() { return this.width; }

    public void setWidth(float width) { this.width = width; }

    public float getHeight() { return this.height; }

    public void setHeight(float height) { this.height = height; }

    public int getTextureID() { return this.texture != null ? this.texture.getTextureID() : -1; }
}
