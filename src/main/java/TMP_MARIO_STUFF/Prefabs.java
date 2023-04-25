package TMP_MARIO_STUFF;

import editor.entity.GameObject;
import editor.entity.component.components.SpriteRenderer;
import editor.entity.component.components.Transform;
import editor.renderer.renderer2D.sprite.Sprite;
import editor.scenes.SceneManager;
import org.joml.Vector3f;

public class Prefabs {

    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY) {
        GameObject block = SceneManager.getCurrentScene().createGameObject("Sprite OBJ_gen " + sprite.getTexture().getFilepath());
        block.transform.scale.set(sizeX, sizeY, 1.0f);
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        block.addComponent(renderer);

        return block;
    }
}
