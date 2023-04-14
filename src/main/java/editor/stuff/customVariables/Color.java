package editor.stuff.customVariables;

public class Color {

    public float r;
    public float g;
    public float b;
    public float a;

    public Color(int r, int g, int b) { init(r, g, b, 255.0f); }

    public Color(int r, int g, int b, int a) { init(r, g, b, a); }

    public Color(float r, float g, float b) { init(r, g, b, 255.0f); }

    public Color(float r, float g, float b, float a) { init(r, g, b, a); }

    private void init(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color copy() { return new Color(this.r, this.g, this.b, this.a); }

    public static Color WHITE = new Color(255.0f, 255.0f, 255.0f);
    public static Color BLACK = new Color(0.0f, 0.0f, 0.0f);
}
