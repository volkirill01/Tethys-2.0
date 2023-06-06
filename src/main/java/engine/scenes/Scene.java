package engine.scenes;

import TMP_MARIO_STUFF.Prefabs;
import com.google.gson.Gson;
import engine.assets.AssetPool;
import engine.audio.Sound;
import engine.editor.windows.Outliner_Window;
import engine.entity.GameObject;
import engine.entity.component.Component;
import engine.gizmo.ed_GizmoSystem;
import engine.logging.DebugLog;
import engine.profiling.Profiler;
import engine.renderer.EntityRenderer;
import engine.renderer.renderer2D.SpriteRenderer;
import engine.entity.component.Transform;
import engine.physics.physics2D.Physics2D;
import engine.renderer.camera.ed_EditorCamera;
import engine.renderer.camera.Camera;
import engine.renderer.renderer2D.ed_Renderer;
import engine.renderer.renderer2D.sprite.Sprite;
import engine.renderer.renderer2D.sprite.SpriteSheet;
import engine.renderer.renderer3D.MeshRenderer;
import engine.stuff.Settings;
import engine.stuff.inputActions.ed_KeyboardControls;
import engine.stuff.inputActions.ed_MouseControls;
import engine.stuff.utils.EditorGson;
import imgui.ImGui;
import imgui.ImVec2;
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

    private final String filepath;
    private boolean isRunning = false;

    private final List<GameObject> gameObjects = new ArrayList<>();
    private final List<GameObject> pendingGameObjects = new ArrayList<>(); // Object we want to add to scene, but not in middle of the frame(Decrease chance for bugs)

    private final GameObject editorStuff = new GameObject("EditorStuff");

    private SpriteSheet sprites;

    public Scene(String filepath) { this.filepath = filepath; }

    public void init() {
        DebugLog.logInfo("Scene:Init: ", this.filepath);

        Profiler.startTimer(String.format("Init Scene - '%s'", this.filepath.replace("\\", "/")));
        loadResources();
        this.editorStuff.addComponent(new ed_KeyboardControls());
        this.editorStuff.addComponent(new ed_MouseControls());
        this.editorStuff.addComponent(new ed_EditorCamera(new Vector3f(0.0f), new Vector3f(0.0f)));
        this.editorStuff.addComponent(new ed_GizmoSystem());

        this.editorStuff.start();

        this.sprites = AssetPool.getSpriteSheet("Assets/decorationsAndBlocks.png");
        Profiler.stopTimer(String.format("Init Scene - '%s'", this.filepath.replace("\\", "/")));
    }

    private void loadResources() {
        Profiler.startTimer(String.format("Scene LoadResources - '%s'", this.filepath.replace("\\", "/")));
        AssetPool.addSpriteSheet("Assets/decorationsAndBlocks.png",
                new SpriteSheet(AssetPool.getTexture("Assets/decorationsAndBlocks.png"),
                        16, 16, 81, 0, 0, 0, 0));

        AssetPool.addSpriteSheet("Resources/gizmos.png",
                new SpriteSheet(AssetPool.getTexture("Resources/gizmos.png"),
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
                if (renderer.getSprite() != AssetPool.getDefaultSprite() && !renderer.getSprite().getTexture().getFilepath().equals("_GENERATED_"))
                    if (renderer.getSprite().getTexture() != null) {
                        // Load Textures from AssetPool and replacing saved textures because Gson loads Textures and creates separate Object, with broken data
                        renderer.getSprite().setTexture(AssetPool.getTexture(renderer.getSprite().getTexture().getFilepath()));
                    }
            }
            if (go.hasComponent(MeshRenderer.class)) {
                MeshRenderer renderer = go.getComponent(MeshRenderer.class);
                if (renderer.getMesh() != null)
                    // Load Meshes from AssetPool and replacing saved meshes because Gson loads Meshes and creates separate Object, with broken data
                    renderer.setMesh(AssetPool.getMesh(renderer.getMesh().getFilepath()));
            }
        }
        Profiler.stopTimer(String.format("Scene LoadResources - '%s'", this.filepath.replace("\\", "/")));
    }

    public void editorUpdate() {
        Profiler.startTimer(String.format("Scene EditorUpdate - '%s'", this.filepath.replace("\\", "/")));
        this.editorStuff.update();
        SceneManager.getCurrentScene().getEditorCamera().adjustProjectionMatrix();

        for (int i = 0; i < this.gameObjects.size(); i++) {
            GameObject obj = this.gameObjects.get(i);
            obj.editorUpdate();

            if (obj.isDeath()) {
                if (Outliner_Window.getActiveGameObjects().contains(this.gameObjects.get(i)))
                    Outliner_Window.removeActiveGameObject(this.gameObjects.get(i));
                this.gameObjects.remove(i);
                EntityRenderer.destroyGameObject(obj, ed_Renderer.class);
                Physics2D.destroyGameObject(obj);
                i--;
            }
        }

        for (GameObject obj : this.pendingGameObjects) {
            this.gameObjects.add(obj);
            obj.start();
            EntityRenderer.add(obj);
            Physics2D.add(obj);
        }
        this.pendingGameObjects.clear();
        Profiler.stopTimer(String.format("Scene EditorUpdate - '%s'", this.filepath.replace("\\", "/")));
    }

    public void update() {
        Profiler.startTimer(String.format("Scene RuntimeUpdate - '%s'", this.filepath.replace("\\", "/")));
        this.editorStuff.update();

        // Update physics only in runtime
        Physics2D.update();

        for (int i = 0; i < this.gameObjects.size(); i++) {
            GameObject obj = this.gameObjects.get(i);
            obj.update();

            if (obj.isDeath()) {
                if (Outliner_Window.getActiveGameObjects().contains(this.gameObjects.get(i)))
                    Outliner_Window.removeActiveGameObject(this.gameObjects.get(i));
                this.gameObjects.remove(i);
                EntityRenderer.destroyGameObject(obj, ed_Renderer.class);
                Physics2D.destroyGameObject(obj);
                i--;
            }
        }

        for (GameObject obj : this.pendingGameObjects) {
            this.gameObjects.add(obj);
            obj.start();
            EntityRenderer.add(obj);
            Physics2D.add(obj);
        }
        this.pendingGameObjects.clear();
        Profiler.stopTimer(String.format("Scene RuntimeUpdate - '%s'", this.filepath.replace("\\", "/")));
    }

    public void start() {
        DebugLog.log("Scene:Start: ", this.filepath);

        Profiler.startTimer(String.format("Scene Start - '%s'", this.filepath.replace("\\", "/")));
        for (GameObject obj : this.gameObjects) {
            obj.start();
            EntityRenderer.add(obj);
            Physics2D.add(obj);
        }
        this.isRunning = true;
        Profiler.stopTimer(String.format("Scene Start - '%s'", this.filepath.replace("\\", "/")));
    }

    public GameObject createGameObject(String name) {
        DebugLog.logInfo("Scene:CreateGameObject: ", this.filepath, ", object name: ", name);

        GameObject obj = new GameObject(name);
        obj.addComponent(new Transform());
        obj.transform = obj.getComponent(Transform.class);
        return obj;
    }

    public void addGameObjectToScene(GameObject obj) {
        if (!this.isRunning)
            this.gameObjects.add(obj);
        else
            this.pendingGameObjects.add(obj);
    }

    public List<GameObject> getAllGameObjects() { return this.gameObjects; }

    public GameObject getGameObject(int incrementID) {
        Optional<GameObject> result = this.gameObjects.stream().filter(gameObject -> gameObject.getIncrementedID() == incrementID).findFirst();
        return result.orElse(null);
    }

    public GameObject getGameObject(String name) {
        Optional<GameObject> result = this.gameObjects.stream().filter(gameObject -> gameObject.getName().equals(name)).findFirst();
        return result.orElse(null);
    }

    public <T extends Component> GameObject getGameObjectWithComponent(Class<T> type) {
        for (GameObject obj : this.gameObjects)
            if (obj.hasComponent(type))
                return obj;
        return null;
    }

    public <T extends Component> List<GameObject> getAllGameObjectsWithComponent(Class<T> type) {
        List<GameObject> result = new ArrayList<>();
        for (GameObject obj : this.gameObjects)
            if (obj.hasComponent(type))
                result.add(obj);
        return result;
    }

    public <T extends Component> List<Component> getAllComponents(Class<T> type) {
        List<Component> result = new ArrayList<>();
        for (GameObject obj : this.gameObjects)
            if (obj.hasComponent(type))
                result.add(obj.getComponent(type));
        return result;
    }

    public Camera getMainCamera() {
        for (GameObject obj : this.gameObjects)
            if (obj.hasComponent(Camera.class) && obj.getComponent(Camera.class).isMain())
                return obj.getComponent(Camera.class);
        return null;
    }

    public void destroy() {
        DebugLog.logInfo("Scene:Destroy: ", this.filepath);

        Profiler.startTimer(String.format("Destroy Scene - '%s'", this.filepath.replace("\\", "/")));
        for (GameObject obj : this.gameObjects)
            obj.destroy();
        Profiler.stopTimer(String.format("Destroy Scene - '%s'", this.filepath.replace("\\", "/")));
    }

    public String getFilepath() { return this.filepath; }

    public ed_EditorCamera getEditorCamera() { return this.editorStuff.getComponent(ed_EditorCamera.class); }

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
            if (ImGui.imageButton(0, 16, 16, 0, 1, 1, 0)) {
                GameObject obj = Prefabs.generateObject(Settings.GRID_WIDTH, Settings.GRID_HEIGHT);
                this.editorStuff.getComponent(ed_MouseControls.class).pickUpObject(obj);
            }

            for (int i = 0; i < this.sprites.size(); i++) {
                Sprite sprite = this.sprites.getSprite(i);
                float spriteWidth = sprite.getWidth() * 3;
                float spriteHeight = sprite.getHeight() * 3;
                int id = sprite.getTexture().getTextureID();
                Vector2f[] texCoordinates = sprite.getTextureCoordinates();

                ImGui.pushID("TileButton_" + i);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoordinates[2].x, texCoordinates[0].y, texCoordinates[0].x, texCoordinates[2].y)) {
                    GameObject obj = Prefabs.generateSpriteObject(sprite, Settings.GRID_WIDTH, Settings.GRID_HEIGHT);
                    this.editorStuff.getComponent(ed_MouseControls.class).pickUpObject(obj);
                }
                ImGui.popID();

                ImVec2 lasButtonPos = new ImVec2();
                ImGui.getItemRectMax(lasButtonPos);
                float lastButtonX2 = lasButtonPos.x;
                float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                if (i + 1 < this.sprites.size() && nextButtonX2 < windowX2)
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

    public void save() { saveAs(this.filepath); }

    public void saveAs(String filepath) {
        DebugLog.logInfo("Scene:SaveAs: ", this.filepath, ", save path: ", filepath);

        Profiler.startTimer(String.format("SaveAs Scene - '%s'", this.filepath.replace("\\", "/")));
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
            throw new RuntimeException(String.format("Error in saving Scene - '%s'", filepath), e);
        }
        Profiler.stopTimer(String.format("SaveAs Scene - '%s'", this.filepath.replace("\\", "/")));
    }

    public void load(String filepath) {
        DebugLog.logInfo("Scene:Load: ", this.filepath, ", load path: ", filepath);

        Profiler.startTimer(String.format("Load Scene - '%s'", this.filepath.replace("\\", "/")));
        Gson gson = EditorGson.getGsonBuilder();

        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get(filepath)));
        } catch (IOException e) {
            DebugLog.logError("Not found Scene file - ", filepath);
        }

        if (!inFile.equals("")) {
            EntityRenderer.clear();

            int maxGameObjectID = -1;
            int maxComponentID = -1;

            GameObject[] objs = gson.fromJson(inFile, GameObject[].class);
            for (GameObject obj : objs) {
                addGameObjectToScene(obj);

                for (Component c : obj.getAllComponents())
                    if (c.getUid() > maxComponentID)
                        maxComponentID = c.getUid();

                if (obj.getIncrementedID() > maxGameObjectID)
                    maxGameObjectID = obj.getIncrementedID();
            }

            maxGameObjectID++;
            maxComponentID++;
            GameObject.init(maxGameObjectID);
            Component.init(maxComponentID);
        }
//        else {
//            // TODO LOAD DEFAULT EMPTY SCENE
//            throw new NullPointerException("Scene is Empty - '" + "level.txt" + "'");
//        }
        Profiler.stopTimer(String.format("Load Scene - '%s'", this.filepath.replace("\\", "/")));
    }
}
