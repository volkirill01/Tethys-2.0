package engine.entity;

import engine.editor.windows.Outliner_Window;
import engine.scenes.SceneManager;

public class GameObject_Manager {

    public static void createEmpty(String name, boolean select) {
        GameObject empty = SceneManager.getCurrentScene().createGameObject(name);

        SceneManager.getCurrentScene().addGameObjectToScene(empty);
        if (select)
            Outliner_Window.setActiveGameObject(empty);
    }
}
