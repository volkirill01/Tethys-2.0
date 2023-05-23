package engine.stuff.inputActions;

import engine.editor.gui.EngineGuiLayer;
import engine.editor.windows.Outliner_Window;
import engine.entity.GameObject;
import engine.entity.component.Component;
import engine.eventListeners.Input;
import engine.eventListeners.KeyCode;
import engine.gizmo.ed_GizmoSystem;
import engine.renderer.debug.DebugDraw;
import engine.scenes.SceneManager;
import engine.stuff.Settings;
import engine.stuff.Window;
import engine.stuff.utils.Time;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;

public class ed_MouseControls extends Component {

    private GameObject holdingObject = null;
    private final float startDebounceTime = 0.2f;
    private float debounceTime = this.startDebounceTime;

    private boolean boxSelectSet = false;
    private final Vector2f boxSelectStart = new Vector2f();
    private final Vector2f boxSelectEnd = new Vector2f();

    public void pickUpObject(GameObject go) {
        if (this.holdingObject != null)
            this.holdingObject.destroy();

        this.holdingObject = go;
        this.holdingObject.setSerialize(false);
        this.holdingObject.setClickable(false);
        SceneManager.getCurrentScene().addGameObjectToScene(go);
    }

    public void place() {
        GameObject copy = this.holdingObject.copy();
        copy.setSerialize(true);
        copy.setClickable(true);
        SceneManager.getCurrentScene().addGameObjectToScene(copy);
    }

    @Override
    public void update() {
        this.debounceTime -= Time.deltaTime();

        if (this.holdingObject != null) {
            if (Input.buttonClick(KeyCode.Escape)) {
                this.holdingObject.destroy();
                this.holdingObject = null;
            }
        }

        if (!EngineGuiLayer.isSceneWindowHovered() && !EngineGuiLayer.getWantCaptureMouse()) {
            this.boxSelectStart.set(new Vector2f());
            this.boxSelectEnd.set(new Vector2f());
            if (this.holdingObject != null) {
                this.holdingObject.transform.position.x = (int) Math.floor((Input.getMouseWorldPositionX() + Settings.GRID_WIDTH / 2.0f) / Settings.GRID_WIDTH) * Settings.GRID_WIDTH;
                this.holdingObject.transform.position.y = (int) Math.floor((Input.getMouseWorldPositionY() + Settings.GRID_HEIGHT / 2.0f) / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT;
            }
            return;
        }

        if (ed_GizmoSystem.isGizmoActive())
            return;

        if (this.holdingObject != null) {
            this.holdingObject.transform.position.x = (int) Math.floor((Input.getMouseWorldPositionX() + Settings.GRID_WIDTH / 2.0f) / Settings.GRID_WIDTH) * Settings.GRID_WIDTH;
            this.holdingObject.transform.position.y = (int) Math.floor((Input.getMouseWorldPositionY() + Settings.GRID_HEIGHT / 2.0f) / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT;

            if (Input.buttonDown(KeyCode.Mouse_Button_Left)) {
                float halfWidth = Settings.GRID_WIDTH / 2.0f;
                float halfHeight = Settings.GRID_HEIGHT / 2.0f;
                if (Input.isMouseDragging() && !blockInSquare(this.holdingObject.transform.position.x - halfWidth, this.holdingObject.transform.position.y - halfHeight)) {
                    place();
                } else if (!Input.isMouseDragging() && !blockInSquare(this.holdingObject.transform.position.x - halfWidth, this.holdingObject.transform.position.y - halfHeight) && this.debounceTime < 0.0f) {
                    place();
                    this.debounceTime = this.startDebounceTime;
                }
            }
        } else if (!Input.isMouseDragging() && Input.buttonClick(KeyCode.Mouse_Button_Left) && this.debounceTime < 0.0f) {
            int x = (int) Input.getMouseScreenPositionX();
            int y = (int) Input.getMouseScreenPositionY();
            int gameObjectID = (int) Window.getScreenFramebuffer().readPixel(x, y, GL_COLOR_ATTACHMENT1) - 1;
            GameObject pickedObject = SceneManager.getCurrentScene().getGameObject(gameObjectID);
            if (pickedObject != null && pickedObject.isClickable())
                Outliner_Window.setActiveGameObject(pickedObject);
            else if (pickedObject == null)
                Outliner_Window.clearSelected();

            this.debounceTime = this.startDebounceTime;
        } else if (Input.isMouseDragging() && Input.buttonDown(KeyCode.Mouse_Button_Left)) {
            if (!this.boxSelectSet) {
                Outliner_Window.clearSelected();
                this.boxSelectStart.set(Input.getMouseScreenPosition());
                this.boxSelectSet = true;
            }
            this.boxSelectEnd.set(Input.getMouseScreenPosition());
            Vector2f boxSelectStartWorld = Input.screenToWorld(this.boxSelectStart);
            Vector2f boxSelectEndWorld = Input.screenToWorld(this.boxSelectEnd);
            Vector2f halfSize = new Vector2f(boxSelectEndWorld).sub(boxSelectStartWorld).div(2.0f);
            DebugDraw.addBox2D(new Vector3f(boxSelectStartWorld.x, boxSelectStartWorld.y, 0.0f).add(halfSize.x, halfSize.y, 0.0f), new Vector2f(halfSize).mul(2.0f), Settings.BOX_SELECTION_COLOR);
            DebugDraw.addBox2D(new Vector3f(boxSelectStartWorld.x, boxSelectStartWorld.y, 0.0f).add(halfSize.x, halfSize.y, 0.0f), new Vector2f(halfSize).mul(2.0f), Settings.BOX_SELECTION_COLOR);
        } else if (this.boxSelectSet) {
            this.boxSelectSet = false;
            int screenStartX = (int) this.boxSelectStart.x;
            int screenStartY = (int) this.boxSelectStart.y;
            int screenEndX = (int) this.boxSelectEnd.x;
            int screenEndY = (int) this.boxSelectEnd.y;
            this.boxSelectStart.zero();
            this.boxSelectEnd.zero();

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

            float[] gameObjectsIDs = Window.getScreenFramebuffer().readPixels(new Vector2i(screenStartX, screenStartY), new Vector2i(screenEndX, screenEndY), GL_COLOR_ATTACHMENT1);
            Set<Integer> uniqueGameObjectsIDs = new HashSet<>();
            for (float objID : gameObjectsIDs)
                uniqueGameObjectsIDs.add((int) objID - 1);

            System.out.println(uniqueGameObjectsIDs);
            for (Integer objID : uniqueGameObjectsIDs) {
                GameObject pickedObject = SceneManager.getCurrentScene().getGameObject(objID);
                if (pickedObject != null && pickedObject.isClickable())
                    Outliner_Window.addActiveGameObject(pickedObject);
            }
        }
    }

    private boolean blockInSquare(float x, float y) {
        Vector2f start = new Vector2f(x, y);
        Vector2f end = new Vector2f(start).add(Settings.GRID_WIDTH, Settings.GRID_HEIGHT);
        Vector2f startScreenF = Input.worldToScreen(start);
        Vector2f endScreenF = Input.worldToScreen(end);
        Vector2i startScreen = new Vector2i((int) startScreenF.x + 2, (int) startScreenF.y + 2); // Adding 2 pixels to go inside square and not check surrounding squares
        Vector2i endScreen = new Vector2i((int) endScreenF.x - 2, (int) endScreenF.y - 2); // Subtracting 2 pixels to go inside square and not check surrounding squares
        float[] gameObjectsIDs = Window.getScreenFramebuffer().readPixels(startScreen, endScreen, GL_COLOR_ATTACHMENT1);

        for (float objID : gameObjectsIDs) {
            if (objID >= 0) { // Check if ID of gameObject is valid( >= 0 )
                GameObject pickedObj = SceneManager.getCurrentScene().getGameObject((int) objID - 1);
                if (pickedObj != null && pickedObj.isClickable())
                    return true;
            }
        }
        return false;
    }
}
