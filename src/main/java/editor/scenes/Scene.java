package editor.scenes;

import TMP_MARIO_STUFF.Prefabs;
import com.google.gson.Gson;
import editor.assets.AssetPool;
import editor.audio.Sound;
import editor.entity.GameObject;
import editor.entity.component.Component;
import editor.renderer.renderer2D.SpriteRenderer;
import editor.entity.component.Transform;
import editor.gizmo.GizmoSystem;
import editor.physics.physics2D.Physics2D;
import editor.renderer.camera.BaseCamera;
import editor.renderer.camera.Camera;
import editor.renderer.camera.EditorCamera;
import editor.renderer.renderer2D.SpriteMasterRenderer;
import editor.renderer.renderer2D.sprite.Sprite;
import editor.renderer.renderer2D.sprite.SpriteSheet;
import editor.stuff.Settings;
import editor.stuff.inputActions.MouseControls;
import editor.stuff.utils.EditorGson;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class Scene {

    private BaseCamera camera;
    private final String filepath;
    private boolean isRunning = false;

    private final SpriteMasterRenderer spriteRenderer = new SpriteMasterRenderer(); // TODO COMBINE SPRITE RENDERER AND MESH RENDER AND CREATE ENTITY RENDERER
    private final Physics2D physics2D = new Physics2D();

    private final List<GameObject> gameObjects = new ArrayList<>();
    private final List<GameObject> pendingGameObjects = new ArrayList<>(); // Object we want to add to scene, but not in middle of the frame(Decrease chance for bugs)

    private EditorCamera editorCamera;

    private GizmoSystem gizmoSystem;

    private SpriteSheet sprites;

    public Scene(String filepath) { this.filepath = filepath; }

    public void init() {
        loadResources();
        this.camera = new BaseCamera(new Vector3f(0.0f, 0.0f, 0.0f));
        this.editorCamera = new EditorCamera(SceneManager.getCurrentScene().getCamera());

        gizmoSystem = new GizmoSystem();
        gizmoSystem.init();

        sprites = AssetPool.getSpriteSheet("Assets/decorationsAndBlocks.png");
    }

    private void loadResources() {
        AssetPool.addSpriteSheet("Assets/decorationsAndBlocks.png",
                new SpriteSheet(AssetPool.getTexture("Assets/decorationsAndBlocks.png"),
                        16, 16, 81, 0, 0, 0, 0));

        AssetPool.addSpriteSheet("editorFiles/gizmos.png",
                new SpriteSheet(AssetPool.getTexture("editorFiles/gizmos.png"),
                        24, 48, 3, 0, 0, 0, 0));

        AssetPool.addSound("Assets/sounds/main-theme-overworld.ogg", true);
        AssetPool.addSound("Assets/sounds/flagpole.ogg", false);
        AssetPool.addSound("Assets/sounds/break_block.ogg", false);
        AssetPool.addSound("Assets/sounds/bump.ogg", false);
        AssetPool.addSound("Assets/sounds/coin.ogg", false);
        AssetPool.addSound("Assets/sounds/gameover.ogg", false);
        AssetPool.addSound("Assets/sounds/jump-small.ogg", false);
        AssetPool.addSound("Assets/sounds/mario_die.ogg", false);
        AssetPool.addSound("Assets/sounds/pipe.ogg", false);
        AssetPool.addSound("Assets/sounds/powerup.ogg", false);
        AssetPool.addSound("Assets/sounds/powerup_appears.ogg", false);
        AssetPool.addSound("Assets/sounds/stage_clear.ogg", false);
        AssetPool.addSound("Assets/sounds/stomp.ogg", false);
        AssetPool.addSound("Assets/sounds/kick.ogg", false);
        AssetPool.addSound("Assets/sounds/invincible.ogg", false);

        for (GameObject go : this.gameObjects) {
            if (go.hasComponent(SpriteRenderer.class)) {
                SpriteRenderer renderer = go.getComponent(SpriteRenderer.class);
                if (renderer.getTexture() != null)
                    // Load Textures from AssetPool and replacing saved textures because Gson loads Textures and creates separate Object, with broken data
                    renderer.setTexture(AssetPool.getTexture(renderer.getTexture().getFilepath()));
            }
        }
    }

    public void editorUpdate() {
        this.editorCamera.update();
        SceneManager.getCurrentScene().getCamera().adjustProjection();

        gizmoSystem.update();

        for (int i = 0; i < this.gameObjects.size(); i++) {
            GameObject obj = this.gameObjects.get(i);
            obj.editorUpdate();

            if (obj.isDeath()) {
                this.gameObjects.remove(i);
                this.spriteRenderer.destroyGameObject(obj);
                this.physics2D.destroyGameObject(obj);
                i--;
            }
        }

        for (GameObject obj : this.pendingGameObjects) {
            this.gameObjects.add(obj);
            obj.start();
            this.spriteRenderer.add(obj);
            this.physics2D.add(obj);
        }
        this.pendingGameObjects.clear();
    }

    public void update() {
        this.editorCamera.update();
        SceneManager.getCurrentScene().getCamera().adjustProjection();

        gizmoSystem.update();

        // Update physics only in runtime
        this.physics2D.update();

        for (int i = 0; i < this.gameObjects.size(); i++) {
            GameObject obj = this.gameObjects.get(i);
            obj.update();

            if (obj.isDeath()) {
                this.gameObjects.remove(i);
                this.spriteRenderer.destroyGameObject(obj);
                this.physics2D.destroyGameObject(obj);
                i--;
            }
        }

        for (GameObject obj : this.pendingGameObjects) {
            this.gameObjects.add(obj);
            obj.start();
            this.spriteRenderer.add(obj);
            this.physics2D.add(obj);
        }
        this.pendingGameObjects.clear();
    }

    public void render(Matrix4f projectionMatrix, Matrix4f viewMatrix) {
        this.spriteRenderer.render(projectionMatrix, viewMatrix);
    }

    public void start() {
        for (GameObject go : this.gameObjects) {
            go.start();
            this.spriteRenderer.add(go);
            this.physics2D.add(go);
        }
        this.isRunning = true;
    }

    public GameObject createGameObject(String name) {
        GameObject go = new GameObject(name);
        go.addComponent(new Transform());
        go.transform = go.getComponent(Transform.class);
        return go;
    }

    public void addGameObjectToScene(GameObject obj) {
        if (!isRunning)
            this.gameObjects.add(obj);
        else
            this.pendingGameObjects.add(obj);
    }

    public GameObject getGameObject(int uid) {
        Optional<GameObject> result = this.gameObjects.stream().filter(gameObject -> gameObject.getUid() == uid).findFirst();
        return result.orElse(null);
    }

    public GameObject getGameObject(String name) {
        Optional<GameObject> result = this.gameObjects.stream().filter(gameObject -> gameObject.name.equals(name)).findFirst();
        return result.orElse(null);
    }

    public <T extends Component> GameObject getGameObjectWithComponent(Class<T> _class) {
        for (GameObject obj : this.gameObjects)
            if (obj.hasComponent(_class))
                return obj;

        return null;
    }

    public List<GameObject> getAllGameObjects() { return this.gameObjects; }

    public List<Camera> getAllCameras() {
        List<Camera> cameras = new ArrayList<>();
        for (GameObject obj : this.gameObjects)
            if (obj.hasComponent(Camera.class))
                cameras.add(obj.getComponent(Camera.class));
        return cameras;
    }

    public Camera getMainCamera() {
        for (GameObject obj : this.gameObjects)
            if (obj.hasComponent(Camera.class) && obj.getComponent(Camera.class).isMain())
                return obj.getComponent(Camera.class);
        return null;
    }

    public void destroy() {
        for (GameObject obj : this.gameObjects)
            obj.destroy();
    }

    public String getFilepath() { return this.filepath; }

    public BaseCamera getCamera() { return this.camera; }

    public void imgui() { // TODO MOVE THIS, ITS NOT BE THERE
        ImGui.begin("Test Window");

        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowContentRegionMax(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x;
        ImGui.beginTabBar("Objects");

        if (ImGui.beginTabItem("Tiles")) {
            for (int i = 0; i < sprites.size(); i++) {
                Sprite sprite = sprites.getSprite(i);
                float spriteWidth = sprite.getWidth() * 3;
                float spriteHeight = sprite.getHeight() * 3;
                int id = sprite.getTextureID();
                Vector2f[] texCoordinates = sprite.getTextureCoordinates();

                ImGui.pushID("TileButton_" + i);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoordinates[2].x, texCoordinates[0].y, texCoordinates[0].x, texCoordinates[2].y)) {
                    GameObject obj = Prefabs.generateSpriteObject(sprite, Settings.GRID_WIDTH, Settings.GRID_HEIGHT);
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
            ImGui.endTabItem();
        }
        if (ImGui.beginTabItem("Sounds")) {
            Collection<Sound> sounds = AssetPool.getAllSounds();

            int i = 0;
            for (Sound sound : sounds) {
                String soundName = sound.getFilepath().split("/")[sound.getFilepath().split("/").length - 1];

                float spriteWidth = ImGui.calcTextSize(soundName).x + 16.0f * 2;
                float spriteHeight = 16.0f * 3;
                int id = ImGui.getID(sound.getFilepath());

                ImGui.pushID("SoundButton_" + id);
                if (ImGui.button(soundName, spriteWidth, spriteHeight)) {
                    if (sound.isPlaying())
                        sound.stop();
                    else
                        sound.play();
                }
                ImGui.popID();

                ImVec2 lasButtonPos = new ImVec2();
                ImGui.getItemRectMax(lasButtonPos);
                float lastButtonX2 = lasButtonPos.x;
                float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                if (i + 1 < sounds.size() && nextButtonX2 < windowX2)
                    ImGui.sameLine();
                i++;
            }
            ImGui.endTabItem();
        }
        ImGui.endTabBar();

        ImGui.end();
    }

    public void saveAs(String filepath) {
        Gson gson = EditorGson.getGsonBuilder();

        try {
            FileWriter writer = new FileWriter(filepath);
            List<GameObject> objectsToSerialize = new ArrayList<>();
            for (GameObject obj : this.gameObjects)
                if (obj.isDoSerialization())
                    objectsToSerialize.add(obj);

            writer.write(gson.toJson(objectsToSerialize));
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Error in saving Scene - '" + filepath + "'", e);
        }
    }

    public void load(String filepath) {
        Gson gson = EditorGson.getGsonBuilder();

        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get(filepath)));
        } catch (IOException e) {
            e.printStackTrace();
//            throw new RuntimeException("Error in loading Scene - '" + filepath + "'", e);
        }

        if (!inFile.equals("")) {
            int maxGameObjectID = -1;
            int maxComponentID = -1;

            GameObject[] objs = gson.fromJson(inFile, GameObject[].class);
            for (GameObject obj : objs) {
                addGameObjectToScene(obj);

                for (Component c : obj.getAllComponents())
                    if (c.getUid() > maxComponentID)
                        maxComponentID = c.getUid();

                if (obj.getUid() > maxGameObjectID)
                    maxGameObjectID = obj.getUid();
            }

            maxGameObjectID++;
            maxComponentID++;
            GameObject.init(maxGameObjectID);
            Component.init(maxComponentID);
        } else {
            // TODO LOAD DEFAULT EMPTY SCENE
//            throw new NullPointerException("Scene is Empty - '" + "level.txt" + "'");
        }
    }

    public Physics2D getPhysics2D() { return this.physics2D; }
}
