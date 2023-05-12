package engine.renderer.camera;

import engine.editor.gui.EditorGUI;
import engine.stuff.Window;
import imgui.ImGui;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera extends ed_BaseCamera {

    private boolean isMain = false;

    public Camera() { super(new Vector3f(0.0f), new Vector3f(0.0f)); }

    @Override
    public void update() {
        super.update();

        this.viewMatrix.identity();

        this.viewMatrix.rotate((float) Math.toRadians(this.gameObject.transform.rotation.x), 1, 0, 0);
        this.viewMatrix.rotate((float) Math.toRadians(this.gameObject.transform.rotation.y), 0, 1, 0);
        this.viewMatrix.rotate((float) Math.toRadians(this.gameObject.transform.rotation.z), 0, 0, 1);
        this.viewMatrix.translate(-this.gameObject.transform.position.x, -this.gameObject.transform.position.y, -this.gameObject.transform.position.z);
        this.viewMatrix.invert(this.inverseViewMatrix);
    }

    @Override
    public void imgui() {
        this.isMain = EditorGUI.field_Boolean("IsMain", this.isMain);

        EditorGUI.field_Color("BackgroundColor", this.backgroundColor);
        EditorGUI.field_Vector2f("Size", this.orthographicProjectionSize, new Vector2f(6.0f, 3.0f));

        this.zoom = EditorGUI.field_Float("Zoom", this.zoom);
        ImGui.image(this.outputFbo.getColorTexture(), ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailX() / Window.getTargetAspectRatio(), 0, 1, 1, 0);
    }

    @Override
    public Matrix4f getViewMatrix() { return this.viewMatrix; }

    public boolean isMain() { return this.isMain; }
}
