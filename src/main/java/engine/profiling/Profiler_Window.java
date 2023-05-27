package engine.profiling;

import engine.editor.gui.EditorGUI;
import engine.editor.gui.EditorGuiWindow;
import engine.renderer.EntityRenderer;
import engine.renderer.renderer2D.SpriteMasterRenderer;
import engine.stuff.utils.Time;
import imgui.ImGui;

public class Profiler_Window extends EditorGuiWindow {

    public Profiler_Window() { super("\uEFF3 Profiler"); }

    @Override
    public void drawWindow() {
        ImGui.text(String.format("Draw Calls: %s", EntityRenderer.getDrawCalls()));
        ImGui.text(String.format("Frame time: %.5fms (FPS: %s)", Time.deltaTime(), Time.getFPS()));

        EditorGUI.separator();
        ImGui.text("Sprite Renderer stats:");
        ImGui.text(String.format("  batch count: %s", SpriteMasterRenderer.getBatchCount()));
        ImGui.text(String.format("  quads count: %s", SpriteMasterRenderer.getQuadsCount()));
        ImGui.text(String.format("  vertices count: %s", SpriteMasterRenderer.getVerticesCount()));
        ImGui.text(String.format("  indices count: %s", SpriteMasterRenderer.getIndicesCount()));

        EditorGUI.separator();
        for (Timer timer : Profiler.getTimers()) {
            ImGui.text(String.format("%.5fms %s", timer.getDuration(), timer.getName()));
        }
    }
}
