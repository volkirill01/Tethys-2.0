package editor.scene;

import editor.entity.GameObject;
import editor.renderer.Camera;
import editor.renderer.SpriteMasterRenderer;

import java.util.ArrayList;
import java.util.List;

public abstract class EditorScene {

    protected Camera camera;
    private boolean isRunning = false;

    protected SpriteMasterRenderer spriteRenderer = new SpriteMasterRenderer();

    protected List<GameObject> gameObjects = new ArrayList<>();

    public EditorScene() { }

    public abstract void init();

    public abstract void update();

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

    public Camera getCamera() { return this.camera; }
}
