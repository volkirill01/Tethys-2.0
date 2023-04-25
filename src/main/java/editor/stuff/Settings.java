package editor.stuff;

import editor.stuff.customVariables.Color;
import org.joml.Vector2f;

public class Settings {

    public static final float GRID_WIDTH = 0.25f;
    public static final float GRID_HEIGHT = 0.25f;

    public static final Color xAxisColor = new Color(255.0f, 0.0f, 0.0f);
    public static final Color xAxisColor_Hover = new Color(225.0f, 76.5f, 76.5f);
    public static final Color yAxisColor = new Color(0.0f, 255.0f, 0.0f);
    public static final Color yAxisColor_Hover = new Color(76.5f, 225.0f, 76.5f);
    public static final Color zAxisColor = new Color(0.0f, 0.0f, 255.0f);
    public static final Color zAxisColor_Hover = new Color(76.5f, 76.5f, 225.0f);

    public static boolean variableNamesStartsUpperCase = true;

    public static final Vector2f gravity2D = new Vector2f(0.0f, -10.0f);
}
