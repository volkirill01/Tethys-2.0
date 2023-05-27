package engine.renderer.camera;

import engine.entity.component.Component;
import engine.profiling.Profiler;
import engine.renderer.frameBuffer.FrameBufferAttachmentSpecification;
import engine.renderer.frameBuffer.Framebuffer;
import engine.renderer.frameBuffer.FrameBufferTextureFormat;
import engine.renderer.frameBuffer.FrameBufferTextureSpecification;
import engine.stuff.Window;
import engine.stuff.customVariables.Color;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Arrays;

public class ed_BaseCamera extends Component {

    public enum ProjectionType {
        Orthographic,
        Perspective
    }

    protected transient Framebuffer outputFbo;
    protected transient final Matrix4f viewMatrix, projectionMatrix, inverseProjectionMatrix, inverseViewMatrix;

    protected transient final Vector3f position = new Vector3f();
    protected transient final Vector3f rotation = new Vector3f();

    protected float zoom = 1.0f;

    protected final Vector2f orthographicProjectionSize = new Vector2f(24.0f, 13.48f);
    protected float orthographicNearPlane = -1.0f;
    protected float orthographicFarPlane = 100.0f;

    protected float perspectiveFov = 45.0f;
    protected float perspectiveNearPlane = 0.01f;
    protected float perspectiveFarPlane = 1_000.0f;

    protected ProjectionType projectionType = ProjectionType.Orthographic;

    protected final Color backgroundColor = Color.WHITE.copy();

    public ed_BaseCamera(Vector3f position, Vector3f rotation) {
        this.position.set(position);
        this.rotation.set(rotation);

        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.inverseProjectionMatrix = new Matrix4f();
        this.inverseViewMatrix = new Matrix4f();
        adjustProjectionMatrix();

        this.outputFbo = new Framebuffer(Window.getScreenWidth(), Window.getScreenHeight(), new FrameBufferAttachmentSpecification(Arrays.asList(
                new FrameBufferTextureSpecification(FrameBufferTextureFormat.RGBA8),
                new FrameBufferTextureSpecification(FrameBufferTextureFormat.DEPTH24STENCIL8)
        )));
    }

    @Override
    public void start() { adjustProjectionMatrix(); }

    @Override
    public void editorUpdate() { update(); }

    @Override
    public void update() {
        adjustProjectionMatrix();
        adjustViewMatrix();
    }

    @Override
    public void destroy() { this.outputFbo.freeMemory(); }

    public void adjustProjectionMatrix() {
        Profiler.startTimer("BaseCamera AdjustProjection");
        if (this.projectionType == ProjectionType.Orthographic) {
            this.projectionMatrix.identity();
            this.projectionMatrix.ortho(-(this.orthographicProjectionSize.x / 2) * this.zoom, this.orthographicProjectionSize.x / 2 * this.zoom, -(this.orthographicProjectionSize.y / 2) * this.zoom, this.orthographicProjectionSize.y / 2 * this.zoom, this.orthographicNearPlane, this.orthographicFarPlane);
        } else {
            this.projectionMatrix.identity();
            this.projectionMatrix.perspective((float) Math.toRadians(this.perspectiveFov), Window.getTargetAspectRatio() + 0.15f, this.perspectiveNearPlane, this.perspectiveFarPlane); // TODO FIND WHY THIS VALUE(0.15f), ADD THIS VALUE TO ASPECT RATION TO REMOVE STRETCH EFFECT
        }
        this.projectionMatrix.invert(this.inverseProjectionMatrix);
        Profiler.stopTimer("BaseCamera AdjustProjection");
    }

    public void adjustViewMatrix() {
        Profiler.startTimer("BaseCamera AdjustView");
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.viewMatrix.identity();
        this.viewMatrix.set(this.viewMatrix.lookAt(new Vector3f(this.position.x, this.position.y, 20.0f), cameraFront.add(this.position.x, this.position.y, 0.0f), cameraUp));
        if (this.projectionType == ProjectionType.Perspective) {
            this.viewMatrix.rotate((float) Math.toRadians(this.rotation.x), 1, 0, 0);
            this.viewMatrix.rotate((float) Math.toRadians(this.rotation.y), 0, 1, 0);
            this.viewMatrix.scale(1.0f / this.zoom); // Perspective camera zoom inverted to match the zoom of orthographic camera
        }

        this.viewMatrix.invert(this.inverseViewMatrix);
        Profiler.stopTimer("BaseCamera AdjustView");
    }

    public Matrix4f getProjectionMatrix() { return this.projectionMatrix; }

    public Matrix4f getInverseProjectionMatrix() { return this.inverseProjectionMatrix; }

    public Matrix4f getViewMatrix() { return this.viewMatrix; }

    public Matrix4f getInverseViewMatrix() { return this.inverseViewMatrix; }

    public Vector2f getOrthographicProjectionSize() { return this.orthographicProjectionSize; }

    public Vector3f getPosition() { return this.position; }

    public Vector3f getRotation() { return this.rotation; }

    public float getZoom() { return this.zoom; }

    public ProjectionType getProjectionType() { return this.projectionType; }

    public void setProjectionType(ProjectionType type) { this.projectionType = type; }

    public Color getBackgroundColor() { return this.backgroundColor; }

    public Framebuffer getOutputFob() { return this.outputFbo; }
}
