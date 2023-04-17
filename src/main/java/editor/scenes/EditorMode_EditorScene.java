package editor.scenes;


import TMP_MARIO_STUFF.Prefabs;
import editor.assets.AssetPool;
import editor.entity.GameObject;
import editor.entity.component.components.Rigidbody;
import editor.entity.component.components.SpriteRenderer;
import editor.renderer.Camera;
import editor.renderer.renderer2D.sprite.Sprite;
import editor.renderer.renderer2D.sprite.SpriteSheet;
import editor.stuff.customVariables.Color;
import editor.stuff.inputActions.MouseControls;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
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
        sprites = AssetPool.getSpriteSheet("Assets/decorationsAndBlocks.png");

        if (levelLoaded) {
            if (gameObjects.size() > 0)
                activeGameObject = gameObjects.get(0);
            return;
        }


//        go1 = new GameObject("Obj1");
//        go1.transform.position.set(100.0f, 20.0f, 0.0f);
//        go1.transform.scale.set(256.0f, 256.0f, 0.0f);
//        SpriteRenderer spriteRenderer1 = new SpriteRenderer();
//        spriteRenderer1.setSprite(sprites.getSprite(0));
//        go1.addComponent(spriteRenderer1);
//        addGameObjectToScene(go1);

//        GameObject go2 = new GameObject("Obj2");
//        go2.transform.position.set(400.0f, 20.0f, 0.0f);
//        go2.transform.scale.set(256.0f, 256.0f, 0.0f);
//        SpriteRenderer spriteRenderer2 = new SpriteRenderer();
//        spriteRenderer2.setColor(Color.WHITE.copy());
//        go2.addComponent(spriteRenderer2);
//        go2.addComponent(new Rigidbody());
//        addGameObjectToScene(go2);
//
//        this.activeGameObject = go2;
//
//        GameObject blendObj1 = new GameObject("Green");
//        blendObj1.transform.position.set(350.0f, 250.0f, 0.0f);
//        blendObj1.transform.scale.set(100.0f, 100.0f, 0.0f);
//        Sprite greenSprite = new Sprite();
//        greenSprite.setTexture(AssetPool.getTexture("Assets/blendImage2.png"));
//        SpriteRenderer greenSpriteRenderer = new SpriteRenderer();
//        greenSpriteRenderer.setSprite(greenSprite);
//        blendObj1.addComponent(greenSpriteRenderer);
//        addGameObjectToScene(blendObj1);
//        GameObject blendObj2 = new GameObject("Red");
//        blendObj2.transform.position.set(400.0f, 300.0f, 0.0f);
//        blendObj2.transform.scale.set(100.0f, 100.0f, 0.0f);
//        Sprite redSprite = new Sprite();
//        redSprite.setTexture(AssetPool.getTexture("Assets/blendImage1.png"));
//        SpriteRenderer redSpriteRenderer = new SpriteRenderer();
//        redSpriteRenderer.setSprite(redSprite);
//        blendObj2.addComponent(redSpriteRenderer);
//        addGameObjectToScene(blendObj2);
//        GameObject blendObj3 = new GameObject("Green");
//        blendObj3.transform.position.set(450.0f, 350.0f, 0.0f);
//        blendObj3.transform.scale.set(100.0f, 100.0f, 0.0f);
//        Sprite green2Sprite = new Sprite();
//        green2Sprite.setTexture(AssetPool.getTexture("Assets/blendImage2.png"));
//        SpriteRenderer green2SpriteRenderer = new SpriteRenderer();
//        green2SpriteRenderer.setSprite(green2Sprite);
//        blendObj3.addComponent(green2SpriteRenderer);
//        addGameObjectToScene(blendObj3);

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
        AssetPool.addSpriteSheet("Assets/decorationsAndBlocks.png",
                new SpriteSheet(AssetPool.getTexture("Assets/decorationsAndBlocks.png"),
                        16, 16, 81, 0, 0, 0, 0));

        for (GameObject go : this.gameObjects) {
            if (go.hasComponent(SpriteRenderer.class)) {
                SpriteRenderer renderer = go.getComponent(SpriteRenderer.class);
                if (renderer.getTexture() != null)
                    // Load Textures from AssetPool and replacing saved textures because Gson loads Textures and creates separate Object, with broken data
                    renderer.setTexture(AssetPool.getTexture(renderer.getTexture().getFilepath()));
            }
        }
    }

    private int spriteIndex = 0;
    private float spriteFlipTime = 0.2f;
    private float spriteFlipTimeLeft = 0.0f;

    @Override
    public void update() {
        MouseControls.update();

//        if (Input.buttonDown(KeyCode.Arrow_Right))
//            camera.getPosition().x += 100f * Time.deltaTime();
//        if (Input.buttonDown(KeyCode.Arrow_Left))
//            camera.getPosition().x -= 100f * Time.deltaTime();
//
//        if (Input.buttonDown(KeyCode.Arrow_Up))
//            camera.getPosition().y += 100f * Time.deltaTime();
//        if (Input.buttonDown(KeyCode.Arrow_Down))
//            camera.getPosition().y -= 100f * Time.deltaTime();
//
//        if (spriteFlipTimeLeft <= 0) {
//            spriteFlipTimeLeft = spriteFlipTime;
//            spriteIndex++;
//            if (spriteIndex > 4)
//                spriteIndex = 0;
//            go1.getComponent(SpriteRenderer.class).setSprite(sprites.getSprite(spriteIndex));
//        } else {
//            spriteFlipTimeLeft -= Time.deltaTime();
//        }
//
//        go1.transform.position.x += 10 * Time.deltaTime();

        for (GameObject go : this.gameObjects)
            go.update();

        this.spriteRenderer.render();
    }

    @Override
    public void imgui() {
        ImGui.begin("Test Window");

        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowContentRegionMax(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x;
        for (int i = 0; i < sprites.size(); i++) {
            Sprite sprite = sprites.getSprite(i);
            float spriteWidth = sprite.getWidth() * 3;
            float spriteHeight = sprite.getHeight() * 3;
            int id = sprite.getTextureID();
            Vector2f[] texCoordinates = sprite.getTextureCoordinates();

            ImGui.pushID("AssetButton_" + id + "_" + i);
            if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoordinates[2].x, texCoordinates[0].y, texCoordinates[0].x, texCoordinates[2].y)) {
                GameObject obj = Prefabs.generateSpriteObject(sprite, 32, 32);
                MouseControls.pickUpObject(obj);
            }
            ImGui.popID();

            ImVec2 lasButtonPos = new ImVec2();
            ImGui.getItemRectMax(lasButtonPos);
            float lastButtonX2 = lasButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
            if (i + 1 < sprites.size() && nextButtonX2 < windowX2)
                ImGui.sameLine();
        }

        ImGui.end();
    }
}
