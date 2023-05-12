package engine.stuff.inputActions;

import engine.editor.windows.Outliner_Window;
import engine.entity.GameObject;
import engine.entity.component.Component;
import engine.eventListeners.Input;
import engine.eventListeners.KeyCode;
import engine.scenes.SceneManager;
import engine.stuff.Settings;
import engine.stuff.utils.Time;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ed_KeyboardControls extends Component {

    private final float startDebounce = 0.2f;
    private float debounce = this.startDebounce;

    private static final float smallMoveMultiplayer = 0.1f;
    private static final float normalMoveMultiplayer = 1.0f;

    @Override
    public void update() {
        this.debounce -= Time.deltaTime();

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
                GameObject copy = Objects.requireNonNull(Outliner_Window.getActiveGameObject()).copy();
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

        if (Input.buttonDown(KeyCode.Page_Down) && this.debounce < 0.0f) {
            this.debounce = this.startDebounce;

            for (GameObject obj : activeGameObjects)
                obj.transform.setZIndex(obj.transform.getZIndex() - 1);
            return;
        }
        if (Input.buttonDown(KeyCode.Page_Up) && this.debounce < 0.0f) {
            this.debounce = this.startDebounce;

            for (GameObject obj : activeGameObjects)
                obj.transform.setZIndex(obj.transform.getZIndex() + 1);
            return;
        }

        float currentMoveMultiplayer = normalMoveMultiplayer;
        if (Input.buttonDown(KeyCode.Left_Shift) || Input.buttonDown(KeyCode.Right_Shift))
            currentMoveMultiplayer = smallMoveMultiplayer;

        if (Input.buttonDown(KeyCode.Arrow_Up) && this.debounce < 0.0f) {
            this.debounce = this.startDebounce;

            for (GameObject obj : activeGameObjects)
                obj.transform.position.y += Settings.GRID_HEIGHT * currentMoveMultiplayer;
            return;
        }
        if (Input.buttonDown(KeyCode.Arrow_Down) && this.debounce < 0.0f) {
            this.debounce = this.startDebounce;

            for (GameObject obj : activeGameObjects)
                obj.transform.position.y -= Settings.GRID_HEIGHT * currentMoveMultiplayer;
            return;
        }
        if (Input.buttonDown(KeyCode.Arrow_Left) && this.debounce < 0.0f) {
            this.debounce = this.startDebounce;

            for (GameObject obj : activeGameObjects)
                obj.transform.position.x -= Settings.GRID_WIDTH * currentMoveMultiplayer;
            return;
        }
        if (Input.buttonDown(KeyCode.Arrow_Right) && this.debounce < 0.0f) {
            this.debounce = this.startDebounce;

            for (GameObject obj : activeGameObjects)
                obj.transform.position.x += Settings.GRID_WIDTH * currentMoveMultiplayer;
            return;
        }
    }
}
