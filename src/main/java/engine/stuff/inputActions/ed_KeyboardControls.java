package engine.stuff.inputActions;

import engine.editor.gui.EngineGuiLayer;
import engine.editor.windows.Outliner_Window;
import engine.entity.GameObject;
import engine.entity.component.Component;
import engine.eventListeners.Input;
import engine.eventListeners.KeyCode;
import engine.observers.EventSystem;
import engine.observers.Observer;
import engine.observers.events.Event;
import engine.observers.events.EventType;
import engine.scenes.SceneManager;
import engine.stuff.Settings;
import engine.stuff.utils.Time;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ed_KeyboardControls extends Component implements Observer {

    private final static float startDebounce = 0.2f;
    private float debounce = startDebounce;

    private static final float smallMoveMultiplayer = 0.1f;
    private static final float normalMoveMultiplayer = 1.0f;
    private float currentMoveMultiplayer = normalMoveMultiplayer;

    public ed_KeyboardControls() { EventSystem.addObserver(this); }

    @Override
    public void update() {
        this.debounce -= Time.deltaTime();

        if (Input.buttonDown(KeyCode.Left_Shift) || Input.buttonDown(KeyCode.Right_Shift))
            this.currentMoveMultiplayer = smallMoveMultiplayer;
        else
            this.currentMoveMultiplayer = normalMoveMultiplayer;
    }

    @Override
    public void onNotify(Event event) {
        if (event.type != EventType.Engine_KeyboardButtonCallback || !EngineGuiLayer.isSceneWindowSelected())
            return;

        List<GameObject> activeGameObjects = Outliner_Window.getActiveGameObjects();

        boolean control = Input.buttonDown(KeyCode.Left_Control) || Input.buttonDown(KeyCode.Right_Control);
        boolean shift = Input.buttonDown(KeyCode.Left_Shift) || Input.buttonDown(KeyCode.Right_Shift);
        if (((AbstractMap.SimpleEntry<Integer, Boolean>) event.data).getValue())
            return;

        switch (((AbstractMap.SimpleEntry<Integer, Boolean>) event.data).getKey()) {
            case KeyCode.S -> {
                if (control && !shift)
                    EventSystem.notify(new Event(EventType.Engine_SaveScene));
                if (control && shift)
                    EventSystem.notify(new Event(EventType.Engine_SaveSceneAs));
            }
            case KeyCode.O -> {
                if (control && !shift)
                    EventSystem.notify(new Event(EventType.Engine_OpenScene));
            }
            case KeyCode.D -> {
                if (control && !shift) {
                    if (this.debounce < 0.0f) {
                        this.debounce = startDebounce;

                        if (activeGameObjects.size() > 1) {
                            List<GameObject> gameObjects = new ArrayList<>(activeGameObjects);
                            Outliner_Window.clearSelected();

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
                    }
                }
            }
            case KeyCode.Delete -> {
                if (!control && !shift) {
                    for (GameObject obj : activeGameObjects)
                        obj.destroy();

                    Outliner_Window.clearSelected();
                }
            }
            case KeyCode.Page_Down -> {
                if (!control && !shift)
                    if (this.debounce < 0.0f) {
                        this.debounce = startDebounce;

                        for (GameObject obj : activeGameObjects)
                            obj.transform.setZIndex(obj.transform.getZIndex() - 1);
                    }
            }
            case KeyCode.Page_Up -> {
                if (!control && !shift)
                    if (this.debounce < 0.0f) {
                        this.debounce = startDebounce;

                        for (GameObject obj : activeGameObjects)
                            obj.transform.setZIndex(obj.transform.getZIndex() + 1);
                    }
            }
            case KeyCode.Arrow_Up -> {
                if (!control)
                    if (this.debounce < 0.0f) {
                        this.debounce = startDebounce;

                        for (GameObject obj : activeGameObjects)
                            obj.transform.position.y += Settings.GRID_HEIGHT * currentMoveMultiplayer;
                    }
            }
            case KeyCode.Arrow_Down -> {
                if (!control)
                    if (this.debounce < 0.0f) {
                        this.debounce = startDebounce;

                        for (GameObject obj : activeGameObjects)
                            obj.transform.position.y -= Settings.GRID_HEIGHT * currentMoveMultiplayer;
                    }
            }
            case KeyCode.Arrow_Left -> {
                if (!control)
                    if (this.debounce < 0.0f) {
                        this.debounce = startDebounce;

                        for (GameObject obj : activeGameObjects)
                            obj.transform.position.x -= Settings.GRID_WIDTH * currentMoveMultiplayer;
                    }
            }
            case KeyCode.Arrow_Right -> {
                if (!control)
                    if (this.debounce < 0.0f) {
                        this.debounce = startDebounce;

                        for (GameObject obj : activeGameObjects)
                            obj.transform.position.x += Settings.GRID_WIDTH * currentMoveMultiplayer;
                    }
            }
            default -> { }
        }

//        if (Input.buttonDown(KeyCode.Left_Control) && Input.buttonClick(KeyCode.D)) {
//            if (activeGameObjects.size() > 1) {
//                Outliner_Window.clearSelected();
//                List<GameObject> gameObjects = new ArrayList<>(activeGameObjects);
//
//                for (GameObject obj : gameObjects) {
//                    GameObject copy = obj.copy();
//                    SceneManager.getCurrentScene().addGameObjectToScene(copy);
//                    Outliner_Window.addActiveGameObject(copy);
//                }
//            } else {
//                GameObject copy = Objects.requireNonNull(Outliner_Window.getActiveGameObject()).copy();
//                copy.transform.position.add(Settings.GRID_WIDTH, 0.0f, 0.0f);
//                SceneManager.getCurrentScene().addGameObjectToScene(copy);
//                Outliner_Window.setActiveGameObject(copy);
//            }
//            return;
//        }
//
//        if (Input.buttonClick(KeyCode.Delete)) {
//            for (GameObject obj : activeGameObjects)
//                obj.destroy();
//
//            Outliner_Window.clearSelected();
//            return;
//        }
//
//        if (Input.buttonDown(KeyCode.Page_Down) && this.debounce < 0.0f) {
//            this.debounce = this.startDebounce;
//
//            for (GameObject obj : activeGameObjects)
//                obj.transform.setZIndex(obj.transform.getZIndex() - 1);
//            return;
//        }
//        if (Input.buttonDown(KeyCode.Page_Up) && this.debounce < 0.0f) {
//            this.debounce = this.startDebounce;
//
//            for (GameObject obj : activeGameObjects)
//                obj.transform.setZIndex(obj.transform.getZIndex() + 1);
//            return;
//        }
//
//        float currentMoveMultiplayer = normalMoveMultiplayer;
//        if (Input.buttonDown(KeyCode.Left_Shift) || Input.buttonDown(KeyCode.Right_Shift))
//            currentMoveMultiplayer = smallMoveMultiplayer;
//
//        if (Input.buttonDown(KeyCode.Arrow_Up) && this.debounce < 0.0f) {
//            this.debounce = this.startDebounce;
//
//            for (GameObject obj : activeGameObjects)
//                obj.transform.position.y += Settings.GRID_HEIGHT * currentMoveMultiplayer;
//            return;
//        }
//        if (Input.buttonDown(KeyCode.Arrow_Down) && this.debounce < 0.0f) {
//            this.debounce = this.startDebounce;
//
//            for (GameObject obj : activeGameObjects)
//                obj.transform.position.y -= Settings.GRID_HEIGHT * currentMoveMultiplayer;
//            return;
//        }
//        if (Input.buttonDown(KeyCode.Arrow_Left) && this.debounce < 0.0f) {
//            this.debounce = this.startDebounce;
//
//            for (GameObject obj : activeGameObjects)
//                obj.transform.position.x -= Settings.GRID_WIDTH * currentMoveMultiplayer;
//            return;
//        }
//        if (Input.buttonDown(KeyCode.Arrow_Right) && this.debounce < 0.0f) {
//            this.debounce = this.startDebounce;
//
//            for (GameObject obj : activeGameObjects)
//                obj.transform.position.x += Settings.GRID_WIDTH * currentMoveMultiplayer;
//            return;
//        }
    }
}
