package engine.physics.physics2D.components.colliders;

import engine.editor.gui.EditorGUI;
import engine.physics.physics2D.Physics2D;
import engine.physics.physics2D.components.ed_Collider2D;
import engine.physics.physics2D.components.RigidBody2D;
import engine.stuff.Window;
import org.joml.Vector2f;

public class Pillbox2DCollider extends ed_Collider2D {

    private final transient Circle2DCollider topCircle = new Circle2DCollider();
    private final transient Circle2DCollider bottomCircle = new Circle2DCollider();
    private final transient Box2DCollider middleBox = new Box2DCollider();
    private transient boolean resetFixtureNextFrame = false;

    private float width = 1.0f;
    private float height = 2.0f;
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
        this.width = EditorGUI.field_Float("Width", this.width, EditorGUI.DEFAULT_FLOAT_FORMAT + "m");
        this.height = EditorGUI.field_Float("Height", this.height, EditorGUI.DEFAULT_FLOAT_FORMAT + "m");
        if (EditorGUI.field_Vector2f("Offset", this.offset, EditorGUI.DEFAULT_FLOAT_FORMAT + "m"))
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

    @Override
    public void reset() {
        this.width = 1.0f;
        this.height = 1.0f;
        this.offset.set(0.0f);
        resetFixture();
        recalculateCollider();
    }

    public void recalculateCollider() {
        float circleRadius = this.width;
        float boxHeight = this.height * 2.0f - 2.0f * circleRadius;
        this.topCircle.setRadius(circleRadius);
        this.bottomCircle.setRadius(circleRadius);

        this.topCircle.setOffset(new Vector2f(this.offset).add(0.0f, boxHeight / 4.0f));
        this.bottomCircle.setOffset(new Vector2f(this.offset).sub(0.0f, boxHeight / 4.0f));
        this.middleBox.setSize(new Vector2f(this.width - 0.01f, boxHeight / 2.0f));
        this.middleBox.setOffset(this.offset);
    }

    public void resetFixture() {
        if (Physics2D.isLocked()) {
            this.resetFixtureNextFrame = true;
            return;
        }
        this.resetFixtureNextFrame = false;

        if (this.gameObject.hasComponent(RigidBody2D.class)) {
            RigidBody2D rb = this.gameObject.getComponent(RigidBody2D.class);
            Physics2D.resetPillbox2DCollider(rb, this);
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
