package editor.renderer.renderer2D.sprite;

import editor.renderer.Texture;
import org.joml.Vector2f;

public class Sprite {

    private final Texture texture;
    private final Vector2f[] textureCoordinates;

    public Sprite(Texture texture) {
        this.texture = texture;
        this.textureCoordinates = new Vector2f[]{
                new Vector2f(1.0f, 1.0f),
                new Vector2f(1.0f, 0.0f),
                new Vector2f(0.0f, 0.0f),
                new Vector2f(0.0f, 1.0f)
        };
    }

    public Sprite(Texture texture, Vector2f[] textureCoordinates) {
        this.texture = texture;
        this.textureCoordinates = textureCoordinates;
    }

    public Texture getTexture() { return this.texture; }

    public Vector2f[] getTextureCoordinates() { return this.textureCoordinates; }
}
