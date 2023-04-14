package editor.scene;

import editor.eventListeners.Input;
import editor.eventListeners.KeyCode;
import editor.eventListeners.KeyListener;
import editor.stuff.Window;
import editor.stuff.utils.Time;

public class EditorMode_EditorScene extends EditorScene {

    private boolean changingScene = false;
    private float timeToChange = 1.0f;

    public EditorMode_EditorScene() {
        System.out.println("Inside Editor");
    }

    @Override
    public void update() {
        if (!changingScene && Input.buttonDown(KeyCode.Space)) {
            changingScene = true;
        }

        if (changingScene && timeToChange > 0.0f) {
            timeToChange -= Time.deltaTime();
            Window.r -= Time.deltaTime() * 5.0f;
            Window.g -= Time.deltaTime() * 5.0f;
            Window.b -= Time.deltaTime() * 5.0f;
        } else  if (changingScene) {
            SceneManager.changeScene(1);
        }
    }
}
