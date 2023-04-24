package editor.renderer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {

    private final Matrix4f projectionMatrix, viewMatrix, inverseProjectionMatrix, inverseViewMatrix;
    private final Vector3f position;
    private final Vector2f projectionSize = new Vector2f(32.0f * 40.0f, 32.0f * 21.0f);
//    private final Vector2f projectionSize = new Vector2f(32.0f * 50.0f, 32.0f * 20.0f);

    private float zoom = 1.0f;

    public Camera(Vector3f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.inverseProjectionMatrix = new Matrix4f();
        this.inverseViewMatrix = new Matrix4f();
        adjustProjection();
    }

    public void adjustProjection() {
        this.projectionMatrix.identity();
        this.projectionMatrix.ortho(0.0f, this.projectionSize.x * this.zoom, 0.0f, this.projectionSize.y * this.zoom, 0.0f, 100.0f);
        this.projectionMatrix.invert(this.inverseProjectionMatrix);
    }

    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.viewMatrix.identity();
        this.viewMatrix.set(this.viewMatrix.lookAt(new Vector3f(this.position.x, this.position.y, 20.0f), cameraFront.add(this.position.x, this.position.y, 0.0f), cameraUp));

        this.viewMatrix.invert(this.inverseViewMatrix);
        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix() { return this.projectionMatrix; }

    public Vector3f getPosition() { return this.position; }

    public Matrix4f getInverseProjectionMatrix() { return this.inverseProjectionMatrix; }

    public Matrix4f getInverseViewMatrix() { return this.inverseViewMatrix; }

    public Vector2f getProjectionSize() { return this.projectionSize; }

    public float getZoom() { return this.zoom; }

    public void setZoom(float zoom) { this.zoom = zoom; }

    public void addZoom(float value) { this.zoom += value; }
}
