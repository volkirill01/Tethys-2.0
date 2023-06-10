package engine.entity;

import engine.editor.windows.Outliner_Window;
import engine.logging.DebugLog;
import engine.scenes.SceneManager;
import org.joml.Vector3f;

public class GameObject_Manager {

    public static GameObject createEmpty(String name, boolean select) { return createEmpty(name, new Vector3f(0.0f), select); }

    public static GameObject createEmpty(String name, Vector3f position, boolean select) {
        DebugLog.logInfo("GameObject_Manager:CreateEmpty: ", name);

        GameObject empty = SceneManager.getCurrentScene().createGameObject(name, position);

        SceneManager.getCurrentScene().addGameObjectToScene(empty);
        if (select)
            Outliner_Window.setActiveGameObject(empty);

        return empty;
    }
}
