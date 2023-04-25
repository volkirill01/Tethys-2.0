package editor.stuff.inputActions;

import editor.editor.windows.Outliner_Window;
import editor.entity.GameObject;
import editor.eventListeners.Input;
import editor.eventListeners.KeyCode;
import editor.renderer.debug.DebugDraw;
import editor.scenes.Scene;
import editor.scenes.SceneManager;
import editor.stuff.Settings;
import editor.stuff.Window;
import editor.stuff.utils.Time;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;

public class MouseControls {

    private static GameObject holdingObject = null;
    private static final float startDebounceTime = 0.05f;
    private static float debounceTime = startDebounceTime;

    private static boolean boxSelectSet = false;
    private static final Vector2f boxSelectStart = new Vector2f();
    private static final Vector2f boxSelectEnd = new Vector2f();

    public static void pickUpObject(GameObject go) {
        if (holdingObject != null)
            holdingObject.destroy();

        holdingObject = go;
        holdingObject.setSerialize(false);
        holdingObject.setClickable(false);
        SceneManager.getCurrentScene().addGameObjectToScene(go);
    }

    public static void place() {
        GameObject copy = holdingObject.copy();
        copy.setSerialize(true);
        copy.setClickable(true);
        SceneManager.getCurrentScene().addGameObjectToScene(copy);
    }

    public static void update() {
        debounceTime -= Time.deltaTime();
        Scene currentScene = SceneManager.getCurrentScene();

        if (holdingObject != null) {
            if (Input.buttonClick(KeyCode.Escape)) {
                holdingObject.destroy();
                holdingObject = null;
            }
        }

        if (holdingObject != null && debounceTime <= 0) {
            holdingObject.transform.position.x = Input.getMouseWorldPositionX() - Settings.GRID_WIDTH / 2.0f;
            holdingObject.transform.position.y = Input.getMouseWorldPositionY() - Settings.GRID_HEIGHT / 2.0f;
            holdingObject.transform.position.x = ((int) Math.floor(holdingObject.transform.position.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH) + Settings.GRID_WIDTH / 2.0f;
            holdingObject.transform.position.y = ((int) Math.floor(holdingObject.transform.position.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT) + Settings.GRID_HEIGHT / 2.0f;

            if (Input.buttonDown(KeyCode.Mouse_Button_Left)) {
                place();
                debounceTime = startDebounceTime;
            }
        } else if (Input.buttonClick(KeyCode.Mouse_Button_Left) && debounceTime < 0.0f) {
            int x = (int) Input.getMouseScreenPositionX();
            int y = (int) Input.getMouseScreenPositionY();
            int gameObjectUID = Window.getPickingTexture().readPixel(x, y);
            GameObject pickedObject = SceneManager.getCurrentScene().getGameObject(gameObjectUID);
            if (pickedObject != null && pickedObject.isClickable())
                Outliner_Window.setActiveGameObject(pickedObject);
            else if (pickedObject == null && !Input.isMouseDragging()) {
                Outliner_Window.clearSelected();
            }
            debounceTime = startDebounceTime;
        } else if (Input.isMouseDragging() && Input.buttonDown(KeyCode.Mouse_Button_Left)) {
            if (!boxSelectSet) {
                Outliner_Window.clearSelected();
                boxSelectStart.set(Input.getMouseScreenPosition());
                boxSelectSet = true;
            }
            boxSelectEnd.set(Input.getMouseScreenPosition());
            Vector2f boxSelectStartWorld = Input.screenToWorld(boxSelectStart);
            Vector2f boxSelectEndWorld = Input.screenToWorld(boxSelectEnd);
            Vector2f halfSize = new Vector2f(boxSelectEndWorld).sub(boxSelectStartWorld).div(2.0f);
            DebugDraw.addBox2D(new Vector3f(boxSelectStartWorld.x, boxSelectStartWorld.y, 0.0f).add(halfSize.x, halfSize.y, 0.0f), new Vector2f(halfSize).mul(2.0f));
        } else if (boxSelectSet) {
            boxSelectSet = false;
            int screenStartX = (int) boxSelectStart.x;
            int screenStartY = (int) boxSelectStart.y;
            int screenEndX = (int) boxSelectEnd.x;
            int screenEndY = (int) boxSelectEnd.y;
            boxSelectStart.zero();
            boxSelectEnd.zero();

            if (screenEndX < screenStartX) {
                int tmp = screenStartX;
                screenStartX = screenEndX;
                screenEndX = tmp;
            }
            if (screenEndY < screenStartY) {
                int tmp = screenStartY;
                screenStartY = screenEndY;
                screenEndY = tmp;
            }

            float[] gameObjectsIDs = Window.getPickingTexture().readPixels(new Vector2i(screenStartX, screenStartY), new Vector2i(screenEndX, screenEndY));
            Set<Integer> uniqueGameObjectsIDs = new HashSet<>();
            for (float objID : gameObjectsIDs)
                uniqueGameObjectsIDs.add((int) objID);

            for (Integer objID : uniqueGameObjectsIDs) {
                GameObject pickedObject = SceneManager.getCurrentScene().getGameObject(objID);
                if (pickedObject != null && pickedObject.isClickable())
                    Outliner_Window.addActiveGameObject(pickedObject);
            }
        }
    }
}
