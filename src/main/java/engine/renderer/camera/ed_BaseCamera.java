package engine.renderer.camera;

import engine.entity.component.Component;
import engine.renderer.stuff.Fbo;
import engine.stuff.Window;
import engine.stuff.customVariables.Color;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class ed_BaseCamera extends Component {

    public enum CameraType {
        Orthographic,
        Perspective
    }

    protected transient Fbo outputFbo;
    protected transient final Matrix4f viewMatrix, projectionMatrix, inverseProjectionMatrix, inverseViewMatrix;

    protected final Vector2f orthographicProjectionSize = new Vector2f(6.0f, 3.37f);

    protected transient final Vector3f position = new Vector3f();
    protected transient final Vector3f rotation = new Vector3f();

    protected float zoom = 1.0f;
    protected final float nearPlane = 0.0f;
    protected final float farPlane = 100.0f;
    protected final float fov = 45.0f;

    protected CameraType cameraType = CameraType.Orthographic;

    protected final Color backgroundColor = Color.WHITE.copy();

    public ed_BaseCamera(Vector3f position, Vector3f rotation) {
        this.position.set(position);
        this.rotation.set(rotation);

        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.inverseProjectionMatrix = new Matrix4f();
        this.inverseViewMatrix = new Matrix4f();
        adjustProjection();

        outputFbo = new Fbo(Window.getScreenWidth(), Window.getScreenHeight(), Fbo.DEPTH_RENDER_BUFFER);
    }

    @Override
    public void start() { adjustProjection(); }

    @Override
    public void editorUpdate() { this.update(); }

    @Override
    public void update() { adjustProjection(); }

    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.viewMatrix.identity();
        this.viewMatrix.set(this.viewMatrix.lookAt(new Vector3f(this.position.x, this.position.y, 20.0f), cameraFront.add(this.position.x, this.position.y, 0.0f), cameraUp));
        if (this.cameraType == CameraType.Perspective) {
            this.viewMatrix.rotate((float) Math.toRadians(this.rotation.x), 1, 0, 0);
            this.viewMatrix.rotate((float) Math.toRadians(this.rotation.y), 0, 1, 0);
            this.viewMatrix.scale(5.0f / this.zoom); // Perspective camera zoom scaled by 5 to match the zoom of orthographic camera
        }

        this.viewMatrix.invert(this.inverseViewMatrix);
        return this.viewMatrix;
    }

    public void adjustProjection() {
        if (this.cameraType == CameraType.Orthographic) {
            this.projectionMatrix.identity();
            this.projectionMatrix.ortho(-(this.orthographicProjectionSize.x / 2) * this.zoom, this.orthographicProjectionSize.x / 2 * this.zoom, -(this.orthographicProjectionSize.y / 2) * this.zoom, this.orthographicProjectionSize.y / 2 * this.zoom, this.nearPlane, this.farPlane);
        } else {
            this.projectionMatrix.identity();
            this.projectionMatrix.perspective((float) Math.toRadians(this.fov), Window.getTargetAspectRatio() + 0.15f, this.nearPlane, this.farPlane); // TODO FIND WHY THIS VALUE(0.15f), ADD THIS VALUE TO ASPECT RATION TO REMOVE STRETCH EFFECT
        }
        this.projectionMatrix.invert(this.inverseProjectionMatrix);
    }

    public Matrix4f getProjectionMatrix() { return this.projectionMatrix; }

    public Matrix4f getInverseProjectionMatrix() { return this.inverseProjectionMatrix; }

    public Matrix4f getInverseViewMatrix() { return this.inverseViewMatrix; }

    public Vector2f getOrthographicProjectionSize() { return this.orthographicProjectionSize; }

    public Vector3f getPosition() { return this.position; }

    public Vector3f getRotation() { return this.rotation; }

    public float getZoom() { return this.zoom; }

    public CameraType getCameraType() { return this.cameraType; }

    public void setCameraType(CameraType type) { this.cameraType = type; }

    public Color getBackgroundColor() { return this.backgroundColor; }

    public Fbo getOutputFob() { return this.outputFbo; }
}
