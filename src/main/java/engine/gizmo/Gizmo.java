package engine.gizmo;

import TMP_MARIO_STUFF.Prefabs;
import engine.editor.windows.Outliner_Window;
import engine.entity.GameObject;
import engine.renderer.renderer2D.SpriteRenderer;
import engine.eventListeners.Input;
import engine.eventListeners.KeyCode;
import engine.renderer.renderer2D.sprite.Sprite;
import engine.scenes.SceneManager;
import engine.stuff.Settings;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Gizmo {

    private final GameObject xAxisObject;
    private final GameObject yAxisObject;
    private final SpriteRenderer xAxisRenderer;
    private final SpriteRenderer yAxisRenderer;

    private final Vector3f xAxisArrowOffset = new Vector3f(Settings.GRID_WIDTH * 1.5f, 0.0f, 0.0f);
    private final Vector3f yAxisArrowOffset = new Vector3f(0.0f, Settings.GRID_HEIGHT * 1.5f, 0.0f);
    protected boolean xAxisActive = false;
    protected boolean yAxisActive = false;

    private final float gizmoWidth = Settings.GRID_WIDTH;
    private final float gizmoHeight = Settings.GRID_WIDTH * 3.0f;

    private boolean using = false;

    protected GameObject activeGameObject = null;

    public Gizmo(Sprite arrowSprite) {
        this.xAxisObject = Prefabs.generateSpriteObject(arrowSprite, this.gizmoWidth, this.gizmoHeight);
        this.yAxisObject = Prefabs.generateSpriteObject(arrowSprite, this.gizmoWidth, this.gizmoHeight);
        this.xAxisRenderer = this.xAxisObject.getComponent(SpriteRenderer.class);
        this.yAxisRenderer = this.yAxisObject.getComponent(SpriteRenderer.class);

        SceneManager.getCurrentScene().addGameObjectToScene(this.xAxisObject);
        SceneManager.getCurrentScene().addGameObjectToScene(this.yAxisObject);
    }

    public void start() {
        this.xAxisObject.transform.rotation.set(0.0f, 0.0f, 90.0f);
        this.yAxisObject.transform.rotation.set(0.0f, 0.0f, 180.0f);
        this.xAxisObject.transform.setZIndex(1000);
        this.yAxisObject.transform.setZIndex(1000);
        this.xAxisObject.setSerialize(false);
        this.yAxisObject.setSerialize(false);
        this.xAxisObject.setClickable(false);
        this.yAxisObject.setClickable(false);
    }

    public void update() {
        if (!using)
            return;

        this.activeGameObject = Outliner_Window.getActiveGameObject();
        if (this.activeGameObject != null)
            this.setActive(true);
        else {
            this.setActive(false);
            return;
        }

        boolean xAxisHover = checkXHoverState();
        boolean yAxisHover = checkYHoverState();

        if (Input.isMouseDragging() && Input.buttonDown(KeyCode.Mouse_Button_Left)) {
            if (xAxisHover || this.xAxisActive) {
                this.xAxisActive = true;
                this.yAxisActive = false;
            } else if (yAxisHover || this.yAxisActive) {
                this.xAxisActive = false;
                this.yAxisActive = true;
            } else {
                this.xAxisActive = false;
                this.yAxisActive = false;
            }
        } else {
            this.xAxisActive = false;
            this.yAxisActive = false;
        }

        if (this.activeGameObject != null) {
            this.xAxisObject.transform.position.set(this.activeGameObject.transform.position).add(this.xAxisArrowOffset);
            this.yAxisObject.transform.position.set(this.activeGameObject.transform.position).add(this.yAxisArrowOffset);
        }
    }

    private void setActive(boolean active) {
        if (active) {
            this.xAxisRenderer.setColor(Settings.xAxisColor);
            this.yAxisRenderer.setColor(Settings.yAxisColor);
        } else {
            this.xAxisRenderer.setColor(0.0f);
            this.yAxisRenderer.setColor(0.0f);
        }
    }

    private boolean checkXHoverState() {
        Vector2f mousePos = Input.getMouseWorldPosition();
        if (mousePos.x <= xAxisObject.transform.position.x + (gizmoHeight / 2.0f) &&
                mousePos.x >= xAxisObject.transform.position.x - (gizmoWidth / 2.0f) &&
                mousePos.y >= xAxisObject.transform.position.y - (gizmoHeight / 2.0f) &&
                mousePos.y <= xAxisObject.transform.position.y + (gizmoWidth / 2.0f)) {

            xAxisRenderer.setColor(Settings.xAxisColor_Hover);
            return true;
        }
        xAxisRenderer.setColor(Settings.xAxisColor);
        return false;
    }

    private boolean checkYHoverState() {
        Vector2f mousePos = Input.getMouseWorldPosition();
        if (mousePos.x <= yAxisObject.transform.position.x + (gizmoWidth / 2.0f) &&
                mousePos.x >= yAxisObject.transform.position.x - (gizmoWidth / 2.0f) &&
                mousePos.y <= yAxisObject.transform.position.y + (gizmoHeight / 2.0f) &&
                mousePos.y >= yAxisObject.transform.position.y - (gizmoHeight / 2.0f)) {

            yAxisRenderer.setColor(Settings.yAxisColor_Hover);
            return true;
        }
        yAxisRenderer.setColor(Settings.yAxisColor);
        return false;
    }

    public void setUsing(boolean using) {
        this.using = using;
        this.setActive(this.using);
    }
}
