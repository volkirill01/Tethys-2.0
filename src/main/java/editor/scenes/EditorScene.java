package editor.scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import editor.entity.GameObject;
import editor.entity.GameObjectDeserializer;
import editor.entity.component.Component;
import editor.entity.component.ComponentDeserializer;
import editor.renderer.Camera;
import editor.renderer.renderer2D.SpriteMasterRenderer;
import imgui.ImGui;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class EditorScene {

    protected Camera camera;
    private boolean isRunning = false;

    protected SpriteMasterRenderer spriteRenderer = new SpriteMasterRenderer();

    protected List<GameObject> gameObjects = new ArrayList<>();

    protected boolean levelLoaded = false;

    public EditorScene() { }

    public abstract void init();

    public abstract void update();

    public abstract void render();

    public void start() {
        for (GameObject go : this.gameObjects) {
            go.start();
            this.spriteRenderer.add(go);
        }
        this.isRunning = true;
    }

    public void addGameObjectToScene(GameObject go) {
        if (!isRunning)
            this.gameObjects.add(go);
        else {
            this.gameObjects.add(go);
            go.start();
            this.spriteRenderer.add(go);
        }
    }

    public GameObject getGameObject(int uid) {
        Optional<GameObject> result = this.gameObjects.stream().filter(gameObject -> gameObject.getUid() == uid).findFirst();
        return result.orElse(null);
    }

    public Camera getCamera() { return this.camera; }

    // TODO MOVE THIS, ITS NOT BE THERE
    public void imgui() {

    }

    public void save() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .create();

        try {
            FileWriter writer = new FileWriter("level.txt");
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
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .create();

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
            this.levelLoaded = true;
        } else {
            // TODO LOAD DEFAULT EMPTY SCENE
//            throw new NullPointerException("Scene is Empty - '" + "level.txt" + "'");
        }
    }
}
