package engine.gizmo;

import engine.assets.AssetPool;
import engine.entity.component.Component;
import engine.eventListeners.Input;
import engine.eventListeners.KeyCode;
import engine.gizmo.gizmos.ScaleGizmo;
import engine.gizmo.gizmos.TranslateGizmo;
import engine.renderer.renderer2D.sprite.SpriteSheet;

public class ed_GizmoSystem extends Component {

    private int usingGizmoIndex = 0;

    private final TranslateGizmo translateGizmo;  // Index 0
    private final ScaleGizmo scaleGizmo;          // Index 1

    public ed_GizmoSystem() {
        SpriteSheet gizmos = AssetPool.getSpriteSheet("editorFiles/gizmos.png");
        this.translateGizmo = new TranslateGizmo(gizmos.getSprite(1));
        this.scaleGizmo = new ScaleGizmo(gizmos.getSprite(2));

        this.translateGizmo.start();
        this.scaleGizmo.start();
    }

    @Override
    public void update() {
        if (Input.buttonDown(KeyCode.G))
            this.usingGizmoIndex = 0;
        else if (Input.buttonDown(KeyCode.T))
            this.usingGizmoIndex = 1;

        switch (this.usingGizmoIndex) {
            case 0 -> {
                this.translateGizmo.setUsing(true);
                this.scaleGizmo.setUsing(false);
            }
            case 1 -> {
                this.translateGizmo.setUsing(false);
                this.scaleGizmo.setUsing(true);
            }
            default -> {
                this.translateGizmo.setUsing(false);
                this.scaleGizmo.setUsing(false);
            }
        }
        this.translateGizmo.update();
        this.scaleGizmo.update();
    }
}
