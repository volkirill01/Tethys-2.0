package editor.stuff.inputActions;

import editor.entity.GameObject;
import editor.eventListeners.Input;
import editor.eventListeners.KeyCode;
import editor.scenes.SceneManager;
import editor.stuff.Settings;

public class MouseControls {

    private static GameObject holdingObject = null;

    public static void pickUpObject(GameObject go) {
        holdingObject = go;
        SceneManager.getCurrentScene().addGameObjectToScene(go);
    }

    public static void place() { holdingObject = null; }

    public static void update() {
        if (holdingObject == null)
            return;

        holdingObject.transform.position.x = Input.getMouseOrthographicXPosition() - 16;
        holdingObject.transform.position.y = Input.getMouseOrthographicYPosition() - 16;
        holdingObject.transform.position.x = (int) (holdingObject.transform.position.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH;
        holdingObject.transform.position.y = (int) (holdingObject.transform.position.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT;

        if (Input.buttonDown(KeyCode.Mouse_Button_Left))
            place();
    }
}
