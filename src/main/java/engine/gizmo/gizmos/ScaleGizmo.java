package engine.gizmo.gizmos;

import engine.eventListeners.Input;
import engine.gizmo.Gizmo;
import engine.renderer.renderer2D.sprite.Sprite;

public class ScaleGizmo extends Gizmo {

    public ScaleGizmo(Sprite handleSprite) { super(handleSprite); }

    public void update() {
        if (activeGameObject != null) {
            if (this.xAxisActive && !this.yAxisActive)
                activeGameObject.transform.scale.x -= Input.getMouseWorldPositionX(); // .getMouseWorldDeltaXPosition();
            else if (this.yAxisActive && !this.xAxisActive)
                activeGameObject.transform.scale.y -= Input.getMouseWorldPositionY(); // .getMouseWorldDeltaYPosition();
        }

        super.update();
    }
}
