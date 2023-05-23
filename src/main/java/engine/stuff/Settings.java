package engine.stuff;

import engine.stuff.customVariables.Color;
import org.joml.Vector2f;

public class Settings {

    public static final float GRID_WIDTH = 1.0f;
    public static final float GRID_HEIGHT = 1.0f;

    public static final float TRANSLATION_SNAPPING = 1.0f;
    public static final float SCALING_SNAPPING = 1.0f;
    public static final float ROTATE_SNAPPING = 45.0f;

    public static final String SHADER_VERSION = "330 core";

    public static final Color EDITOR_BACKGROUND_COLOR = new Color(33.0f, 36.0f, 40.0f);

    public static final Color BOX_SELECTION_COLOR = new Color(150.0f, 150.0f, 150.0f);

    public static final Color X_AXIS_COLOR = new Color(204.0f, 36.0f, 29.0f);
    public static final Color X_AXIS_COLOR_HOVER = new Color(224.0f, 50.0f, 43.0f);
    public static final Color Y_AXIS_COLOR = new Color(152.0f, 151.0f, 26.0f);
    public static final Color Y_AXIS_COLOR_HOVER = new Color(172.0f, 165.0f, 40.0f);
    public static final Color Z_AXIS_COLOR = new Color(69.0f, 133.0f, 136.0f);
    public static final Color Z_AXIS_COLOR_HOVER = new Color(89.0f, 147.0f, 150.0f);
    public static final Color W_AXIS_COLOR = new Color(114.0f, 12.0f, 191.0f);
    public static final Color W_AXIS_COLOR_HOVER = new Color(123.0f, 19.0f, 219.0f);

    public static final boolean VARIABLE_NAMES_STARTS_UPPER_CASE = true;

    public static final Vector2f GRAVITY_2D = new Vector2f(0.0f, -25.0f);
}
