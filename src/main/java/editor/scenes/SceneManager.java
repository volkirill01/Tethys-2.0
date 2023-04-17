package editor.scenes;

public class SceneManager {

    private static int currentSceneIndex = -1;
    private static EditorScene currentScene;

    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0 -> currentScene = new EditorMode_EditorScene();
            case 1 -> currentScene = new PlayMode_EditorScene();
            default -> throw new IllegalStateException("Unknown scene '" + newScene + "'.");
        }
        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    public static EditorScene getCurrentScene() { return currentScene; }
}
