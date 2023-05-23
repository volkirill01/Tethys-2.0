package TMP_MARIO_STUFF;

import engine.entity.GameObject;
import engine.renderer.renderer2D.SpriteRenderer;
import engine.renderer.renderer2D.SubTexture2D;
import engine.renderer.renderer2D.sprite.Sprite;
import engine.scenes.SceneManager;

public class Prefabs {

    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY) {
        GameObject block = SceneManager.getCurrentScene().createGameObject("Sprite OBJ_gen " + sprite.getTexture().getFilepath());
        block.transform.scale.set(sizeX, sizeY, 1.0f);
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        block.addComponent(renderer);

        return block;
    }

    public static GameObject generateObject(float sizeX, float sizeY) {
        GameObject block = SceneManager.getCurrentScene().createGameObject("OBJ");
        block.transform.scale.set(sizeX, sizeY, 1.0f);

        return block;
    }
}
