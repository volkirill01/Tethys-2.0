package engine.stuff;

import engine.stuff.customVariables.Color;
import org.joml.Vector2f;

public class Settings {

    public static final float GRID_WIDTH = 0.25f;
    public static final float GRID_HEIGHT = 0.25f;

    public static final String shaderVersion = "330 core";

    public static final Color editorBackgroundColor = new Color(33.0f, 36.0f, 40.0f);

    public static final Color boxSelectionColor = new Color(150.0f, 150.0f, 150.0f);

    public static final Color xAxisColor = new Color(204.0f, 36.0f, 29.0f);
    public static final Color xAxisColor_Hover = new Color(224.0f, 50.0f, 43.0f);
    public static final Color yAxisColor = new Color(152.0f, 151.0f, 26.0f);
    public static final Color yAxisColor_Hover = new Color(172.0f, 165.0f, 40.0f);
    public static final Color zAxisColor = new Color(69.0f, 133.0f, 136.0f);
    public static final Color zAxisColor_Hover = new Color(89.0f, 147.0f, 150.0f);
    public static final Color wAxisColor = new Color(114.0f, 12.0f, 191.0f);
    public static final Color wAxisColor_Hover = new Color(123.0f, 19.0f, 219.0f);

    public static final boolean variableNamesStartsUpperCase = true;

    public static final Vector2f gravity2D = new Vector2f(0.0f, -10.0f);
}
