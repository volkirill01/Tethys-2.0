package engine.physics.physics2D.components;

import engine.editor.gui.EditorGUI;
import engine.entity.component.Component;
import engine.physics.physics2D.Physics2D;
import engine.physics.physics2D.enums.BodyType;
import imgui.ImGui;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.joml.Vector2f;

public class RigidBody2D extends Component {

    private transient Body rawBody = null;

    private BodyType bodyType = BodyType.Dynamic;
    private final Vector2f velocity = new Vector2f();

    private boolean fixedRotation = false;
    private boolean continuesCollision = false;

    private float density = 1.0f;
    private float friction = 0.5f;
    private float restitution = 0.0f; // Bounciness

    // ==========================
    // TODO MOVE THIS IN TO PHYSICS MATERIAL
    private float angularDamping = 0.8f;
    private float linearDamping = 0.9f;
    private float mass = 1.0f;
    private float angularVelocity = 0.0f;
    private float gravityScale = 1.0f;
    private boolean isTrigger = false;

    @Override
    public void start() { Physics2D.add(this.gameObject); }

    @Override
    public void editorUpdate() {
        if (this.rawBody == null)
            return;

        this.rawBody.setTransform(new Vec2(this.gameObject.transform.position.x, this.gameObject.transform.position.y), this.gameObject.transform.rotation.z); // TODO FIX BUG, COLLIDER ON FIRST START OFFSET NOT UPDATED
    }

    @Override
    public void update() {
        if (this.rawBody == null)
            return;

        switch (this.bodyType) {
            case Static -> this.rawBody.setTransform(new Vec2(this.gameObject.transform.position.x, this.gameObject.transform.position.y), this.gameObject.transform.rotation.z);
            case Dynamic, Kinematic -> {
                this.gameObject.transform.position.set(this.rawBody.getPosition().x, this.rawBody.getPosition().y, this.gameObject.transform.position.z);
                this.gameObject.transform.rotation.z = this.rawBody.getAngle();
                Vec2 velocity = this.rawBody.getLinearVelocity();
                this.velocity.set(velocity.x, velocity.y);
            }
        }
    }

    @Override
    public void destroy() { Physics2D.destroyGameObject(this.gameObject); }

    @Override
    public void imgui() {
        ImGui.beginDisabled();
        EditorGUI.field_Vector2f("Velocity", this.velocity);
        ImGui.endDisabled();
        setBodyType((BodyType) EditorGUI.field_Enum("Body Type", this.bodyType));

        this.fixedRotation = EditorGUI.field_Boolean("Fixed Rotation", this.fixedRotation);
        this.continuesCollision = EditorGUI.field_Boolean("Continues Collision", this.continuesCollision);

        this.density = EditorGUI.field_Float("Density", this.density);
        this.friction = EditorGUI.field_Float("Friction", this.friction);
        this.restitution = EditorGUI.field_Float("Restitution(Bounciness)", this.restitution);

//        this.angularDamping = EditorGUI.field_Float("Angular Damping", this.angularDamping);
//        this.linearDamping = EditorGUI.field_Float("Linear Damping", this.linearDamping);
//        this.mass = EditorGUI.field_Float("Mass", this.mass, EditorGUI.DEFAULT_FLOAT_FORMAT + "kg");
//        this.angularVelocity = EditorGUI.field_Float("Angular Velocity", this.angularVelocity);
//        this.gravityScale = EditorGUI.field_Float("Gravity Scale", this.gravityScale);
//        this.isTrigger = EditorGUI.field_Boolean("Is Trigger", this.isTrigger);
    }

    // =========================================
    public void addVelocity(Vector2f force) {
        if (this.rawBody != null)
            this.rawBody.applyForceToCenter(new Vec2(force.x, force.y));
    }

    public void addImpulse(Vector2f force) {
        if (this.rawBody != null)
            this.rawBody.applyLinearImpulse(new Vec2(force.x, force.y), this.rawBody.getWorldCenter());
    }

    // =========================================
    public Body getRawBody() { return this.rawBody; }

    public void setRawBody(Body rawBody) { this.rawBody = rawBody; }

    public BodyType getBodyType() { return this.bodyType; }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
        switch (this.bodyType) {
            case Static -> this.rawBody.setType(org.jbox2d.dynamics.BodyType.STATIC);
            case Dynamic -> this.rawBody.setType(org.jbox2d.dynamics.BodyType.DYNAMIC);
            case Kinematic -> this.rawBody.setType(org.jbox2d.dynamics.BodyType.KINEMATIC);
        }
    }

    public Vector2f getVelocity() { return this.velocity; }

    public void setVelocity(Vector2f velocity) {
        this.velocity.set(velocity);
        if (this.rawBody != null)
            this.rawBody.setLinearVelocity(new Vec2(this.velocity.x, this.velocity.y));
    }

    public boolean isFixedRotation() { return this.fixedRotation; }

    public void setFixedRotation(boolean fixedRotation) { this.fixedRotation = fixedRotation; }

    public boolean isContinuesCollision() { return this.continuesCollision; }

    public void setContinuesCollision(boolean continuesCollision) { this.continuesCollision = continuesCollision; }

    public float getDensity() { return this.density; }

    public void setDensity(float density) { this.density = density; }

    public float getFriction() { return this.friction; }

    public void setFriction(float friction) { this.friction = friction; }

    public float getRestitution() { return this.restitution; }

    public void setRestitution(float restitution) { this.restitution = restitution; }

    // ==========================
    public float getAngularDamping() { return this.angularDamping; }

    public void setAngularDamping(float angularDamping) { this.angularDamping = angularDamping; }

    public float getLinearDamping() { return this.linearDamping; }

    public void setLinearDamping(float linearDamping) { this.linearDamping = linearDamping; }

    public float getMass() { return this.mass; }

    public void setMass(float mass) { this.mass = mass; }

    public float getAngularVelocity() { return this.angularVelocity; }

    public void setAngularVelocity(float velocity) {
        this.angularVelocity = velocity;
        if (this.rawBody != null)
            this.rawBody.setAngularVelocity(this.angularVelocity);
    }

    public float getGravityScale() { return this.gravityScale; }

    public void setGravityScale(float gravityScale) {
        this.gravityScale = gravityScale;
        if (this.rawBody != null)
            this.rawBody.setGravityScale(this.gravityScale);
    }

    public boolean isTrigger() { return this.isTrigger; }

    public void setIsTrigger(boolean isTrigger) {
        this.isTrigger = isTrigger;
        if (this.rawBody != null)
            Physics2D.setIsTrigger(this, isTrigger);
    }
}
