
package engine.renderer.renderer2D;

import engine.renderer.Texture2D;
import org.joml.Vector2f;

import java.util.Arrays;

public class SubTexture2D {

    private Texture2D texture;
    private final Vector2f[] textureCoordinates;

    public SubTexture2D(Texture2D texture, Vector2f min, Vector2f max) {
        this.texture = texture;

        this.textureCoordinates = new Vector2f[]{
                new Vector2f(min.x, min.y),
                new Vector2f(max.x, min.y),
                new Vector2f(max.x, max.y),
                new Vector2f(min.x, max.y)
        };
    }

    public static SubTexture2D createFromCoordinates(Texture2D texture, Vector2f coordinates, Vector2f cellSize, Vector2f spriteSize) {
        Vector2f min = new Vector2f((coordinates.x * cellSize.x) / texture.getWidth(), (coordinates.y * cellSize.y) / texture.getHeight());
        Vector2f max = new Vector2f(((coordinates.x + spriteSize.x) * cellSize.x) / texture.getWidth(), ((coordinates.y + spriteSize.y) * cellSize.y) / texture.getHeight());
        return new SubTexture2D(texture, min, max);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        if (!(object instanceof SubTexture2D s)) return false;

        if (object == this) return true;

        return s.texture.equals(this.texture) && Arrays.equals(s.textureCoordinates, this.textureCoordinates);
    }

    public Texture2D getTexture() { return this.texture; }

    public void setTexture(Texture2D texture) { this.texture = texture; }

    public Vector2f[] getTextureCoordinates() { return this.textureCoordinates; }
}
