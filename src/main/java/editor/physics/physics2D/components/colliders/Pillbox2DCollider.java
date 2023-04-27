package editor.physics.physics2D.components.colliders;

import editor.editor.gui.EditorGUI;
import editor.physics.physics2D.components.Collider2D;
import editor.physics.physics2D.components.RigidBody2D;
import editor.stuff.Window;
import org.joml.Vector2f;

public class Pillbox2DCollider extends Collider2D {

    private final transient Circle2DCollider topCircle = new Circle2DCollider();
    private final transient Circle2DCollider bottomCircle = new Circle2DCollider();
    private final transient Box2DCollider middleBox = new Box2DCollider();
    private transient boolean resetFixtureNextFrame = false;

    private float width = 0.25f;
    private float height = 0.5f;
    private transient float old_width = 0.0f;
    private transient float old_height = 0.0f;
    private final Vector2f offset = new Vector2f();

    @Override
    public void start() {
        this.topCircle.gameObject = this.gameObject;
        this.bottomCircle.gameObject = this.gameObject;
        this.middleBox.gameObject = this.gameObject;
        recalculateCollider();
    }

    @Override
    public void imgui() {
        this.width = EditorGUI.field_Float("Width", this.width);
        this.height = EditorGUI.field_Float("Height", this.height);
        if (EditorGUI.field_Vector2f("Offset", this.offset))
            recalculateCollider();
    }

    @Override
    public void editorUpdate() {
        if (this.gameObject == null || this.topCircle.gameObject == null || this.bottomCircle.gameObject == null || this.middleBox.gameObject == null)
            return;

        if (this.old_width != this.width)
            setWidth(this.width);
        if (this.old_height != this.height)
            setHeight(this.height);

        this.topCircle.editorUpdate();
        this.bottomCircle.editorUpdate();
        this.middleBox.editorUpdate();

        if (this.resetFixtureNextFrame)
            resetFixture();
    }

    @Override
    public void update() {
        if (this.gameObject == null || this.topCircle.gameObject == null || this.bottomCircle.gameObject == null || this.middleBox.gameObject == null)
            return;

        if (this.old_width != this.width)
            setWidth(this.width);
        if (this.old_height != this.height)
            setHeight(this.height);

        this.topCircle.update();
        this.bottomCircle.update();
        this.middleBox.update();

        if (this.resetFixtureNextFrame)
            resetFixture();
    }

    public void recalculateCollider() {
        float circleRadius = this.width;
        float boxHeight = this.height * 2.0f - 2.0f * circleRadius;
        this.topCircle.setRadius(circleRadius);
        this.bottomCircle.setRadius(circleRadius);

        this.topCircle.setOffset(new Vector2f(this.offset).add(0.0f, boxHeight / 4.0f));
        this.bottomCircle.setOffset(new Vector2f(this.offset).sub(0.0f, boxHeight / 4.0f));
        this.middleBox.setSize(new Vector2f(this.width, boxHeight / 2.0f));
        this.middleBox.setOffset(this.offset);
    }

    public void resetFixture() {
        if (Window.getPhysics2D().isLocked()) {
            this.resetFixtureNextFrame = true;
            return;
        }

        this.resetFixtureNextFrame = false;

        if (this.gameObject.hasComponent(RigidBody2D.class)) {
            RigidBody2D rb = this.gameObject.getComponent(RigidBody2D.class);
            Window.getPhysics2D().resetPillbox2DCollider(rb, this);
        }
    }

    public void setWidth(float width) {
        this.width = width;
        this.old_width = this.width;
        recalculateCollider();
    }

    public void setHeight(float height) {
        this.height = height;
        this.old_height = this.height;
        recalculateCollider();
    }

    public Circle2DCollider getTopCircle() { return this.topCircle; }

    public Circle2DCollider getBottomCircle() { return this.bottomCircle; }

    public Box2DCollider getMiddleBox() { return this.middleBox; }
}
