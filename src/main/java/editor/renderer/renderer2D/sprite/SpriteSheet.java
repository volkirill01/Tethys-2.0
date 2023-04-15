package editor.renderer.renderer2D.sprite;

import editor.renderer.Texture;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class SpriteSheet {

    private Texture texture;
    private List<Sprite> sprites;

    public SpriteSheet(Texture texture, int spriteWidth, int spriteHeight, int numberOfSprites, int spacingX, int spacingY, int paddingX, int paddingY) {
        this.texture = texture;
        this.sprites = new ArrayList<>();

        int currentX = paddingX;
        int currentY = paddingY + this.texture.getHeight() - spriteHeight;
        for (int i = 0; i < numberOfSprites; i++) {
            float topY = (currentY + spriteHeight) / (float) this.texture.getHeight();
            float rightX = (currentX + spriteWidth) / (float) this.texture.getWidth();
            float leftX = currentX / (float) this.texture.getWidth();
            float bottomY = currentY / (float) this.texture.getHeight();

            Vector2f[] texCoords = {
                    new Vector2f(rightX, topY),
                    new Vector2f(rightX, bottomY),
                    new Vector2f(leftX, bottomY),
                    new Vector2f(leftX, topY)
            };
            Sprite sprite = new Sprite(this.texture, texCoords);
            this.sprites.add(sprite);

            currentX += spriteWidth + spacingX;
            if (currentX >= texture.getWidth() - paddingX) {
                currentX = 0;
                currentY -= spriteHeight + spacingY;
            }
        }
    }

    public Sprite getSprite(int index) { return this.sprites.get(index); }
}
