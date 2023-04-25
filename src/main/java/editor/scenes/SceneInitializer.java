package editor.scenes;

public abstract class SceneInitializer {

    public abstract void init(Scene scene);
    public abstract void loadResources(Scene scene);
    public abstract void imgui(); // TODO MOVE THIS, ITS NOT BE THERE
}
