package engine.renderer.renderer2D;

import engine.editor.gui.EditorGUI;
import engine.entity.component.Transform;
import engine.renderer.EntityRenderer;
import engine.stuff.customVariables.Color;
import imgui.ImGui;

public class ShapeRenderer2D extends ed_Renderer {

    private enum ShapeType {
        Rectangle,
        Circle
    }

    private final Color color = Color.WHITE.copy();
    private ShapeType shapeType = ShapeType.Circle;

    private transient Transform lastTransform;

    private float radius = 0.5f;
    private float thickness = 1.0f;
    private float fade = 0.0f;

    @Override
    public void start() {
        super.start();
        this.lastTransform = gameObject.transform.copy();
    }

    @Override
    public void editorUpdate() {
        if (this.lastTransform != null)
            if (!this.lastTransform.equals(this.gameObject.transform))
                this.gameObject.transform.copy(this.lastTransform);
    }

    @Override
    public void update() {
        if (this.lastTransform != null)
            if (!this.lastTransform.equals(this.gameObject.transform))
                this.gameObject.transform.copy(this.lastTransform);
    }

    @Override
    public void destroy() { EntityRenderer.destroyGameObject(this.gameObject, ShapeRenderer2D.class); }

    @Override
    public void imgui() {
        EditorGUI.field_Color("Color", this.color);

        setShapeType((ShapeType) EditorGUI.field_Enum("Shape Type", this.shapeType));

        EditorGUI.separator();
        switch (this.shapeType) {
            case Rectangle -> {
                ImGui.textColored(1.0f, 0.0f, 0.0f, 1.0f, "Not implemented yet.");
            }
            case Circle -> {
                setRadius(EditorGUI.field_Float("Radius", this.radius, 0.0f, EditorGUI.DEFAULT_FLOAT_FORMAT + "m"));
                setThickness(EditorGUI.field_Float("Thickness", this.thickness, 0.0f, 1.0f));
                setFade(EditorGUI.field_Float("Fade", this.fade, 0.0f, 10.0f));
            }
            default -> throw new IllegalStateException(String.format("Unknown shape type - '%s'", this.shapeType));
        }
    }

    public Color getColor() { return this.color; }

    public void setColor(Color color) {
        if (!this.color.equals(color))
            this.color.set(color);
    }

    public ShapeType getShapeType() { return this.shapeType; }

    public void setShapeType(ShapeType shapeType) { this.shapeType = shapeType; }

    public float getRadius() { return this.radius; }

    public void setRadius(float radius) { this.radius = radius;  }

    public float getThickness() { return this.thickness; }

    public void setThickness(float thickness) { this.thickness = thickness;  }

    public float getFade() { return this.fade; }

    public void setFade(float fade) { this.fade = fade; }
}
