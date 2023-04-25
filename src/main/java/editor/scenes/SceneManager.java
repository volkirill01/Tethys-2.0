package editor.scenes;

import editor.editor.windows.Outliner_Window;

public class SceneManager {

    private static Scene currentScene;

    public static void changeScene(SceneInitializer newScene) {
        if (currentScene != null)
            currentScene.destroy();

        Outliner_Window.setActiveGameObject(null);
        currentScene = new Scene(newScene);
        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    public static Scene getCurrentScene() { return currentScene; }
}
