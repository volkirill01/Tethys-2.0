package engine.stuff.customVariables;

public class Color {

    public float r;
    public float g;
    public float b;
    public float a;

    public Color(Color ref) { init(ref.r, ref.g, ref.b, ref.a); }

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

    public static Color fromHex(String hex) {
        hex = hex.replace("#", "").toUpperCase();
        return switch (hex.length()) {
            case 6 -> new Color(Integer.valueOf(hex.substring(0, 2), 16), Integer.valueOf(hex.substring(2, 4), 16), Integer.valueOf(hex.substring(4, 6), 16));
            case 8 -> new Color(Integer.valueOf(hex.substring(0, 2), 16), Integer.valueOf(hex.substring(2, 4), 16), Integer.valueOf(hex.substring(4, 6), 16), Integer.valueOf(hex.substring(6, 8), 16));
            default -> throw new IllegalStateException(String.format("Unknown number of digits - '%d'", hex.length()));
        };
    }

    public Color copy() { return new Color(this.r, this.g, this.b, this.a); }

    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        if (!(object instanceof Color c)) return false;

        if (object == this) return true;

        return c.r == this.r && c.g == this.g && c.b == this.b && c.a == this.a;
    }

    @Override
    public String toString() { return String.format("(%fs %fs %fs %f)", this.r, this.g, this.b, this.a).replace(",", ".").replace("s", ","); }

    public static final Color WHITE = new Color(255.0f, 255.0f, 255.0f);
    public static final Color BLACK = new Color(0.0f, 0.0f, 0.0f);
    public static final Color RED = new Color(255.0f, 0.0f, 0.0f);
    public static final Color GREEN = new Color(0.0f, 255.0f, 0.0f);
    public static final Color BLUE = new Color(0.0f, 0.0f, 255.0f);
    public static final Color YELLOW = new Color(255.0f, 255.0f, 0.0f);
}
