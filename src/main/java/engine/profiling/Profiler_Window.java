package engine.profiling;

import engine.editor.gui.EditorGUI;
import engine.editor.gui.EditorGuiWindow;
import engine.renderer.EntityRenderer;
import engine.renderer.renderer2D.MasterRenderer2D;
import engine.stuff.utils.Time;
import imgui.ImGui;

public class Profiler_Window extends EditorGuiWindow {

    public Profiler_Window() { super("\uEFF3 Profiler"); }

    @Override
    public void drawWindow() {
        ImGui.text(String.format("Draw Calls: %s", EntityRenderer.getDrawCalls()));
        ImGui.text(String.format("Frame time: %.5fms (FPS: %s)", Time.deltaTime(), Time.getFPS()));

        EditorGUI.separator();
        ImGui.text("2D Renderer stats:");
        ImGui.text(String.format("  batch count: %s", MasterRenderer2D.getBatchCount()));
        ImGui.text(String.format("  quads count: %s", MasterRenderer2D.getQuadsCount()));
        ImGui.text(String.format("  vertices count: %s", MasterRenderer2D.getVerticesCount()));
        ImGui.text(String.format("  indices count: %s", MasterRenderer2D.getIndicesCount()));

        if (ImGui.collapsingHeader("Timers"))
            for (Timer timer : Profiler.getTimers())
                ImGui.text(String.format("%.5fms %s", timer.getDuration(), timer.getName()));
    }
}
