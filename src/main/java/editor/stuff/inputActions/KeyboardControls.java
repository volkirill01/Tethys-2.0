package editor.stuff.inputActions;

import editor.editor.windows.Outliner_Window;
import editor.entity.GameObject;
import editor.eventListeners.Input;
import editor.eventListeners.KeyCode;
import editor.scenes.SceneManager;
import editor.stuff.Settings;
import editor.stuff.Window;

import java.util.ArrayList;
import java.util.List;

public class KeyboardControls {

    public static void update() {
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
                return;
            } else {
                GameObject copy = Outliner_Window.getActiveGameObject().copy();
                copy.transform.position.add(Settings.GRID_WIDTH, 0.0f, 0.0f);
                SceneManager.getCurrentScene().addGameObjectToScene(copy);
                Outliner_Window.setActiveGameObject(copy);
                return;
            }
        }

        if (Input.buttonClick(KeyCode.Delete)) {
            for (GameObject obj : activeGameObjects)
                obj.destroy();

            Outliner_Window.clearSelected();
            return;
        }
    }
}
