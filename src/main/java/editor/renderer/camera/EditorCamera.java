package editor.renderer.camera;

import editor.eventListeners.Input;
import editor.eventListeners.KeyCode;
import editor.scenes.SceneManager;
import editor.stuff.utils.Time;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class EditorCamera {

    private final float startDragDebounce = 0.1f;
    private float dragDebounce = this.startDragDebounce;

    private final BaseCamera levelEditorCamera;
    private Vector2f clickOrigin;
    private boolean reset = false;
    private float lerpTime = 0.0f;

    // 2D
    private final float dragSensitivity = 30.0f;
    private final float scrollSensitivity = 0.1f;

    // 3D
    private final float sensitivity = 0.35f;
    private final float moveSpeed = 1.0f;
    private final Vector3f direction = new Vector3f(0.0f);

    public EditorCamera(BaseCamera levelEditorCamera) {
        this.levelEditorCamera = levelEditorCamera;
        this.clickOrigin = new Vector2f();
    }

    public void update() {
//        if (InputManager.isShortcutPressed("cameraMove(front)"))
//            direction.z += actualMoveSpeed * Time.deltaTime();
//        else if (InputManager.isShortcutPressed("cameraMove(back)"))
//            direction.z -= actualMoveSpeed * Time.deltaTime();
//
//        if (InputManager.isShortcutPressed("cameraMove(left)"))
//            direction.x += actualMoveSpeed * Time.deltaTime();
//        else if (InputManager.isShortcutPressed("cameraMove(right)"))
//            direction.x -= actualMoveSpeed * Time.deltaTime();
//
//        if (InputManager.isShortcutPressed("cameraMove(up)"))
//            direction.y += actualMoveSpeed * Time.deltaTime();
//        else if (InputManager.isShortcutPressed("cameraMove(down)"))
//            direction.y -= actualMoveSpeed * Time.deltaTime();
//        if (direction.z != 0 ) {
//            position.x -= (float) Math.sin(Math.toRadians(yaw)) * -1.0f * direction.z;
//            position.z -= (float) Math.cos(Math.toRadians(yaw)) * direction.z;
//        }
//        if (direction.x != 0) {
//            position.x -= (float) Math.sin(Math.toRadians(yaw - 90)) * -1.0f * direction.x;
//            position.z -= (float) Math.cos(Math.toRadians(yaw - 90)) * direction.x;
//        }
//
//        position.y += direction.y;

        // TODO REPLACE STATIC KEY, WITH KEY FROM USER SETTINGS
        if (SceneManager.getCurrentScene().getCamera().getCameraType() == CameraType.Orthographic) {
            if (Input.buttonDown(KeyCode.Mouse_Button_Right) && this.dragDebounce > 0) {
                this.clickOrigin = Input.getMouseWorldPosition();
                this.dragDebounce -= Time.deltaTime();
                return;
            } else if (Input.buttonDown(KeyCode.Mouse_Button_Right)) {
                Vector2f mousePos = Input.getMouseWorldPosition();
                Vector2f delta = new Vector2f(mousePos).sub(this.clickOrigin);
                delta.mul(Time.deltaTime() * this.dragSensitivity);
                this.levelEditorCamera.getPosition().sub(delta.x, delta.y, 0.0f);
                this.clickOrigin.lerp(mousePos, Time.deltaTime());
            }

            if (this.dragDebounce <= 0.0f && !Input.buttonDown(KeyCode.Mouse_Button_Right))
                this.dragDebounce = this.startDragDebounce;
        } else {
            if (Input.buttonDown(KeyCode.Mouse_Button_Right)) {
                float angleChange = Input.getMouseDeltaPositionX() * sensitivity;
                levelEditorCamera.getRotation().y -= angleChange;
                if (levelEditorCamera.getRotation().y > 180.0f)
                    levelEditorCamera.getRotation().y -= 180.0f;
                float pitchChange = Input.getMouseDeltaPositionY() * sensitivity;
                levelEditorCamera.getRotation().x -= pitchChange;
                if (levelEditorCamera.getRotation().x > 180.0f)
                    levelEditorCamera.getRotation().x -= 180.0f;
            }

            direction.x = 0;
            direction.y = 0;
            direction.z = 0;

            if (Input.buttonDown(KeyCode.W))
                direction.z += moveSpeed * Time.deltaTime();
            else if (Input.buttonDown(KeyCode.S))
                direction.z -= moveSpeed * Time.deltaTime();

            if (Input.buttonDown(KeyCode.A))
                direction.x += moveSpeed * Time.deltaTime();
            else if (Input.buttonDown(KeyCode.D))
                direction.x -= moveSpeed * Time.deltaTime();

            if (Input.buttonDown(KeyCode.Space))
                direction.y += moveSpeed * Time.deltaTime();
            else if (Input.buttonDown(KeyCode.Right_Shift) || Input.buttonDown(KeyCode.Left_Shift))
                direction.y -= moveSpeed * Time.deltaTime();

            if (direction.z != 0.0f) {
                levelEditorCamera.getPosition().x -= (float) Math.sin(Math.toRadians(levelEditorCamera.getRotation().y)) * -direction.z;
                levelEditorCamera.getPosition().z -= (float) Math.cos(Math.toRadians(levelEditorCamera.getRotation().y)) * direction.z;
            }
            if (direction.x != 0.0f) {
                levelEditorCamera.getPosition().x -= (float) Math.sin(Math.toRadians(levelEditorCamera.getRotation().y - 90.0f)) * -direction.x;
                levelEditorCamera.getPosition().z -= (float) Math.cos(Math.toRadians(levelEditorCamera.getRotation().y - 90.0f)) * direction.x;
            }

            levelEditorCamera.getPosition().y += direction.y;
        }

        if (Input.getMouseScrollY() != 0.0f) {
            float addValue = (float) Math.pow(Math.abs(Input.getMouseScrollY() * this.scrollSensitivity), 1.0f / this.levelEditorCamera.getZoom());
            addValue *= Math.signum(Input.getMouseScrollY());
            this.levelEditorCamera.addZoom(addValue);
        }

        if (Input.buttonDown(KeyCode.F))
            this.reset = true;

        if (this.reset) {
            this.levelEditorCamera.getPosition().lerp(new Vector3f(0.0f), this.lerpTime);
            this.levelEditorCamera.getRotation().lerp(new Vector3f(0.0f), this.lerpTime);
            this.levelEditorCamera.setZoom(this.levelEditorCamera.getZoom() + ((1.0f - this.levelEditorCamera.getZoom()) * this.lerpTime));
            this.lerpTime += 0.1f * Time.deltaTime();

            if (Math.abs(this.levelEditorCamera.getPosition().x) <= 0.02f && Math.abs(this.levelEditorCamera.getPosition().y) <= 0.02f &&
                Math.abs(this.levelEditorCamera.getRotation().x) <= 0.02f && Math.abs(this.levelEditorCamera.getRotation().y) <= 0.02f) {
                this.levelEditorCamera.getPosition().set(0.0f);
                this.levelEditorCamera.getRotation().set(0.0f);
                this.levelEditorCamera.setZoom(1.0f);
                this.reset = false;
                this.lerpTime = 0.0f;
            }
        }
    }
}
