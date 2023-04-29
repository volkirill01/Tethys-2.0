package editor.stuff.inputActions;

import editor.TestFieldsWindow;
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
    private static final float startDebounceTime = 0.2f;
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

        if (holdingObject != null) {
            if (Input.buttonClick(KeyCode.Escape)) {
                holdingObject.destroy();
                holdingObject = null;
            }
        }

        if (holdingObject != null) {
            holdingObject.transform.position.x = (int) Math.floor((Input.getMouseWorldPositionX() + Settings.GRID_WIDTH / 2.0f) / Settings.GRID_WIDTH) * Settings.GRID_WIDTH;
            holdingObject.transform.position.y = (int) Math.floor((Input.getMouseWorldPositionY() + Settings.GRID_HEIGHT / 2.0f) / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT;

            if (Input.buttonDown(KeyCode.Mouse_Button_Left)) {
                float halfWidth = Settings.GRID_WIDTH / 2.0f;
                float halfHeight = Settings.GRID_HEIGHT / 2.0f;
                if (Input.isMouseDragging() && !blockInSquare(holdingObject.transform.position.x - halfWidth, holdingObject.transform.position.y - halfHeight)) {
                    place();
                } else if (!Input.isMouseDragging() && debounceTime < 0.0f) {
                    place();
                    debounceTime = startDebounceTime;
                }
            }
        } else if (!Input.isMouseDragging() && Input.buttonClick(KeyCode.Mouse_Button_Left) && debounceTime < 0.0f) {
            int x = (int) Input.getMouseScreenPositionX();
            int y = (int) Input.getMouseScreenPositionY();
            int gameObjectID = Window.getPickingTexture().readPixel(x, y);
            GameObject pickedObject = SceneManager.getCurrentScene().getGameObject(gameObjectID);
            if (pickedObject != null && pickedObject.isClickable())
                Outliner_Window.setActiveGameObject(pickedObject);
            else if (pickedObject == null)
                Outliner_Window.clearSelected();

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
            DebugDraw.addBox2D(new Vector3f(boxSelectStartWorld.x, boxSelectStartWorld.y, 0.0f).add(halfSize.x, halfSize.y, 0.0f), new Vector2f(halfSize).mul(2.0f), Settings.boxSelectionColor);
            DebugDraw.addBox2D(new Vector3f(boxSelectStartWorld.x, boxSelectStartWorld.y, 0.0f).add(halfSize.x, halfSize.y, 0.0f), new Vector2f(halfSize).mul(2.0f), Settings.boxSelectionColor);
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

    private static boolean blockInSquare(float x, float y) {
        Vector2f start = new Vector2f(x, y);
        Vector2f end = new Vector2f(start).add(Settings.GRID_WIDTH, Settings.GRID_HEIGHT);
        Vector2f startScreenF = Input.worldToScreen(start);
        Vector2f endScreenF = Input.worldToScreen(end);
        Vector2i startScreen = new Vector2i((int) startScreenF.x + 2, (int) startScreenF.y + 2); // Adding 2 pixels to go inside square and not check surrounding squares
        Vector2i endScreen = new Vector2i((int) endScreenF.x - 2, (int) endScreenF.y - 2); // Subtracting 2 pixels to go inside square and not check surrounding squares
        float[] gameObjectsIDs = Window.getPickingTexture().readPixels(startScreen, endScreen);

        for (float objID : gameObjectsIDs) {
            if (objID >= 0) { // Check if ID of gameObject is valid( >= 0 )
                GameObject pickedObj = SceneManager.getCurrentScene().getGameObject((int) objID);
                if (pickedObj.isClickable())
                    return true;
            }
        }
        return false;
    }
}
