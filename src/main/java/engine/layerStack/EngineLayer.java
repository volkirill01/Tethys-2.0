package engine.layerStack;

import engine.observers.events.Event;
import engine.profiling.Profiler;
import engine.renderer.EntityRenderer;
import engine.renderer.RenderCommand;
import engine.renderer.camera.Camera;
import engine.renderer.debug.DebugDraw;
import engine.renderer.debug.DebugGrid;
import engine.renderer.shader.Shader;
import engine.scenes.SceneManager;
import engine.stuff.Settings;
import engine.stuff.Window;
import engine.stuff.customVariables.Color;
import engine.stuff.utils.Time;
import org.joml.Matrix4f;

public class EngineLayer extends Layer {

    @Override
    public void init() { EntityRenderer.init(); }

    @Override
    public void update() {
        if (!Window.isMinimized()) { // && (ImGuiLayer.getWindow(SceneView_Window.class).isVisible() || ImGuiLayer.getWindow(GameView_Window.class).isVisible()) // TODO SEPARATE UPDATE AND RENDERING
            EntityRenderer.resetStats();

            DebugDraw.beginFrame();

            SceneManager.getCurrentScene().getEditorCamera().getOutputFob().bind();

            if (Time.deltaTime() >= 0.0f) {
                DebugGrid.addGrid(); // TODO FIX GRID DRAWING

                if (Window.isRuntimePlaying()) {
                    Profiler.startTimer("Game Update");
                    SceneManager.getCurrentScene().update();
                    Profiler.stopTimer("Game Update");
                } else {
                    Profiler.startTimer("Editor Update");
                    SceneManager.getCurrentScene().editorUpdate();
                    Profiler.stopTimer("Editor Update");
                }

                Profiler.startTimer("Render Scene");
                renderPass(
                        SceneManager.getCurrentScene().getEditorCamera().getProjectionMatrix(),
                        SceneManager.getCurrentScene().getEditorCamera().getViewMatrix(),
                        Settings.editorBackgroundColor);
                Profiler.stopTimer("Render Scene");

                DebugDraw.draw();
            }

            SceneManager.getCurrentScene().getEditorCamera().getOutputFob().unbind();

            Profiler.startTimer("Render Game Cameras");
            for (Camera c : SceneManager.getCurrentScene().getAllCameras()) {
                c.getOutputFob().bind();
                Color backgroundColor = new Color(c.getBackgroundColor());
                backgroundColor.a = 255.0f;
                renderPass(c.getProjectionMatrix(), c.getViewMatrix(), backgroundColor);
                c.getOutputFob().unbind();
            }
            Profiler.stopTimer("Render Game Cameras");
        }
    }

    private static void renderPass(Matrix4f projectionMatrix, Matrix4f viewMatrix, Color backgroundColor) {
        RenderCommand.setClearColor(backgroundColor);
        RenderCommand.clear(RenderCommand.BufferBit.ColorAndDepthBuffer);
        EntityRenderer.render(projectionMatrix, viewMatrix);
    }

    private static void renderPass_SingleShader(Matrix4f projectionMatrix, Matrix4f viewMatrix, Color backgroundColor, Shader shader) {
        RenderCommand.setClearColor(backgroundColor);
        RenderCommand.clear(RenderCommand.BufferBit.ColorAndDepthBuffer);
        EntityRenderer.setShader(shader);
        EntityRenderer.render_SingleShader(projectionMatrix, viewMatrix, shader);
    }

    @Override
    public boolean onEvent(Event event) {
        return false;
    }
}
