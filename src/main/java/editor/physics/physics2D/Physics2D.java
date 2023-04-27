package editor.physics.physics2D;

import editor.entity.GameObject;
import editor.physics.physics2D.components.Collider2D;
import editor.physics.physics2D.components.RigidBody2D;
import editor.physics.physics2D.components.colliders.Box2DCollider;
import editor.physics.physics2D.components.colliders.Circle2DCollider;
import editor.physics.physics2D.components.colliders.Pillbox2DCollider;
import editor.stuff.Settings;
import editor.stuff.utils.Time;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.joml.Vector2f;

public class Physics2D {

    private final Vec2 gravity = new Vec2(Settings.gravity2D.x, Settings.gravity2D.y);
    private final World world = new World(this.gravity);

    private float physicsTime = 0.0f;
    private final float physicsTimeStep = 1.0f / 60.0f; // 60 frames per 1 second
    private final int velocityIterations = 8;
    private final int positionIterations = 3;

    public Physics2D() { world.setContactListener(new ContactListener2D()); }

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
            bodyDef.gravityScale = rb.getGravityScale();
            bodyDef.angularVelocity = rb.getAngularVelocity();
            bodyDef.userData = rb.gameObject;

            switch (rb.getBodyType()) {
                case Static -> bodyDef.type = BodyType.STATIC;
                case Dynamic -> bodyDef.type = BodyType.DYNAMIC;
                case Kinematic -> bodyDef.type = BodyType.KINEMATIC;
            }

            Body body = this.world.createBody(bodyDef);
            body.m_mass = rb.getMass();
            rb.setRawBody(body);

            if (obj.hasComponent(Box2DCollider.class))
                addBox2DCollider(rb, obj.getComponent(Box2DCollider.class));

            if (obj.hasComponent(Circle2DCollider.class))
                addCircle2DCollider(rb, obj.getComponent(Circle2DCollider.class));

            if (obj.hasComponent(Pillbox2DCollider.class))
                addPillbox2DCollider(rb, obj.getComponent(Pillbox2DCollider.class));
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
        this.physicsTime += Time.deltaTime();
        if (this.physicsTime >= 0.0f) {
            this.physicsTime -= this.physicsTimeStep;
            this.world.step(this.physicsTimeStep, this.velocityIterations, this.positionIterations);
        }
    }

    public void setIsTrigger(RigidBody2D rb, boolean isTrigger) {
        Body body = rb.getRawBody();
        if (body == null) return; // If object not contain box 2D collider return

        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            fixture.m_isSensor = isTrigger;
            fixture = fixture.m_next;
        }
    }

    public void resetBox2DCollider(RigidBody2D rb, Box2DCollider collider) {
        Body body = rb.getRawBody();
        if (body == null) return; // If object not contain box 2D collider return

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++)
            body.destroyFixture(body.getFixtureList());

        addBox2DCollider(rb, collider);
        body.resetMassData();
    }

    public void resetCircle2DCollider(RigidBody2D rb, Circle2DCollider collider) {
        Body body = rb.getRawBody();
        if (body == null) return; // If object not contain circle 2D collider return

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++)
            body.destroyFixture(body.getFixtureList());

        addCircle2DCollider(rb, collider);
        body.resetMassData();
    }

    public void resetPillbox2DCollider(RigidBody2D rb, Pillbox2DCollider collider) {
        Body body = rb.getRawBody();
        if (body == null) return; // If object not contain box 2D collider return

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++)
            body.destroyFixture(body.getFixtureList());

        addPillbox2DCollider(rb, collider);
        body.resetMassData();
    }

    private int fixtureListSize(Body body) {
        int size = 0;
        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            size++;
            fixture = fixture.m_next;
        }
        return size;
    }

    public void addBox2DCollider(RigidBody2D rb, Box2DCollider collider) {
        Body body = rb.getRawBody();
        if (body == null) throw new NullPointerException("Raw Body must not be null.");

        PolygonShape shape = new PolygonShape();

        Vector2f halfSize = new Vector2f(collider.getSize()).div(2.0f);
        Vector2f offset = collider.getOffset();
        shape.setAsBox(halfSize.x, halfSize.y, new Vec2(offset.x, offset.y), 0.0f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = rb.getFriction();
        fixtureDef.userData = collider.gameObject;
        fixtureDef.isSensor = rb.isTrigger();
        body.createFixture(fixtureDef);
    }

    public void addCircle2DCollider(RigidBody2D rb, Circle2DCollider collider) {
        Body body = rb.getRawBody();
        if (body == null) throw new NullPointerException("Raw Body must not be null.");

        CircleShape shape = new CircleShape();
        shape.setRadius(collider.getRadius() / 2.0f);
        shape.m_p.set(collider.getOffset().x, collider.getOffset().y);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = rb.getFriction();
        fixtureDef.userData = collider.gameObject;
        fixtureDef.isSensor = rb.isTrigger();
        body.createFixture(fixtureDef);
    }

    public void addPillbox2DCollider(RigidBody2D rb, Pillbox2DCollider collider) {
        Body body = rb.getRawBody();
        if (body == null) throw new NullPointerException("Raw Body must not be null.");

        addCircle2DCollider(rb, collider.getTopCircle());
        addCircle2DCollider(rb, collider.getBottomCircle());
        addBox2DCollider(rb, collider.getMiddleBox());
    }

    public RayCastInfo raycast(GameObject requestingObject, Vector2f startPoint, Vector2f endPoint) {
        RayCastInfo callback = new RayCastInfo(requestingObject);
        world.raycast(callback, new Vec2(startPoint.x, startPoint.y), new Vec2(endPoint.x, endPoint.y));
        return callback;
    }

    public boolean isLocked() { return world.isLocked(); }
}
