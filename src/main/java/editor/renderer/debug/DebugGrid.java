package editor.renderer.debug;

import editor.scenes.SceneManager;
import editor.stuff.Settings;
import editor.stuff.customVariables.Color;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class DebugGrid {

    public static void draw() {
        Vector3f cameraPosition = SceneManager.getCurrentScene().getCamera().getPosition();
        Vector2f projectionSize = SceneManager.getCurrentScene().getCamera().getProjectionSize();

        Color color = new Color(0.2f, 0.2f, 0.2f);

        int firstX = ((int) (cameraPosition.x / Settings.GRID_WIDTH) - 1) * Settings.GRID_WIDTH;
        int firstY = ((int) (cameraPosition.y / Settings.GRID_HEIGHT) - 1) * Settings.GRID_HEIGHT;

        int numberVerticalLines = (int) (projectionSize.x / Settings.GRID_WIDTH) + 2;
        int numberHorizontalLines = (int) (projectionSize.y / Settings.GRID_HEIGHT) + 2;

        float width = projectionSize.x + Settings.GRID_WIDTH * 2;
        float height = projectionSize.y + Settings.GRID_HEIGHT * 2;

        int maxLines = Math.max(numberVerticalLines, numberHorizontalLines);
        for (int i = 0; i < maxLines; i++) {
            int x = firstX + (Settings.GRID_WIDTH * i);
            int y = firstY + (Settings.GRID_HEIGHT * i);

            if (i < numberVerticalLines)
                DebugDraw.addLine(new Vector3f(x, firstY, 0.0f), new Vector3f(x, firstY + height, 0.0f), color);
            if (i < numberHorizontalLines)
                DebugDraw.addLine(new Vector3f(firstX, y, 0.0f), new Vector3f(firstX + width, y, 0.0f), color);
        }
    }
}
