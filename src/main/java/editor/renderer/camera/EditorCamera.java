package editor.renderer.camera;

import editor.entity.component.Component;
import editor.eventListeners.Input;
import editor.eventListeners.KeyCode;
import editor.scenes.SceneManager;
import editor.stuff.Window;
import editor.stuff.utils.Time;
import org.joml.*;

import java.lang.Math;

public class EditorCamera extends Component { // TODO MAKE BESA CAMERA CLASS AND INHERITANCE TO IT

    private final Matrix4f projectionMatrix, viewMatrix, inverseProjectionMatrix, inverseViewMatrix;
    private final Vector3f position;
    private final Vector3f rotation = new Vector3f();

    private final Vector2f projectionSize = new Vector2f(6.0f, 3.0f);

    private float zoom = 1.0f;
    private static final float nearPlane = 0.0f;
    private static final float farPlane = 100.0f;
    private static final float fov = 45.0f;

    private CameraType cameraType = CameraType.Orthographic;

    private final float startDragDebounce = 0.1f;
    private float dragDebounce = this.startDragDebounce;

    private Vector2f clickOrigin;
    private boolean reset = false;
    private float lerpTime = 0.0f;

    // 2D
    private static final float dragSensitivity = 30.0f;
    private static final float scrollSensitivity = 0.1f;

    // 3D
    private final Vector3f direction = new Vector3f(0.0f);
    private static final float sensitivity = 0.35f;
    private static final float moveSpeed = 3.0f;

    public EditorCamera(Vector3f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.inverseProjectionMatrix = new Matrix4f();
        this.inverseViewMatrix = new Matrix4f();
        this.clickOrigin = new Vector2f();
        adjustProjection();
    }

    @Override
    public void update() {
        if (Input.anyButtonDown() && !Input.buttonDown(KeyCode.F) || Input.getMouseScrollX() != 0.0f || Input.getMouseScrollY() != 0.0f) {
            this.reset = false;
            this.lerpTime = 0.0f;
        }

        // TODO REPLACE STATIC KEY, WITH KEY FROM USER SETTINGS
        if (SceneManager.getCurrentScene().getEditorCamera().getCameraType() == CameraType.Orthographic) {
            if (Input.buttonDown(KeyCode.W))
                this.position.y += moveSpeed / 2.0f * this.zoom * Time.deltaTime();
            else if (Input.buttonDown(KeyCode.S))
                this.position.y -= moveSpeed / 2.0f * this.zoom * Time.deltaTime();

            if (Input.buttonDown(KeyCode.A))
                this.position.x -= moveSpeed / 2.0f * this.zoom * Time.deltaTime();
            else if (Input.buttonDown(KeyCode.D))
                this.position.x += moveSpeed / 2.0f * this.zoom * Time.deltaTime();

            if (Input.buttonDown(KeyCode.Mouse_Button_Right) && this.dragDebounce > 0) {
                this.clickOrigin = Input.getMouseWorldPosition();
                this.dragDebounce -= Time.deltaTime();
                return;
            } else if (Input.buttonDown(KeyCode.Mouse_Button_Right)) {
                Vector2f mousePos = Input.getMouseWorldPosition();
                Vector2f delta = new Vector2f(mousePos).sub(this.clickOrigin);
                delta.mul(Time.deltaTime() * dragSensitivity);
                this.position.sub(delta.x, delta.y, 0.0f);
                this.clickOrigin.lerp(mousePos, Time.deltaTime());
            }

            if (this.dragDebounce <= 0.0f && !Input.buttonDown(KeyCode.Mouse_Button_Right))
                this.dragDebounce = this.startDragDebounce;
        } else {
            if (Input.buttonDown(KeyCode.Mouse_Button_Right)) {
                float angleChange = Input.getMouseDeltaPositionX() * sensitivity;
                this.rotation.y -= angleChange;
                if (this.rotation.y > 180.0f)
                    this.rotation.y -= 180.0f;
                float pitchChange = Input.getMouseDeltaPositionY() * sensitivity;
                this.rotation.x -= pitchChange;
                if (this.rotation.x > 180.0f)
                    this.rotation.x -= 180.0f;
            }

            this.direction.set(0.0f);
            if (Input.buttonDown(KeyCode.W))
                this.direction.z += moveSpeed * Time.deltaTime();
            else if (Input.buttonDown(KeyCode.S))
                this.direction.z -= moveSpeed * Time.deltaTime();

            if (Input.buttonDown(KeyCode.A))
                this.direction.x -= moveSpeed * Time.deltaTime();
            else if (Input.buttonDown(KeyCode.D))
                this.direction.x += moveSpeed * Time.deltaTime();

            if (Input.buttonDown(KeyCode.Space))
                this.direction.y += moveSpeed * Time.deltaTime();
            else if (Input.buttonDown(KeyCode.Left_Shift) || Input.buttonDown(KeyCode.Right_Shift))
                this.direction.y -= moveSpeed * Time.deltaTime();

//            if (this.direction.z != 0.0f) { // TODO FIX CAMERA MOVEMENT
//                this.position.z += (float) Math.cos(Math.toRadians(this.rotation.y)) * -1.0f * this.direction.z;
//                this.position.x += (float) Math.sin(Math.toRadians(this.rotation.y)) * this.direction.z;
//            }
//            if (this.direction.x != 0.0f) {
//                this.position.x -= (float) Math.sin(Math.toRadians(this.rotation.y - 90.0f)) * -1.0f * this.direction.x;
//                this.position.z -= (float) Math.cos(Math.toRadians(this.rotation.y - 90.0f)) * this.direction.x;
//            }

            this.position.y += this.direction.y;
        }

        if (Input.getMouseScrollY() != 0.0f) {
            float addValue = (float) Math.pow(Math.abs(Input.getMouseScrollY() * scrollSensitivity), 0.5f / this.zoom);
            addValue *= Math.signum(Input.getMouseScrollY());
            this.zoom -= addValue;
            this.zoom = Math.max(this.zoom, 0.1f);
        }

        if (Input.buttonDown(KeyCode.F))
            this.reset = true;

        if (this.reset) {
            getPosition().lerp(new Vector3f(0.0f), this.lerpTime);
            getRotation().lerp(new Vector3f(0.0f), this.lerpTime);
            this.zoom += (1.0f - this.zoom) * this.lerpTime;
            this.lerpTime += 0.1f * Time.deltaTime();

            if (Math.abs(this.position.x) <= 0.02f && Math.abs(this.position.y) <= 0.02f &&
                    Math.abs(this.rotation.x) <= 0.02f && Math.abs(this.rotation.y) <= 0.02f &&
                    Math.abs(this.zoom) <= 0.02f) {
                this.position.set(0.0f);
                this.rotation.set(0.0f);
                this.zoom = 1.0f;
                this.reset = false;
                this.lerpTime = 0.0f;
            }
        }
        adjustProjection();
    }

    public void adjustProjection() {
        if (this.cameraType == CameraType.Orthographic) {
            this.projectionMatrix.identity();
            this.projectionMatrix.ortho(-(this.projectionSize.x / 2) * this.zoom, this.projectionSize.x / 2 * this.zoom, -(this.projectionSize.y / 2) * this.zoom, this.projectionSize.y / 2 * this.zoom, nearPlane, farPlane);
        } else {
            this.projectionMatrix.identity();
            this.projectionMatrix.perspective((float) Math.toRadians(fov), Window.getTargetAspectRatio(), nearPlane, farPlane);
        }
        this.projectionMatrix.invert(this.inverseProjectionMatrix);
    }

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

    public Matrix4f getProjectionMatrix() { return this.projectionMatrix; }

    public Vector3f getPosition() { return this.position; }

    public Vector3f getRotation() { return this.rotation; }

    public Matrix4f getInverseProjectionMatrix() { return this.inverseProjectionMatrix; }

    public Matrix4f getInverseViewMatrix() { return this.inverseViewMatrix; }

    public Vector2f getProjectionSize() { return this.projectionSize; }

    public float getZoom() { return this.zoom; }

    public void setZoom(float zoom) { this.zoom = zoom; }

    public CameraType getCameraType() { return this.cameraType; }

    public void setCameraType(CameraType cameraType) { this.cameraType = cameraType; }
}
