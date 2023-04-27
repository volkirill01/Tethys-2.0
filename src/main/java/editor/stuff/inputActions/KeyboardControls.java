package editor.stuff.inputActions;

import editor.editor.windows.Outliner_Window;
import editor.entity.GameObject;
import editor.eventListeners.Input;
import editor.eventListeners.KeyCode;
import editor.scenes.SceneManager;
import editor.stuff.Settings;
import editor.stuff.utils.Time;

import java.util.ArrayList;
import java.util.List;

public class KeyboardControls {

    private static final float startDebounce = 0.2f;
    private static float debounce = startDebounce;

    private static final float smallMoveMultiplayer = 0.1f;
    private static final float normalMoveMultiplayer = 1.0f;
    private static float currentMoveMultiplayer = normalMoveMultiplayer;

    public static void update() {
        debounce -= Time.deltaTime();

        List<GameObject> activeGameObjects = Outliner_Window.getActiveGameObjects();

        if (Input.buttonDown(KeyCode.Left_Control) && Input.buttonClick(KeyCode.D)) {
            if (activeGameObjects.size() > 1) {
                Outliner_Window.clearSelected();
                List<GameObject> gameObjects = new ArrayList<>(activeGameObjects);

                for (GameObject obj : gameObjects) {
                    GameObject copy = obj.copy();
                    SceneManager.getCurrentScene().addGameObjectToScene(copy);
                    Outliner_Window.addActiveGameObject(copy);
                }
            } else {
                GameObject copy = Outliner_Window.getActiveGameObject().copy();
                copy.transform.position.add(Settings.GRID_WIDTH, 0.0f, 0.0f);
                SceneManager.getCurrentScene().addGameObjectToScene(copy);
                Outliner_Window.setActiveGameObject(copy);
            }
            return;
        }

        if (Input.buttonClick(KeyCode.Delete)) {
            for (GameObject obj : activeGameObjects)
                obj.destroy();

            Outliner_Window.clearSelected();
            return;
        }

        if (Input.buttonDown(KeyCode.Page_Down) && debounce < 0.0f) {
            debounce = startDebounce;

            for (GameObject obj : activeGameObjects)
                obj.transform.setZIndex(obj.transform.getZIndex() - 1);
            return;
        }
        if (Input.buttonDown(KeyCode.Page_Up) && debounce < 0.0f) {
            debounce = startDebounce;

            for (GameObject obj : activeGameObjects)
                obj.transform.setZIndex(obj.transform.getZIndex() + 1);
            return;
        }

        if (Input.buttonDown(KeyCode.Left_Shift) || Input.buttonDown(KeyCode.Right_Shift))
            currentMoveMultiplayer = smallMoveMultiplayer;
        else
            currentMoveMultiplayer = normalMoveMultiplayer;

        if (Input.buttonDown(KeyCode.Arrow_Up) && debounce < 0.0f) {
            debounce = startDebounce;

            for (GameObject obj : activeGameObjects)
                obj.transform.position.y += Settings.GRID_HEIGHT * currentMoveMultiplayer;
            return;
        }
        if (Input.buttonDown(KeyCode.Arrow_Down) && debounce < 0.0f) {
            debounce = startDebounce;

            for (GameObject obj : activeGameObjects)
                obj.transform.position.y -= Settings.GRID_HEIGHT * currentMoveMultiplayer;
            return;
        }
        if (Input.buttonDown(KeyCode.Arrow_Left) && debounce < 0.0f) {
            debounce = startDebounce;

            for (GameObject obj : activeGameObjects)
                obj.transform.position.x -= Settings.GRID_WIDTH * currentMoveMultiplayer;
            return;
        }
        if (Input.buttonDown(KeyCode.Arrow_Right) && debounce < 0.0f) {
            debounce = startDebounce;

            for (GameObject obj : activeGameObjects)
                obj.transform.position.x += Settings.GRID_WIDTH * currentMoveMultiplayer;
            return;
        }
    }
}
