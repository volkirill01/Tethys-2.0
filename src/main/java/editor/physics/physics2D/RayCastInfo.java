package editor.physics.physics2D;

import editor.entity.GameObject;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.joml.Vector2f;

public class RayCastInfo implements RayCastCallback {

    public Fixture fixture = null; // Fixture that's we hit
    public Vector2f hitPoint = new Vector2f();
    public Vector2f hitNormal = new Vector2f();
    public float fraction = 0.0f; // Distance alone vector that's we hit (50% must be in middle of rayCast, 0% must be in start of rayCast, 100% must be in end of rayCast)
    public boolean isHit = false;
    private GameObject hitObject = null;
    private final GameObject requestedObject;

    public RayCastInfo(GameObject requestedObject) { this.requestedObject = requestedObject; }

    @Override
    public float reportFixture(Fixture fixture, Vec2 hitPoint, Vec2 hitNormal, float fraction) {
        if (fixture.getUserData() == requestedObject)
            return 1.0f; // If this condition works, skip this object and continue rayCasting

        this.fixture = fixture;
        this.hitPoint = new Vector2f(hitPoint.x, hitPoint.y);
        this.hitNormal = new Vector2f(hitNormal.x, hitNormal.y);
        this.fraction = fraction;
        this.isHit = fraction != 0.0f; // If length of rayCast not equals to 0, we must be collided with something
        this.hitObject = (GameObject) fixture.getUserData();

        return fraction; // This function not stop on first collided object, its continues rayCasting if 'fraction' not equals to 1
    }
}
