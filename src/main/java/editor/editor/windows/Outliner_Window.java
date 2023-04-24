package editor.editor.windows;

import editor.entity.GameObject;
import editor.eventListeners.Input;
import editor.eventListeners.KeyCode;
import editor.scenes.SceneManager;
import editor.stuff.Window;
import editor.stuff.utils.Time;
import imgui.ImGui;

public class Outliner_Window {

    protected static GameObject activeGameObject = null;

    private float startDebounceTime = 0.2f;
    private float debounceTime = this.startDebounceTime;

    public void update() {
        this.debounceTime -= Time.deltaTime();

        if (this.debounceTime < 0.0f) {
            if (Input.buttonDown(KeyCode.Mouse_Button_Left)) {
                int x = (int) Input.getMouseScreenPositionX();
                int y = (int) Input.getMouseScreenPositionY();
                int gameObjectUID = Window.getPickingTexture().readPixel(x, y);
                GameObject pickedObject = SceneManager.getCurrentScene().getGameObject(gameObjectUID);
                if (pickedObject != null && pickedObject.isClickable())
                    activeGameObject = pickedObject;
                else if (pickedObject == null && !Input.isMouseDragging()) {
                    activeGameObject = null;
                }
                this.debounceTime = this.startDebounceTime;
            } else
                this.debounceTime = 0.0f;
        }
    }

    public void imgui() {
        if (activeGameObject != null) {
            ImGui.begin("Outliner");
            activeGameObject.imgui();
            ImGui.end();
        }
    }

    public static GameObject getActiveGameObject() { return activeGameObject; }
}
