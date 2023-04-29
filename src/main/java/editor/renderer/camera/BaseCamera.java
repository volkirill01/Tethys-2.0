package editor.renderer.camera;

import editor.TestFieldsWindow;
import editor.stuff.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class BaseCamera {

    private final Matrix4f projectionMatrix, viewMatrix, inverseProjectionMatrix, inverseViewMatrix;
    private final Vector3f position;
    private final Vector3f rotation = new Vector3f();

    private final Vector2f projectionSize = new Vector2f(6.0f, 3.0f);

    private float zoom = 1.0f;
    private final float nearPlane = 0.0f;
    private final float farPlane = 100.0f;
    private final float fov = 70.0f;

    private CameraType cameraType = CameraType.Orthographic;

    public BaseCamera(Vector3f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.inverseProjectionMatrix = new Matrix4f();
        this.inverseViewMatrix = new Matrix4f();
        adjustProjection();
    }

    public void adjustProjection() {
        if (this.cameraType == CameraType.Orthographic) {
            this.projectionMatrix.identity();
            this.projectionMatrix.ortho(-(this.projectionSize.x / 2), this.projectionSize.x / 2, -(this.projectionSize.y / 2), this.projectionSize.y / 2, nearPlane, farPlane);
        } else {
            this.projectionMatrix.identity();
            this.projectionMatrix.perspective(this.fov, Window.getTargetAspectRatio(), this.nearPlane, this.farPlane);
        }
        this.projectionMatrix.invert(this.inverseProjectionMatrix);
    }

    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.viewMatrix.identity();
        this.viewMatrix.set(this.viewMatrix.lookAt(new Vector3f(this.position.x, this.position.y, 20.0f), cameraFront.add(this.position.x, this.position.y, 0.0f), cameraUp));
        this.viewMatrix.scale(this.zoom);
        if (this.cameraType == CameraType.Perspective) {
            this.viewMatrix.rotate((float) Math.toRadians(this.rotation.x), 1, 0, 0);
            this.viewMatrix.rotate((float) Math.toRadians(this.rotation.y), 0, 1, 0);
            this.viewMatrix.scale(4.0f); // In perspective mode camera 4 times smaller than orthographic camera
        }

        this.viewMatrix.invert(this.inverseViewMatrix);
        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix() { return this.projectionMatrix; }

    public Vector3f getPosition() { return this.position; }

    public Vector3f getRotation() { return this.rotation; }

    public Matrix4f getInverseProjectionMatrix() { return this.inverseProjectionMatrix; }

    public Matrix4f getInverseViewMatrix() { return this.inverseViewMatrix; }

    public Vector2f getProjectionSize() { return this.projectionSize; }

    public float getZoom() { return this.zoom; }

    public void setZoom(float zoom) { this.zoom = zoom; }

    public void addZoom(float value) { this.zoom += value; }

    public CameraType getCameraType() { return this.cameraType; }

    public void setCameraType(CameraType cameraType) { this.cameraType = cameraType; }
}
