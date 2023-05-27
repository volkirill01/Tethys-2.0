package engine.renderer.camera;

import engine.editor.gui.EngineGuiLayer;
import engine.editor.windows.Outliner_Window;
import engine.eventListeners.Input;
import engine.eventListeners.KeyCode;
import engine.renderer.frameBuffer.FrameBufferAttachmentSpecification;
import engine.renderer.frameBuffer.Framebuffer;
import engine.renderer.frameBuffer.FrameBufferTextureFormat;
import engine.renderer.frameBuffer.FrameBufferTextureSpecification;
import engine.stuff.Maths;
import engine.stuff.Window;
import engine.stuff.utils.Time;
import org.joml.*;

import java.lang.Math;
import java.util.Arrays;

public class ed_EditorCamera extends ed_BaseCamera {

    private final float startDragDebounce = 0.1f;
    private float dragDebounce = this.startDragDebounce;

    private Vector2f clickOrigin = new Vector2f();
    private boolean reset = false;
    private float resetLerpTime = 0.0f;
    private float resetLerpSpeed = 0.1f;
    private static final float scrollSensitivity = 3.0f;

    // 2D
    private static final float dragSensitivity = 30.0f;

    // 3D
    private final Vector3f direction = new Vector3f();
    private static final float rotateSensitivity = 0.35f;
    private static final float moveSpeed = 3.0f;

    private final Vector2f initialMousePosition = new Vector2f();

    public ed_EditorCamera(Vector3f position, Vector3f rotation) {
        super(position, rotation);
        this.outputFbo = new Framebuffer(Window.getScreenWidth(), Window.getScreenHeight(), new FrameBufferAttachmentSpecification(Arrays.asList(
                new FrameBufferTextureSpecification(FrameBufferTextureFormat.RGBA8), // Scene render pass
                new FrameBufferTextureSpecification(FrameBufferTextureFormat.RED_INTEGER), // Picking texture render pass(ID pass)
                new FrameBufferTextureSpecification(FrameBufferTextureFormat.DEPTH24STENCIL8)
        )));
    }

    @Override
    public void update() {
        if (Input.anyButtonDown() && !Input.buttonDown(KeyCode.F) || Input.getMouseScrollX() != 0.0f || Input.getMouseScrollY() != 0.0f) {
            this.reset = false;
            this.resetLerpTime = 0.0f;
        }

        // TODO REPLACE STATIC KEY, WITH KEY FROM USER SETTINGS
        if (this.projectionType == ProjectionType.Orthographic) {
            if (EngineGuiLayer.isSceneWindowSelected()) {
                if (!Input.buttonDown(KeyCode.Left_Control)) {
                    if (Input.buttonDown(KeyCode.W))
                        this.position.y += moveSpeed / 2.0f * this.zoom * Time.deltaTime();
                    else if (Input.buttonDown(KeyCode.S))
                        this.position.y -= moveSpeed / 2.0f * this.zoom * Time.deltaTime();

                    if (Input.buttonDown(KeyCode.A))
                        this.position.x -= moveSpeed / 2.0f * this.zoom * Time.deltaTime();
                    else if (Input.buttonDown(KeyCode.D))
                        this.position.x += moveSpeed / 2.0f * this.zoom * Time.deltaTime();
                }
            }

            if (EngineGuiLayer.isSceneWindowSelected() && EngineGuiLayer.getWantCaptureMouse()) {
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
            }

            if (this.dragDebounce <= 0.0f && !Input.buttonDown(KeyCode.Mouse_Button_Right))
                this.dragDebounce = this.startDragDebounce;
        } else {
            if (EngineGuiLayer.isSceneWindowSelected() && EngineGuiLayer.getWantCaptureMouse()) {
                if (Input.buttonDown(KeyCode.Mouse_Button_Right)) {
                    float yawChange = Input.getMouseDeltaPositionX() * rotateSensitivity;
                    this.rotation.y -= yawChange;
                    if (this.rotation.y > 360.0f)
                        this.rotation.y -= 360.0f;
                    if (this.rotation.y < 0.0f)
                        this.rotation.y += 360.0f;

                    float pitchChange = Input.getMouseDeltaPositionY() * rotateSensitivity;
                    this.rotation.x -= pitchChange;
                    if (this.rotation.x > 360.0f)
                        this.rotation.x -= 360.0f;
                    if (this.rotation.x < 0.0f)
                        this.rotation.x += 360.0f;
                }
            }

//            this.direction.set(0.0f);
//            if (EngineGuiLayer.isSceneWindowSelected()) {
//                if (Input.buttonDown(KeyCode.W))
//                    this.direction.z += moveSpeed * Time.deltaTime();
//                else if (Input.buttonDown(KeyCode.S))
//                    this.direction.z -= moveSpeed * Time.deltaTime();
//
//                if (Input.buttonDown(KeyCode.A))
//                    this.direction.x -= moveSpeed * Time.deltaTime();
//                else if (Input.buttonDown(KeyCode.D))
//                    this.direction.x += moveSpeed * Time.deltaTime();
//
//                if (Input.buttonDown(KeyCode.Space))
//                    this.direction.y += moveSpeed * Time.deltaTime();
//                else if (Input.buttonDown(KeyCode.Left_Shift) || Input.buttonDown(KeyCode.Right_Shift))
//                    this.direction.y -= moveSpeed * Time.deltaTime();
//
////                if (this.direction.z != 0.0f) { // TODO FIX CAMERA MOVEMENT
////                    this.position.z += (float) Math.cos(Math.toRadians(this.rotation.y)) * -1.0f * this.direction.z;
////                    this.position.x += (float) Math.sin(Math.toRadians(this.rotation.y)) * this.direction.z;
////                }
////                if (this.direction.x != 0.0f) {
////                    this.position.x -= (float) Math.sin(Math.toRadians(this.rotation.y - 90.0f)) * -1.0f * this.direction.x;
////                    this.position.z -= (float) Math.cos(Math.toRadians(this.rotation.y - 90.0f)) * this.direction.x;
////                }
//
//                this.position.y += this.direction.y;
//            }
        }

        if (EngineGuiLayer.getWantCaptureMouse()) {
            if (Input.getMouseScrollY() != 0.0f) {
                float addValue = (float) Math.pow(Math.abs(Input.getMouseScrollY()), 0.5f / this.zoom);
                this.zoom = Maths.lerp(this.zoom, addValue * -scrollSensitivity, (Time.deltaTime() * Math.signum(Input.getMouseScrollY()))); // Smooth zoom
                this.zoom = Math.max(this.zoom, 0.1f);
            }

            if (Input.buttonDown(KeyCode.F))
                this.reset = true;
        }

        if (this.reset) {
            Vector3f resetPoint = new Vector3f(0.0f);
            if (Outliner_Window.getActiveGameObject() != null)
                resetPoint.set(Outliner_Window.getActiveGameObject().transform.position);

            getPosition().lerp(resetPoint, this.resetLerpTime);
            getRotation().lerp(resetPoint, this.resetLerpTime);
            this.zoom += (1.0f - this.zoom) * this.resetLerpTime;
            this.resetLerpTime += resetLerpSpeed * Time.deltaTime();

            if (Math.abs(this.position.x) <= resetPoint.x + 0.02f && Math.abs(this.position.y) <= resetPoint.y + 0.02f && Math.abs(this.rotation.x) <= 0.02f && Math.abs(this.rotation.y) <= 0.02f && Math.abs(this.zoom) <= 0.02f) {
                this.position.set(resetPoint);
                this.rotation.set(resetPoint);
                this.zoom = 1.0f;
                this.reset = false;
                this.resetLerpTime = 0.0f;
            }
        }
        super.update();
    }
}
