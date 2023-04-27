package editor.scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import editor.entity.GameObject;
import editor.entity.GameObjectDeserializer;
import editor.entity.component.Component;
import editor.entity.component.ComponentDeserializer;
import editor.entity.component.components.Transform;
import editor.gizmo.GizmoSystem;
import editor.physics.physics2D.Physics2D;
import editor.renderer.Camera;
import editor.renderer.EditorCamera;
import editor.renderer.renderer2D.SpriteMasterRenderer;
import editor.stuff.inputActions.KeyboardControls;
import editor.stuff.inputActions.MouseControls;
import editor.stuff.utils.EditorGson;
import org.joml.Vector3f;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Scene {

    private Camera camera;
    private boolean isRunning = false;

    private final SpriteMasterRenderer spriteRenderer = new SpriteMasterRenderer();
    private final Physics2D physics2D = new Physics2D();

    private final List<GameObject> gameObjects = new ArrayList<>();
    private final List<GameObject> pendingGameObjects = new ArrayList<>(); // Object we want to add to scene, but not in middle of the frame(Decrease chance for bugs)

    private final SceneInitializer sceneInitializer;
    private EditorCamera editorCamera;

    private GizmoSystem gizmoSystem;

    public Scene(SceneInitializer sceneInitializer) { this.sceneInitializer = sceneInitializer; }

    public void init() {
        this.sceneInitializer.loadResources(this);
        this.sceneInitializer.init(this);
        this.camera = new Camera(new Vector3f(0.0f, 0.0f, 0.0f));
        this.editorCamera = new EditorCamera(SceneManager.getCurrentScene().getCamera());

        gizmoSystem = new GizmoSystem();
        gizmoSystem.init();
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

    public void render() { this.spriteRenderer.render(); }

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

    public void destroy() {
        for (GameObject obj : this.gameObjects)
            obj.destroy();
    }

    public Camera getCamera() { return this.camera; }

    public void imgui() { this.sceneInitializer.imgui(); } // TODO MOVE THIS, ITS NOT BE THERE

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
            throw new RuntimeException("Error in saving Scene - '" + "level.txt" + "'", e);
        }
    }

    public void load() {
        Gson gson = EditorGson.getGsonBuilder();

        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get("level.txt")));
        } catch (IOException e) {
            e.printStackTrace();
//            throw new RuntimeException("Error in loading Scene - '" + "level.txt" + "'", e);
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
