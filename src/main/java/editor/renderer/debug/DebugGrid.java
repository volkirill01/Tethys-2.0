package editor.renderer.debug;

import editor.TestFieldsWindow;
import editor.renderer.camera.BaseCamera;
import editor.renderer.camera.CameraType;
import editor.scenes.SceneManager;
import editor.stuff.Settings;
import editor.stuff.customVariables.Color;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class DebugGrid {

    private static final Color gridColor = new Color(35.0f, 35.0f, 35.0f);

    public static void addGrid() {
        BaseCamera camera = SceneManager.getCurrentScene().getCamera();
        if (camera.getZoom() > 5.0f || camera.getCameraType() == CameraType.Perspective)
            return;

        Vector3f cameraPosition = camera.getPosition();
        Vector2f projectionSize = new Vector2f(camera.getProjectionSize()).mul(1.0f - camera.getZoom()); // TODO FIX SCALING OF GRID

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
                DebugDraw.addLine(new Vector3f(x, firstY - Settings.GRID_HEIGHT, 0.0f), new Vector3f(x, firstY + height, 0.0f), gridColor);
            if (i < numberHorizontalLines)
                DebugDraw.addLine(new Vector3f(firstX - Settings.GRID_WIDTH, y, 0.0f), new Vector3f(firstX + width, y, 0.0f), gridColor);
        }
    }
}
