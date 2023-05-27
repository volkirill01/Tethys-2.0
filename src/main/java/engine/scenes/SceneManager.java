package engine.scenes;

import engine.editor.windows.Outliner_Window;
import engine.logging.DebugLog;
import engine.profiling.Profiler;

public class SceneManager {

    private static Scene currentScene;

    public static void changeScene(String filepath) {
        DebugLog.logInfo("SceneManager:ChangeScene: ", filepath);

        Profiler.startTimer(String.format("SceneManager Change Scene - '%s'", filepath));
        if (currentScene != null)
            currentScene.destroy();

        Outliner_Window.setActiveGameObject(null);
        currentScene = new Scene(filepath);
        currentScene.load(filepath);
        currentScene.init();
        currentScene.start();
        Profiler.stopTimer(String.format("SceneManager Change Scene - '%s'", filepath));
    }

    public static Scene getCurrentScene() { return currentScene; }
}
