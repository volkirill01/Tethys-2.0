package engine.physics.physics2D;

import engine.entity.GameObject;
import engine.physics.physics2D.components.ed_Collider2D;
import engine.physics.physics2D.components.RigidBody2D;
import engine.physics.physics2D.components.colliders.Box2DCollider;
import engine.physics.physics2D.components.colliders.Circle2DCollider;
import engine.physics.physics2D.components.colliders.Pillbox2DCollider;
import engine.profiling.Profiler;
import engine.stuff.Settings;
import engine.stuff.utils.Time;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.joml.Vector2f;

public class Physics2D {

    private static final Vec2 gravity = new Vec2(Settings.GRAVITY_2D.x, Settings.GRAVITY_2D.y);
    private static final World world = new World(gravity);

    private static float physicsTime = 0.0f;
    private static final int velocityIterations = 8;
    private static final int positionIterations = 3;

    static {
        world.setContactListener(new ContactListener2D());
    }

    public static void add(GameObject obj) {
        if (!obj.hasComponent(RigidBody2D.class) || !obj.hasComponent(ed_Collider2D.class))
            return;

        Profiler.startTimer(String.format("Physics2D Add GameObject - '%s'", obj.getName()));
        RigidBody2D rb = obj.getComponent(RigidBody2D.class);

        if (obj.getComponent(RigidBody2D.class).getRawBody() == null) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.angle = (float) Math.toRadians(obj.transform.rotation.z); // TODO DEBUG COLLISIONS AND FIX ROTATION OR SOMETHING ELSE
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

            Body body = world.createBody(bodyDef);
            body.m_mass = rb.getMass();
            rb.setRawBody(body);

            if (obj.hasComponent(Box2DCollider.class))
                addBox2DCollider(rb, obj.getComponent(Box2DCollider.class));

            if (obj.hasComponent(Circle2DCollider.class))
                addCircle2DCollider(rb, obj.getComponent(Circle2DCollider.class));

            if (obj.hasComponent(Pillbox2DCollider.class))
                addPillbox2DCollider(rb, obj.getComponent(Pillbox2DCollider.class));
        }
        Profiler.stopTimer(String.format("Physics2D Add GameObject - '%s'", obj.getName()));
    }

    public static void destroyGameObject(GameObject obj) {
        Profiler.startTimer(String.format("Physics2D Destroy GameObject - '%s'", obj.getName()));
        if (!obj.hasComponent(RigidBody2D.class)) return;

        RigidBody2D rb = obj.getComponent(RigidBody2D.class);
        if (rb.getRawBody() != null) {
            world.destroyBody(rb.getRawBody());
            rb.setRawBody(null);
        }
        Profiler.stopTimer(String.format("Physics2D Destroy GameObject - '%s'", obj.getName()));
    }

    public static void update() {
        Profiler.startTimer("Physics2D Update");
        physicsTime += Time.deltaTime();
        if (physicsTime >= 0.0f) {
            physicsTime -= Time.deltaTime();
            world.step(Time.deltaTime(), velocityIterations, positionIterations);
        }
        Profiler.stopTimer("Physics2D Update");
    }

    public static void setIsTrigger(RigidBody2D rb, boolean isTrigger) {
        Body body = rb.getRawBody();
        if (body == null) return; // If object not contain box 2D collider return

        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            fixture.m_isSensor = isTrigger;
            fixture = fixture.m_next;
        }
    }

    public static void resetBox2DCollider(RigidBody2D rb, Box2DCollider collider) {
        Body body = rb.getRawBody();
        if (body == null) return; // If object not contain box 2D collider return

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++)
            body.destroyFixture(body.getFixtureList());

        addBox2DCollider(rb, collider);
        body.resetMassData();
    }

    public static void resetCircle2DCollider(RigidBody2D rb, Circle2DCollider collider) {
        Body body = rb.getRawBody();
        if (body == null) return; // If object not contain circle 2D collider return

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++)
            body.destroyFixture(body.getFixtureList());

        addCircle2DCollider(rb, collider);
        body.resetMassData();
    }

    public static void resetPillbox2DCollider(RigidBody2D rb, Pillbox2DCollider collider) {
        Body body = rb.getRawBody();
        if (body == null) return; // If object not contain box 2D collider return

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++)
            body.destroyFixture(body.getFixtureList());

        addPillbox2DCollider(rb, collider);
        body.resetMassData();
    }

    private static int fixtureListSize(Body body) {
        int size = 0;
        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            size++;
            fixture = fixture.m_next;
        }
        return size;
    }

    public static void addBox2DCollider(RigidBody2D rb, Box2DCollider collider) {
        Profiler.startTimer(String.format("Physics2D Add BoxCollider2D - '%s'", rb.gameObject.getName()));
        Body body = rb.getRawBody();
        if (body == null) throw new NullPointerException(String.format("'%s' - Raw Body must not be null.", rb.gameObject.getName()));

        PolygonShape shape = new PolygonShape();

        Vector2f halfSize = new Vector2f(collider.getSize()).div(2.0f).mul(rb.gameObject.transform.scale.x, rb.gameObject.transform.scale.y);
        Vector2f offset = collider.getOffset();
        shape.setAsBox(Math.abs(halfSize.x), Math.abs(halfSize.y), new Vec2(offset.x, offset.y), (float) Math.toRadians(rb.gameObject.transform.rotation.z));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = rb.getFriction();
        fixtureDef.userData = collider.gameObject;
        fixtureDef.isSensor = rb.isTrigger();
        body.createFixture(fixtureDef);
        Profiler.stopTimer(String.format("Physics2D Add BoxCollider2D - '%s'", rb.gameObject.getName()));
    }

    public static void addCircle2DCollider(RigidBody2D rb, Circle2DCollider collider) {
        Profiler.startTimer(String.format("Physics2D Add CircleCollider2D - '%s'", rb.gameObject.getName()));
        Body body = rb.getRawBody();
        if (body == null) throw new NullPointerException(String.format("'%s' - Raw Body must not be null.", rb.gameObject.getName()));

        CircleShape shape = new CircleShape();
        shape.setRadius(collider.getRadius() / 2.0f);
        Vector2f offset = collider.getOffset();
        shape.m_p.set(offset.x, offset.y);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = rb.getFriction();
        fixtureDef.userData = collider.gameObject;
        fixtureDef.isSensor = rb.isTrigger();
        body.createFixture(fixtureDef);
        Profiler.stopTimer(String.format("Physics2D Add CircleCollider2D - '%s'", rb.gameObject.getName()));
    }

    public static void addPillbox2DCollider(RigidBody2D rb, Pillbox2DCollider collider) {
        Profiler.startTimer(String.format("Physics2D Add PillboxCollider2D - '%s'", rb.gameObject.getName()));
        Body body = rb.getRawBody();
        if (body == null) throw new NullPointerException(String.format("'%s' - Raw Body must not be null.", rb.gameObject.getName()));

        addCircle2DCollider(rb, collider.getTopCircle());
        addCircle2DCollider(rb, collider.getBottomCircle());
        addBox2DCollider(rb, collider.getMiddleBox());
        Profiler.stopTimer(String.format("Physics2D Add PillboxCollider2D - '%s'", rb.gameObject.getName()));
    }

    public static RayCastInfo rayCast(GameObject requestingObject, Vector2f startPoint, Vector2f endPoint) {
        Profiler.startTimer(String.format("Physics2D RayCast - RequestingObject: '%s'", requestingObject.getName()));
        RayCastInfo callback = new RayCastInfo(requestingObject);
        world.raycast(callback, new Vec2(startPoint.x, startPoint.y), new Vec2(endPoint.x, endPoint.y));
        Profiler.stopTimer(String.format("Physics2D RayCast - RequestingObject: '%s'", requestingObject.getName()));
        return callback;
    }

    public static boolean isLocked() { return world.isLocked(); }
}
