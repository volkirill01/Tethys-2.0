package engine.layerStack;

import engine.observers.events.Event;
import engine.profiling.Profiler;
import engine.renderer.EntityRenderer;
import engine.renderer.RenderCommand;
import engine.renderer.camera.Camera;
import engine.renderer.camera.ed_BaseCamera;
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
        if (!Window.isMinimized()) { // && (ImGuiLayer.getWindow(SceneView_Window.class).isVisible() || ImGuiLayer.getWindow(GameView_Window.class).isVisible()) // TODO SEPARATE UPDATE AND RENDERING
            EntityRenderer.resetStats();

            DebugRenderer.beginFrame();

            SceneManager.getCurrentScene().getEditorCamera().getOutputFob().bindToWrite();

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

                if (!Window.isRuntimePause() || Window.isNextFrame()) {
                    Profiler.startTimer("Render Scene");
                    renderPass(SceneManager.getCurrentScene().getEditorCamera(), Settings.EDITOR_BACKGROUND_COLOR);
                    Profiler.stopTimer("Render Scene");

                    DebugRenderer.render(SceneManager.getCurrentScene().getEditorCamera());
                }
            }

            SceneManager.getCurrentScene().getEditorCamera().getOutputFob().unbind();

            if (!Window.isRuntimePause() || Window.isNextFrame()) {
                Profiler.startTimer("Render Game Cameras");
                for (Camera c : SceneManager.getCurrentScene().getAllCameras()) {
                    c.getOutputFob().bindToWrite();
                    Color backgroundColor = new Color(c.getBackgroundColor());
                    backgroundColor.a = 255.0f;
                    renderPass(c, backgroundColor);
                    c.getOutputFob().unbind();
                }
                Profiler.stopTimer("Render Game Cameras");
            }
        }
    }

    private void renderPass(ed_BaseCamera camera, Color backgroundColor) {
        RenderCommand.setClearColor(backgroundColor);
        RenderCommand.clear(RenderCommand.BufferBit.COLOR_AND_DEPTH_BUFFER);
        // Clear entity ID attachment to -1
//        SceneManager.getCurrentScene().getEditorCamera().getOutputFob().clearColorAttachment(1, -1); // TODO FIX CLEARING THE COLOR BUFFER
//        EntityRenderer.beginScene();
        EntityRenderer.render(camera);
//        EntityRenderer.endScene();
    }

    @Override
    public boolean onEvent(Event event) { return false; }
}
