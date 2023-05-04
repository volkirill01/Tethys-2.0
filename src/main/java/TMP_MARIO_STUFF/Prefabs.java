package TMP_MARIO_STUFF;

import editor.entity.GameObject;
import editor.renderer.renderer2D.SpriteRenderer;
import editor.renderer.renderer2D.sprite.Sprite;
import editor.scenes.SceneManager;

public class Prefabs {

    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY) {
        GameObject block = SceneManager.getCurrentScene().createGameObject("Sprite OBJ_gen " + sprite.getTexture().getFilepath());
        block.transform.scale.set(sizeX, sizeY, 0.25f);
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        block.addComponent(renderer);

        return block;
    }
}
