package editor.renderer.renderer2D.sprite;

import editor.renderer.Texture;
import org.joml.Vector2f;

public class Sprite {

    private float width, height;

    private Texture texture = null;
    private Vector2f[] textureCoordinates = {
            new Vector2f(1.0f, 1.0f),
            new Vector2f(1.0f, 0.0f),
            new Vector2f(0.0f, 0.0f),
            new Vector2f(0.0f, 1.0f)
    };

    public Texture getTexture() { return this.texture; }

    public void setTexture(Texture texture) { this.texture = texture; }

    public Vector2f[] getTextureCoordinates() { return this.textureCoordinates; }

    public void setTextureCoordinates(Vector2f[] textureCoordinates) { this.textureCoordinates = textureCoordinates; }

    public float getWidth() { return this.width; }

    public void setWidth(float width) { this.width = width; }

    public float getHeight() { return this.height; }

    public void setHeight(float height) { this.height = height; }

    public int getTextureID() { return this.texture != null ? this.texture.getTextureID() : -1; }
}
