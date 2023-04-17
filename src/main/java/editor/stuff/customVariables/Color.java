package editor.stuff.customVariables;

import editor.entity.component.components.Transform;

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

    public void set(float r, float g, float b) { init(r, g, b, 255.0f); }

    public void set(float r, float g, float b, float a) { init(r, g, b, a); }

    public void set(Color ref) { init(ref.r, ref.g, ref.b, ref.a); }

    public void set(float[] colorArray) { init(colorArray[0], colorArray[1], colorArray[2], colorArray.length == 4 ? colorArray[3] : 255.0f);}

    public Color copy() { return new Color(this.r, this.g, this.b, this.a); }

    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        if (!(object instanceof Color)) return false;

        if (object == this) return true;

        Color c = (Color) object;
        return c.r == this.r && c.g == this.g && c.b == this.b && c.a == this.a;
    }

    @Override
    public String toString() { return String.format("(%f, %f, %f, %f)", this.r, this.g, this.b, this.a); }

    public static Color WHITE = new Color(255.0f, 255.0f, 255.0f);
    public static Color BLACK = new Color(0.0f, 0.0f, 0.0f);
    public static Color RED = new Color(255.0f, 0.0f, 0.0f);
    public static Color GREEN = new Color(0.0f, 255.0f, 0.0f);
    public static Color BLUE = new Color(0.0f, 0.0f, 255.0f);
}
