package editor.physics.physics2D;

import editor.entity.GameObject;
import editor.entity.component.components.Transform;
import editor.physics.physics2D.components.Collider2D;
import editor.physics.physics2D.components.RigidBody2D;
import editor.physics.physics2D.components.colliders.Box2DCollider;
import editor.physics.physics2D.components.colliders.Circle2DCollider;
import editor.stuff.Settings;
import editor.stuff.utils.Time;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.joml.Vector2f;

public class Physics2D {

    private final Vec2 gravity = new Vec2(Settings.gravity2D.x, Settings.gravity2D.y);
    private final World world = new World(this.gravity);

    private float physicsTime = 0.0f;
    private final float physicsTimeStep = 1.0f / 60.0f; // 60 frames per 1 second
    private final int velocityIterations = 8;
    private final int positionIterations = 3;

    public void add(GameObject obj) {
        if (!obj.hasComponent(RigidBody2D.class) || !obj.hasComponent(Collider2D.class))
            return;

        RigidBody2D rb = obj.getComponent(RigidBody2D.class);

        if (obj.getComponent(RigidBody2D.class).getRawBody() == null) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.angle = (float) Math.toRadians(obj.transform.rotation);
            bodyDef.position.set(obj.transform.position.x, obj.transform.position.y);
            bodyDef.angularDamping = rb.getAngularDamping();
            bodyDef.linearDamping = rb.getLinearDamping();
            bodyDef.fixedRotation = rb.isFixedRotation();
            bodyDef.bullet = rb.isContinuesCollision();

            switch (rb.getBodyType()) {
                case Static -> bodyDef.type = BodyType.STATIC;
                case Dynamic -> bodyDef.type = BodyType.DYNAMIC;
                case Kinematic -> bodyDef.type = BodyType.KINEMATIC;
            }

            PolygonShape shape = new PolygonShape();

            if (obj.hasComponent(Circle2DCollider.class)) {
                Circle2DCollider circleCollider = obj.getComponent(Circle2DCollider.class);

                shape.setRadius(circleCollider.getRadius());
            } else if (obj.hasComponent(Box2DCollider.class)) {
                Box2DCollider boxCollider = obj.getComponent(Box2DCollider.class);

                Vector2f halfSize = new Vector2f(boxCollider.getSize()).div(2.0f);
                Vector2f offset = boxCollider.getOffset();
                Vector2f origin = boxCollider.getOrigin();
                shape.setAsBox(halfSize.x, halfSize.y, new Vec2(origin.x, origin.y), 0.0f);

                Vec2 pos = bodyDef.position;
                float xPos = pos.x + offset.x;
                float yPos = pos.y + offset.y;
                bodyDef.position.set(xPos, yPos);
            }

            Body body = this.world.createBody(bodyDef);
            rb.setRawBody(body);
            body.createFixture(shape, rb.getMass());
        }
    }

    public void destroyGameObject(GameObject obj) {
        if (!obj.hasComponent(RigidBody2D.class)) return;

        RigidBody2D rb = obj.getComponent(RigidBody2D.class);
        if (rb.getRawBody() != null) {
            this.world.destroyBody(rb.getRawBody());
            rb.setRawBody(null);
        }
    }

    public void update() {
//        this.world.step(Time.deltaTime(), this.velocityIterations, this.positionIterations);

        this.physicsTime += Time.deltaTime();
        if (this.physicsTime >= 0.0f) {
            this.physicsTime -= this.physicsTimeStep;
            this.world.step(this.physicsTimeStep, this.velocityIterations, this.positionIterations);
        }
    }
}
