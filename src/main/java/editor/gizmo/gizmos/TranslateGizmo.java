package editor.gizmo.gizmos;

import editor.eventListeners.Input;
import editor.gizmo.Gizmo;
import editor.renderer.renderer2D.sprite.Sprite;

public class TranslateGizmo extends Gizmo {

    public TranslateGizmo(Sprite arrowSprite) { super(arrowSprite); }

    public void update() {
        if (activeGameObject != null) {
            if (this.xAxisActive && !this.yAxisActive)
                activeGameObject.transform.position.x -= Input.getMouseWorldDeltaXPosition();
            else if (this.yAxisActive && !this.xAxisActive)
                activeGameObject.transform.position.y -= Input.getMouseWorldDeltaYPosition();
        }

        super.update();
    }
}
