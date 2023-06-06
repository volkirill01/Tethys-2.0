package engine.stuff.inputActions;

import engine.editor.gui.EngineGuiLayer;
import engine.editor.windows.Outliner_Window;
import engine.editor.windows.SceneHierarchy_Window;
import engine.editor.windows.SceneView_Window;
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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ed_KeyboardControls extends Component implements Observer {

//    private final static float startDebounce = 0.2f;
//    private float debounce = startDebounce;

    private static final float smallMoveMultiplayer = 0.1f;
    private static final float normalMoveMultiplayer = 1.0f;
    private float currentMoveMultiplayer = normalMoveMultiplayer;

    public ed_KeyboardControls() { EventSystem.addObserver(this); }

    @Override
    public void update() {
//        if (this.debounce > 0.0f)
//            this.debounce -= Time.deltaTime();

        if (Input.buttonDown(KeyCode.Left_Shift) || Input.buttonDown(KeyCode.Right_Shift))
            this.currentMoveMultiplayer = smallMoveMultiplayer;
        else
            this.currentMoveMultiplayer = normalMoveMultiplayer;
    }

    @Override
    public void onNotify(Event event) {
        if (event.type != EventType.Engine_KeyboardButtonCallback || !(EngineGuiLayer.isAnyWindowSelected_ByType(SceneView_Window.class) || EngineGuiLayer.isAnyWindowSelected_ByType(SceneHierarchy_Window.class)))
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
//                    if (this.debounce <= 0.0f) {
//                        this.debounce = startDebounce;

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
//                    }
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
//                    if (this.debounce <= 0.0f) {
//                        this.debounce = startDebounce;

                    for (GameObject obj : activeGameObjects)
                        obj.transform.setZIndex(obj.transform.getZIndex() - 1);
//                    }
            }
            case KeyCode.Page_Up -> {
                if (!control && !shift)
//                    if (this.debounce <= 0.0f) {
//                        this.debounce = startDebounce;

                    for (GameObject obj : activeGameObjects)
                        obj.transform.setZIndex(obj.transform.getZIndex() + 1);
//                    }
            }
            case KeyCode.Arrow_Up -> {
                if (!control)
//                    if (this.debounce <= 0.0f) {
//                        this.debounce = startDebounce;

                    for (GameObject obj : activeGameObjects)
                        obj.transform.position.y += Settings.GRID_HEIGHT * currentMoveMultiplayer;
//                    }
            }
            case KeyCode.Arrow_Down -> {
                if (!control)
//                    if (this.debounce <= 0.0f) {
//                        this.debounce = startDebounce;

                    for (GameObject obj : activeGameObjects)
                        obj.transform.position.y -= Settings.GRID_HEIGHT * currentMoveMultiplayer;
//                    }
            }
            case KeyCode.Arrow_Left -> {
                if (!control)
//                    if (this.debounce <= 0.0f) {
//                        this.debounce = startDebounce;

                    for (GameObject obj : activeGameObjects)
                        obj.transform.position.x -= Settings.GRID_WIDTH * currentMoveMultiplayer;
//                    }
            }
            case KeyCode.Arrow_Right -> {
                if (!control)
//                    if (this.debounce <= 0.0f) {
//                        this.debounce = startDebounce;

                    for (GameObject obj : activeGameObjects)
                        obj.transform.position.x += Settings.GRID_WIDTH * currentMoveMultiplayer;
//                    }
            }
            default -> { }
        }
    }
}
