package engine.scenes;

import engine.editor.windows.Outliner_Window;

public class SceneManager {

    private static Scene currentScene;

    public static void changeScene(String filepath) {
        if (currentScene != null)
            currentScene.destroy();

        Outliner_Window.setActiveGameObject(null);
        currentScene = new Scene(filepath);
        currentScene.load(filepath);
        currentScene.init();
        currentScene.start();
    }

    public static Scene getCurrentScene() { return currentScene; }
}
