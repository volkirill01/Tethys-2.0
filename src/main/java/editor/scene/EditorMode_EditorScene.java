package editor.scene;


import editor.assets.AssetPool;
import editor.entity.GameObject;
import editor.entity.component.components.SpriteRenderer;
import editor.eventListeners.Input;
import editor.eventListeners.KeyCode;
import editor.renderer.Camera;
import editor.renderer.renderer2D.sprite.SpriteSheet;
import editor.stuff.utils.Time;
import org.joml.Vector3f;

public class EditorMode_EditorScene extends EditorScene {

    private SpriteSheet sprites;
    private GameObject go1;

    public EditorMode_EditorScene() {

    }

    @Override
    public void init() {
        loadResources();
        this.camera = new Camera(new Vector3f(-250.0f, 0.0f, 0.0f));

        sprites = AssetPool.getSpriteSheet("Assets/spritesheet.png");

        go1 = new GameObject("Obj1");
        go1.transform.position.set(100.0f, 100.0f, 0.0f);
        go1.transform.scale.set(256.0f, 256.0f, 0.0f);
        go1.addComponent(new SpriteRenderer(sprites.getSprite(0)));
        addGameObjectToScene(go1);

        GameObject go2 = new GameObject("Obj2");
        go2.transform.position.set(400.0f, 100.0f, 0.0f);
        go2.transform.scale.set(256.0f, 256.0f, 0.0f);
        go2.addComponent(new SpriteRenderer(sprites.getSprite(15)));
        addGameObjectToScene(go2);

        // Performance Test
//        int xOffset = 10;
//        int yOffset = 10;
//
//        float totalWidth = (float) (600 - xOffset * 2);
//        float totalHeight = (float) (300 - yOffset * 2);
//        float sizeX = totalWidth / 100.0f;
//        float sizeY = totalHeight / 100.0f;
//
//        for (int x = 0; x < 100; x++) {
//            for (int y = 0; y < 100; y++) {
//                float xPos = xOffset + (x * sizeX);
//                float yPos = yOffset + (y * sizeY);
//
//                GameObject go = new GameObject("Obj " + x + ", " + y);
//                go.transform.position.set(xPos, yPos);
//                go.transform.scale.set(sizeX, sizeY);
//                go.addComponent(new SpriteRenderer(new Color(5.0f, 2.0f, 25.0f, 255.0f)));
//                this.addGameObjectToScene(go);
//            }
//        }
    }

    private void loadResources() {
        AssetPool.addSpriteSheet("Assets/spritesheet.png",
                new SpriteSheet(AssetPool.getTexture("Assets/spritesheet.png"),
                        16, 16, 26, 0, 0, 0, 0));
    }

    private int spriteIndex = 0;
    private float spriteFlipTime = 0.2f;
    private float spriteFlipTimeLeft = 0.0f;

    @Override
    public void update() {
        if (Input.buttonDown(KeyCode.Arrow_Right))
            camera.getPosition().x += 100f * Time.deltaTime();
        if (Input.buttonDown(KeyCode.Arrow_Left))
            camera.getPosition().x -= 100f * Time.deltaTime();

        if (Input.buttonDown(KeyCode.Arrow_Up))
            camera.getPosition().y += 100f * Time.deltaTime();
        if (Input.buttonDown(KeyCode.Arrow_Down))
            camera.getPosition().y -= 100f * Time.deltaTime();

        if (spriteFlipTimeLeft <= 0) {
            spriteFlipTimeLeft = spriteFlipTime;
            spriteIndex++;
            if (spriteIndex > 4)
                spriteIndex = 0;
            go1.getComponent(SpriteRenderer.class).setSprite(sprites.getSprite(spriteIndex));
        } else {
            spriteFlipTimeLeft -= Time.deltaTime();
        }

        go1.transform.position.x += 10 * Time.deltaTime();

        for (GameObject go : this.gameObjects)
            go.update();

        this.spriteRenderer.render();
    }
}
