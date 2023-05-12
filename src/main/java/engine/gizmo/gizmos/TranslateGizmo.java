package engine.gizmo.gizmos;

import engine.eventListeners.Input;
import engine.gizmo.Gizmo;
import engine.renderer.renderer2D.sprite.Sprite;

public class TranslateGizmo extends Gizmo {

    public TranslateGizmo(Sprite arrowSprite) { super(arrowSprite); }

    public void update() {
        if (activeGameObject != null) {
            if (this.xAxisActive && !this.yAxisActive)
                activeGameObject.transform.position.x -= Input.getMouseWorldPositionX(); //.getMouseWorldDeltaXPosition();
            else if (this.yAxisActive && !this.xAxisActive)
                activeGameObject.transform.position.y -= Input.getMouseWorldPositionY(); //.getMouseWorldDeltaYPosition();
        }

        super.update();
    }
}
