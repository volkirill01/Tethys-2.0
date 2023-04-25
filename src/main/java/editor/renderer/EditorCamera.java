package editor.renderer;

import editor.eventListeners.Input;
import editor.eventListeners.KeyCode;
import editor.stuff.utils.Time;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class EditorCamera {

    private final float startDragDebounce = 0.1f;
    private float dragDebounce = this.startDragDebounce;

    private final Camera levelEditorCamera;
    private Vector2f clickOrigin;
    private boolean reset = false;

    private float lerpTime = 0.0f;
    private final float dragSensitivity = 30.0f;
    private final float scrollSensitivity = 0.1f;

    public EditorCamera(Camera levelEditorCamera) {
        this.levelEditorCamera = levelEditorCamera;
        this.clickOrigin = new Vector2f();
    }

    public void update() {
        // TODO REPLACE STATIC KEY, WITH KEY FROM USER SETTINGS
        if (Input.buttonDown(KeyCode.Mouse_Button_Right) && this.dragDebounce > 0) {
            this.clickOrigin = Input.getMouseWorldPosition();
//            this.clickOrigin = new Vector2f(Input.getMouseOrthographicXPosition(), Input.getMouseOrthographicYPosition());
            this.dragDebounce -= Time.deltaTime();
            return;
        } else if (Input.buttonDown(KeyCode.Mouse_Button_Right)) {
            Vector2f mousePos = Input.getMouseWorldPosition();
//            Vector2f mousePos = new Vector2f(Input.getMouseOrthographicXPosition(), Input.getMouseOrthographicYPosition());
            Vector2f delta = new Vector2f(mousePos).sub(this.clickOrigin);
            delta.mul(Time.deltaTime() * this.dragSensitivity);
            this.levelEditorCamera.getPosition().sub(delta.x, delta.y, 0.0f);
            this.clickOrigin.lerp(mousePos, Time.deltaTime());
        }

        if (this.dragDebounce <= 0.0f && !Input.buttonDown(KeyCode.Mouse_Button_Right))
            this.dragDebounce = this.startDragDebounce;

        if (Input.getMouseScrollY() != 0.0f) {
            float addValue = (float) Math.pow(Math.abs(Input.getMouseScrollY() * this.scrollSensitivity), 1.0f / this.levelEditorCamera.getZoom());
            addValue *= -Math.signum(Input.getMouseScrollY());
            this.levelEditorCamera.addZoom(addValue);
        }

        if (Input.buttonDown(KeyCode.F))
            this.reset = true;

        if (this.reset) {
            this.levelEditorCamera.getPosition().lerp(new Vector3f(0.0f), this.lerpTime);
            this.levelEditorCamera.setZoom(this.levelEditorCamera.getZoom() + ((1.0f - this.levelEditorCamera.getZoom()) * this.lerpTime));
            this.lerpTime += 0.1f * Time.deltaTime();

            if (Math.abs(this.levelEditorCamera.getPosition().x) <= 0.5f && Math.abs(this.levelEditorCamera.getPosition().y) <= 0.5f) {
                this.levelEditorCamera.getPosition().set(0.0f);
                this.levelEditorCamera.setZoom(1.0f);
                this.reset = false;
                this.lerpTime = 0.0f;
            }
        }
    }
}
