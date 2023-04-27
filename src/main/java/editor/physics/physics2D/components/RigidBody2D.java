package editor.physics.physics2D.components;

import editor.entity.component.Component;
import editor.physics.physics2D.enums.BodyType;
import editor.stuff.Window;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.joml.Math;
import org.joml.Vector2f;

public class RigidBody2D extends Component {

    private final Vector2f velocity = new Vector2f();
    private float angularDamping = 0.8f;
    private float linearDamping = 0.9f;
    private float mass = 1.0f;
    private BodyType bodyType = BodyType.Dynamic;
    private float friction = 0.1f;
    private float angularVelocity = 0.0f;
    private float gravityScale = 1.0f;
    private boolean isTrigger = false;

    private boolean fixedRotation = false;
    private boolean continuesCollision = false;

    private transient Body rawBody = null;

    @Override
    public void update() {
        if (this.rawBody == null)
            return;

        switch (this.bodyType) {
            case Static -> this.rawBody.setTransform(new Vec2(this.gameObject.transform.position.x, this.gameObject.transform.position.y), this.gameObject.transform.rotation);
            case Dynamic, Kinematic -> {
                this.gameObject.transform.position.set(this.rawBody.getPosition().x, this.rawBody.getPosition().y, this.gameObject.transform.position.z);
                this.gameObject.transform.rotation = (float) Math.toDegrees(this.rawBody.getAngle());
                Vec2 velocity = this.rawBody.getLinearVelocity();
                this.velocity.set(velocity.x, velocity.y);
            }
        }
    }

    public void addVelocity(Vector2f force) {
        if (this.rawBody != null)
            this.rawBody.applyForceToCenter(new Vec2(force.x, force.y));
    }

    public void addImpulse(Vector2f force) {
        if (this.rawBody != null)
            this.rawBody.applyLinearImpulse(new Vec2(force.x, force.y), this.rawBody.getWorldCenter());
    }

    public Vector2f getVelocity() { return this.velocity; }

    public void setVelocity(Vector2f velocity) {
        this.velocity.set(velocity);
        if (this.rawBody != null)
            this.rawBody.setLinearVelocity(new Vec2(this.velocity.x, this.velocity.y));
    }

    public void setAngularVelocity(float velocity) {
        this.angularVelocity = velocity;
        if (this.rawBody != null)
            this.rawBody.setAngularVelocity(this.angularVelocity);
    }

    public void setGravityScale(float gravityScale) {
        this.gravityScale = gravityScale;
        if (this.rawBody != null)
            this.rawBody.setGravityScale(this.gravityScale);
    }

    public void setIsTrigger(boolean isTrigger) {
        this.isTrigger = isTrigger;
        if (this.rawBody != null) {
            Window.getPhysics2D().setIsTrigger(this, isTrigger);
        }
    }

    public boolean isTrigger() { return this.isTrigger; }

    public float getFriction() { return this.friction; }

    public void setFriction(float friction) { this.friction = friction; }

    public float getAngularDamping() { return this.angularDamping; }

    public void setAngularDamping(float angularDamping) { this.angularDamping = angularDamping; }

    public float getLinearDamping() { return this.linearDamping; }

    public void setLinearDamping(float linearDamping) { this.linearDamping = linearDamping; }

    public float getMass() { return this.mass; }

    public void setMass(float mass) { this.mass = mass; }

    public BodyType getBodyType() { return this.bodyType; }

    public void setBodyType(BodyType bodyType) { this.bodyType = bodyType; }

    public boolean isFixedRotation() { return this.fixedRotation; }

    public void setFixedRotation(boolean fixedRotation) { this.fixedRotation = fixedRotation; }

    public boolean isContinuesCollision() { return this.continuesCollision; }

    public void setContinuesCollision(boolean continuesCollision) { this.continuesCollision = continuesCollision; }

    public float getAngularVelocity() { return this.angularVelocity; }

    public float getGravityScale() { return this.gravityScale; }

    public Body getRawBody() { return this.rawBody; }

    public void setRawBody(Body rawBody) { this.rawBody = rawBody; }
}
