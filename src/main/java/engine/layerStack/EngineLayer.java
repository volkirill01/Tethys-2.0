package engine.layerStack;

import engine.editor.gui.EngineGuiLayer;
import engine.editor.windows.GameView_Window;
import engine.editor.windows.Outliner_Window;
import engine.editor.windows.SceneView_Window;
import engine.entity.component.Component;
import engine.observers.events.Event;
import engine.profiling.Profiler;
import engine.renderer.EntityRenderer;
import engine.renderer.camera.Camera;
import engine.renderer.debug.DebugRenderer;
import engine.renderer.debug.DebugGrid;
import engine.scenes.SceneManager;
import engine.stuff.Settings;
import engine.stuff.Window;
import engine.stuff.customVariables.Color;
import engine.stuff.utils.Time;

public class EngineLayer extends Layer {

    @Override
    public void init() { EntityRenderer.init(); }

    @Override
    public void update() {
        if (!Window.isMinimized()) {
            EntityRenderer.resetStats();

            DebugRenderer.beginFrame();

            if (Time.deltaTime() >= 0.0f) {
                DebugGrid.addGrid();

                if (Window.isRuntimePlaying()) {
                    if (!Window.isRuntimePause() || Window.isNextFrame()) {
                        Profiler.startTimer("Game Update");
                        SceneManager.getCurrentScene().update();
                        Profiler.stopTimer("Game Update");
                    }
                } else {
                    Profiler.startTimer("Editor Update");
                    SceneManager.getCurrentScene().editorUpdate();
                    Profiler.stopTimer("Editor Update");
                }

                if (EngineGuiLayer.isAnyWindowVisible_ByType(SceneView_Window.class))
                    if (!Window.isRuntimePause() || Window.isNextFrame()) {
                        SceneManager.getCurrentScene().getEditorCamera().getOutputFob().bindToWrite();

                        Profiler.startTimer("Render Scene");
                        EntityRenderer.render(SceneManager.getCurrentScene().getEditorCamera(), Settings.EDITOR_BACKGROUND_COLOR);
                        Profiler.stopTimer("Render Scene");

                        DebugRenderer.render(SceneManager.getCurrentScene().getEditorCamera());

                        SceneManager.getCurrentScene().getEditorCamera().getOutputFob().unbind();
                    }
            }

            if (!Window.isRuntimePause() || Window.isNextFrame()) {
                Profiler.startTimer("Render Game Cameras");
                for (Component c : SceneManager.getCurrentScene().getAllComponents(Camera.class)) {
                    Camera cam = (Camera) c;

                    if (Outliner_Window.getActiveGameObjects().contains(cam.gameObject) || (EngineGuiLayer.isAnyWindowVisible_ByType(GameView_Window.class))) {
                        cam.getOutputFob().bindToWrite();
                        Color backgroundColor = new Color(cam.getBackgroundColor());
                        backgroundColor.a = 255.0f;
                        EntityRenderer.render(cam, backgroundColor);
                        cam.getOutputFob().unbind();
                    }
                }
                Profiler.stopTimer("Render Game Cameras");
            }
        }
    }

    @Override
    public boolean onEvent(Event event) { return false; }
}
