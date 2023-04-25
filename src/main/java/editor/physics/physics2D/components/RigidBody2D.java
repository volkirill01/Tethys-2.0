package editor.physics.physics2D.components;

import editor.entity.component.Component;
import editor.physics.physics2D.enums.BodyType;
import org.jbox2d.dynamics.Body;
import org.joml.Math;
import org.joml.Vector2f;

public class RigidBody2D extends Component {

    private final Vector2f velocity = new Vector2f();
    private float angularDamping = 0.8f;
    private float linearDamping = 0.9f;
    private float mass = 1.0f;
    private BodyType bodyType = BodyType.Dynamic;

    private boolean fixedRotation = false;
    private boolean continuesCollision = true;

    private transient Body rawBody = null;

    @Override
    public void update() {
        if (this.rawBody == null)
            return;

        this.gameObject.transform.position.set(this.rawBody.getPosition().x, this.rawBody.getPosition().y, this.gameObject.transform.position.z);
        this.gameObject.transform.rotation = (float) Math.toDegrees(this.rawBody.getAngle());
    }

    public Vector2f getVelocity() { return this.velocity; }

    public void setVelocity(Vector2f velocity) { this.velocity.set(velocity); }

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

    public Body getRawBody() { return this.rawBody; }

    public void setRawBody(Body rawBody) { this.rawBody = rawBody; }
}
