package TMP_MARIO_STUFF;

import editor.entity.GameObject;
import editor.entity.component.components.SpriteRenderer;
import editor.entity.component.components.Transform;
import editor.renderer.renderer2D.sprite.Sprite;
import org.joml.Vector3f;

public class Prefabs {

    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY) {
        GameObject block = new GameObject("Sprite OBJ_gen", new Transform(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(sizeX, sizeY, 1.0f)), 0);
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        block.addComponent(renderer);

        return block;
    }
}
