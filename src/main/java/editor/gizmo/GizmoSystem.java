package editor.gizmo;

import editor.assets.AssetPool;
import editor.eventListeners.Input;
import editor.eventListeners.KeyCode;
import editor.gizmo.gizmos.ScaleGizmo;
import editor.gizmo.gizmos.TranslateGizmo;
import editor.renderer.renderer2D.sprite.SpriteSheet;

public class GizmoSystem {

    private int usingGizmoIndex = 0;

    private TranslateGizmo translateGizmo;  // Index 0
    private ScaleGizmo scaleGizmo;          // Index 1

    public void init() {
        SpriteSheet gizmos = AssetPool.getSpriteSheet("editorFiles/gizmos.png");
        this.translateGizmo = new TranslateGizmo(gizmos.getSprite(1));
        this.scaleGizmo = new ScaleGizmo(gizmos.getSprite(2));

        this.translateGizmo.start();
        this.scaleGizmo.start();
    }

    public void update() {
        if (Input.buttonDown(KeyCode.G))
            this.usingGizmoIndex = 0;
        else if (Input.buttonDown(KeyCode.S))
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
