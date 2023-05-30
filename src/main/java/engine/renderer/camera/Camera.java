package engine.renderer.camera;

import engine.editor.gui.EditorGUI;
import engine.stuff.Window;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera extends ed_BaseCamera {

    private boolean isMain = false;

    public Camera() { super(new Vector3f(0.0f), new Vector3f(0.0f)); }

    @Override
    public void update() {
        adjustProjectionMatrix();

        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.viewMatrix.identity();
        this.viewMatrix.set(this.viewMatrix.lookAt(this.gameObject.transform.position, cameraFront.add(this.gameObject.transform.position), cameraUp));
        this.viewMatrix.rotate(this.gameObject.transform.rotation.x, 1, 0, 0);
        this.viewMatrix.rotate(this.gameObject.transform.rotation.y, 0, 1, 0);
        this.viewMatrix.rotate(this.gameObject.transform.rotation.z, 0, 0, 1);

        if (this.projectionType == ProjectionType.Perspective)
            this.viewMatrix.scale(1.0f / this.zoom); // Perspective camera zoom inverted to match the zoom of orthographic camera

        this.viewMatrix.invert(this.inverseViewMatrix);
    }

    @Override
    public void imgui() {
        this.isMain = EditorGUI.field_Boolean("Is Main", this.isMain);
        this.projectionType = (ProjectionType) EditorGUI.field_Enum("Projection Type", this.projectionType);
        EditorGUI.field_Color("Background Color", this.backgroundColor);

        EditorGUI.separator();
        if (this.projectionType == ProjectionType.Orthographic) {
            EditorGUI.field_Vector2f("Size", this.orthographicProjectionSize, new Vector2f(24.0f, 13.48f));
            EditorGUI.separator();
            this.orthographicNearPlane = EditorGUI.field_Float("Near Plane", this.orthographicNearPlane);
            this.orthographicFarPlane = EditorGUI.field_Float("Far Plane", this.orthographicFarPlane);
        } else {
            this.perspectiveFov = EditorGUI.field_Float("Fov", this.perspectiveFov);
            EditorGUI.separator();
            this.perspectiveNearPlane = EditorGUI.field_Float("Near Plane", this.perspectiveNearPlane);
            this.perspectiveFarPlane = EditorGUI.field_Float("Far Plane", this.perspectiveFarPlane);
        }

        EditorGUI.separator();
        this.zoom = EditorGUI.field_Float("Zoom", this.zoom);

        ImGui.image(this.outputFbo.getColorAttachmentID(), ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailX() / Window.getTargetAspectRatio(), 0, 1, 1, 0);
    }

    public boolean isMain() { return this.isMain; }
}
