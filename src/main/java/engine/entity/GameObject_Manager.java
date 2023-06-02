package engine.entity;

import engine.editor.windows.Outliner_Window;
import engine.logging.DebugLog;
import engine.scenes.SceneManager;

public class GameObject_Manager {

    public static GameObject createEmpty(String name, boolean select) {
        DebugLog.logInfo("GameObject_Manager:CreateEmpty: ", name);

        GameObject empty = SceneManager.getCurrentScene().createGameObject(name);

        SceneManager.getCurrentScene().addGameObjectToScene(empty);
        if (select)
            Outliner_Window.setActiveGameObject(empty);

        return empty;
    }
}
