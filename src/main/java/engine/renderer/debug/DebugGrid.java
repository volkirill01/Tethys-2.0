package engine.renderer.debug;

import engine.profiling.Profiler;
import engine.renderer.camera.ed_BaseCamera;
import engine.renderer.camera.ed_EditorCamera;
import engine.scenes.SceneManager;
import engine.stuff.Settings;
import engine.stuff.customVariables.Color;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class DebugGrid {

    private static final Color gridColor = new Color(20.0f, 20.0f, 20.0f);

    public static void addGrid() {
        Profiler.startTimer("Debug AddGrid");
        ed_EditorCamera camera = SceneManager.getCurrentScene().getEditorCamera();
        if (camera.getZoom() > 5.0f || camera.getProjectionType() == ed_BaseCamera.ProjectionType.Perspective)
            return;

        Vector3f cameraPosition = camera.getPosition();
        Vector2f projectionSize = new Vector2f(camera.getOrthographicProjectionSize()).mul(camera.getZoom()); // TODO FIX SCALING OF GRID

        float firstX = ((int) ((cameraPosition.x - projectionSize.x / 2) / Settings.GRID_WIDTH) - 2) * Settings.GRID_WIDTH + Settings.GRID_WIDTH / 2.0f;
        float firstY = ((int) ((cameraPosition.y - projectionSize.y / 2) / Settings.GRID_HEIGHT) - 2) * Settings.GRID_HEIGHT + Settings.GRID_HEIGHT / 2.0f;

        int numberVerticalLines = (int) (projectionSize.x / Settings.GRID_WIDTH) + 4;
        int numberHorizontalLines = (int) (projectionSize.y / Settings.GRID_HEIGHT) + 4;

        float width = projectionSize.x + (5 * Settings.GRID_WIDTH) - Settings.GRID_WIDTH;
        float height = projectionSize.y + (5 * Settings.GRID_HEIGHT) - Settings.GRID_HEIGHT;

        int maxLines = Math.max(numberVerticalLines, numberHorizontalLines);
        for (int i = 0; i < maxLines; i++) {
            float x = firstX + (Settings.GRID_WIDTH * i);
            float y = firstY + (Settings.GRID_HEIGHT * i);

            if (i < numberVerticalLines)
                DebugRenderer.addLine(new Vector3f(x, firstY - Settings.GRID_HEIGHT, 0.0f), new Vector3f(x, firstY + height, 0.0f), gridColor);
            if (i < numberHorizontalLines)
                DebugRenderer.addLine(new Vector3f(firstX - Settings.GRID_WIDTH, y, 0.0f), new Vector3f(firstX + width, y, 0.0f), gridColor);
        }
        Profiler.stopTimer("Debug AddGrid");
    }
}
