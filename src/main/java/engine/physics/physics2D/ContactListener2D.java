package engine.physics.physics2D;

import engine.entity.GameObject;
import engine.entity.component.Component;
import engine.profiling.Profiler;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class ContactListener2D implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Profiler.startTimer(String.format("Begin Contact - (A: '%s', B: '%s')", ((GameObject) contact.getFixtureA().getUserData()).getName(), ((GameObject) contact.getFixtureB().getUserData()).getName()));
        GameObject objA = (GameObject) contact.getFixtureA().getUserData();
        GameObject objB = (GameObject) contact.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y); // Direction with B object hit A object
        Vector2f bNormal = new Vector2f(aNormal).negate(); // Direction with A object hit B object

        for (Component c : objA.getAllComponents())
            c.beginCollision(objB, contact, aNormal);

        for (Component c : objB.getAllComponents())
            c.beginCollision(objA, contact, bNormal);
        Profiler.stopTimer(String.format("Begin Contact - (A: '%s', B: '%s')", ((GameObject) contact.getFixtureA().getUserData()).getName(), ((GameObject) contact.getFixtureB().getUserData()).getName()));
    }

    @Override
    public void endContact(Contact contact) {
        Profiler.startTimer(String.format("End Contact - (A: '%s', B: '%s')", ((GameObject) contact.getFixtureA().getUserData()).getName(), ((GameObject) contact.getFixtureB().getUserData()).getName()));
        GameObject objA = (GameObject) contact.getFixtureA().getUserData();
        GameObject objB = (GameObject) contact.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y); // Direction with B object hit A object
        Vector2f bNormal = new Vector2f(aNormal).negate(); // Direction with A object hit B object

        for (Component c : objA.getAllComponents())
            c.endCollision(objB, contact, aNormal);

        for (Component c : objB.getAllComponents())
            c.endCollision(objA, contact, bNormal);
        Profiler.stopTimer(String.format("End Contact - (A: '%s', B: '%s')", ((GameObject) contact.getFixtureA().getUserData()).getName(), ((GameObject) contact.getFixtureB().getUserData()).getName()));
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
        Profiler.startTimer(String.format("Pre Solve - (A: '%s', B: '%s')", ((GameObject) contact.getFixtureA().getUserData()).getName(), ((GameObject) contact.getFixtureB().getUserData()).getName()));
        GameObject objA = (GameObject) contact.getFixtureA().getUserData();
        GameObject objB = (GameObject) contact.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y); // Direction with B object hit A object
        Vector2f bNormal = new Vector2f(aNormal).negate(); // Direction with A object hit B object

        for (Component c : objA.getAllComponents())
            c.preSolve(objB, contact, aNormal);

        for (Component c : objB.getAllComponents())
            c.preSolve(objA, contact, bNormal);
        Profiler.stopTimer(String.format("Pre Solve - (A: '%s', B: '%s')", ((GameObject) contact.getFixtureA().getUserData()).getName(), ((GameObject) contact.getFixtureB().getUserData()).getName()));
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {
        Profiler.startTimer(String.format("Post Solve - (A: '%s', B: '%s')", ((GameObject) contact.getFixtureA().getUserData()).getName(), ((GameObject) contact.getFixtureB().getUserData()).getName()));
        GameObject objA = (GameObject) contact.getFixtureA().getUserData();
        GameObject objB = (GameObject) contact.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y); // Direction with B object hit A object
        Vector2f bNormal = new Vector2f(aNormal).negate(); // Direction with A object hit B object

        for (Component c : objA.getAllComponents())
            c.postSolve(objB, contact, aNormal);

        for (Component c : objB.getAllComponents())
            c.postSolve(objA, contact, bNormal);
        Profiler.stopTimer(String.format("Post Solve - (A: '%s', B: '%s')", ((GameObject) contact.getFixtureA().getUserData()).getName(), ((GameObject) contact.getFixtureB().getUserData()).getName()));
    }
}
