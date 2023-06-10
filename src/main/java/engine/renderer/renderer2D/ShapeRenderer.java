package engine.renderer.renderer2D;

import engine.editor.gui.EditorGUI;
import engine.renderer.EntityRenderer;
import engine.stuff.customVariables.Color;
import imgui.ImGui;
import org.joml.Vector2f;

public class ShapeRenderer extends ed_Renderer {

    public enum ShapeType {
        Rectangle,
        Circle
    }

    private final Color color = Color.WHITE.copy();
    private ShapeType shapeType = ShapeType.Circle;

//    private transient Transform lastTransform; // TODO ADD DIRTY FLAG SYSTEM

    private float radius = 0.5f;
    private float thickness = 1.0f;
    private float fade = 0.0f;

    private float cornerRadius = 0.0f;
    private final Vector2f size = new Vector2f(1.0f);

//    @Override
//    public void start() {
//        super.start();
//        this.lastTransform = gameObject.transform.copy();
//    }

//    @Override
//    public void editorUpdate() {
//        if (this.lastTransform != null)
//            if (!this.lastTransform.equals(this.gameObject.transform))
//                this.gameObject.transform.copy(this.lastTransform);
//    }
//
//    @Override
//    public void update() {
//        if (this.lastTransform != null)
//            if (!this.lastTransform.equals(this.gameObject.transform))
//                this.gameObject.transform.copy(this.lastTransform);
//    }

    @Override
    public void destroy() { EntityRenderer.destroyGameObject(this.gameObject, ShapeRenderer.class); }

    @Override
    public void imgui() {
        EditorGUI.field_Color("Color", this.color);

        setShapeType((ShapeType) EditorGUI.field_Enum("Shape Type", this.shapeType));

        ImGui.separator();
        switch (this.shapeType) {
            case Rectangle -> {
                EditorGUI.field_Vector2f("Size", this.size);
                this.cornerRadius = EditorGUI.field_Float("Corner Radius", this.cornerRadius, 0.0f, 1.0f);
                this.thickness = EditorGUI.field_Float("Thickness", this.thickness, 0.0f, 1.0f);
                this.fade = EditorGUI.field_Float("Fade", this.fade, 0.0f, 10.0f);
            }
            case Circle -> {
                this.radius = EditorGUI.field_Float("Radius", this.radius, 0.0f, EditorGUI.DEFAULT_FLOAT_FORMAT + "m");
                this.thickness = EditorGUI.field_Float("Thickness", this.thickness, 0.0f, 1.0f);
                this.fade = EditorGUI.field_Float("Fade", this.fade, 0.0f, 10.0f);
            }
            default -> throw new IllegalStateException(String.format("Unknown shape type - '%s'", this.shapeType));
        }
    }

    @Override
    public void reset() {
        this.color.set(Color.WHITE);
        this.shapeType = ShapeType.Circle;
        this.radius = 0.5f;
        this.thickness = 1.0f;
        this.fade = 0.0f;
    }

    public Color getColor() { return this.color; }

    public void setColor(Color color) {
        if (!this.color.equals(color))
            this.color.set(color);
    }

    public ShapeType getShapeType() { return this.shapeType; }

    public void setShapeType(ShapeType shapeType) {
        ShapeType oldType = this.shapeType;
        this.shapeType = shapeType;
        if (this.shapeType != oldType) {
            MasterRenderer2D.destroyGameObject(this.gameObject, ShapeRenderer.class);
            MasterRenderer2D.add(this.gameObject);
        }
    }

    public float getRadius() { return this.radius; }

    public void setRadius(float radius) { this.radius = radius;  }

    public float getThickness() { return this.thickness; }

    public void setThickness(float thickness) { this.thickness = thickness;  }

    public float getFade() { return this.fade; }

    public void setFade(float fade) { this.fade = fade; }

    public float getCornerRadius() { return this.cornerRadius; }

    public void setCornerRadius(float cornerRadius) { this.cornerRadius = cornerRadius; }

    public Vector2f getSize() { return this.size; }

    public void setSize(Vector2f size) { this.size.set(size); }
}
