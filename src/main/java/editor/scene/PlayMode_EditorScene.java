package editor.scene;

import editor.stuff.Window;

public class PlayMode_EditorScene extends EditorScene {

    public PlayMode_EditorScene() {
        System.out.println("Inside PlayMode");
        Window.r = 1.0f;
        Window.g = 0.0f;
        Window.b = 1.0f;
    }

    @Override
    public void update() {

    }
}
