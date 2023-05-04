package editor.renderer.camera;

import editor.editor.gui.EditorGUI;
import editor.entity.component.Component;
import editor.renderer.stuff.Fbo;
import editor.stuff.Window;
import editor.stuff.customVariables.Color;
import imgui.ImGui;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera extends Component { // TODO MAKE BESA CAMERA CLASS AND INHERITANCE TO IT

    private transient final Fbo outputFbo = new Fbo(Window.getScreenWidth(), Window.getScreenHeight(), Fbo.DEPTH_TEXTURE);
    private transient final Matrix4f viewMatrix = new Matrix4f().identity();
    private transient final Matrix4f projectionMatrix = new Matrix4f().identity();

    private boolean isMain = false;

    private final Color backgroundColor = Color.WHITE.copy();

    private final Vector2f projectionSize = new Vector2f(6.0f, 3.0f);
    private float zoom = 1.0f;

    @Override
    public void start() { adjustProjection(); }

    @Override
    public void editorUpdate() { this.update(); }

    @Override
    public void update() {
        adjustProjection();

        this.viewMatrix.identity();

        this.viewMatrix.rotate((float) Math.toRadians(this.gameObject.transform.rotation.x), 1, 0, 0);
        this.viewMatrix.rotate((float) Math.toRadians(this.gameObject.transform.rotation.y), 0, 1, 0);
        this.viewMatrix.rotate((float) Math.toRadians(this.gameObject.transform.rotation.z), 0, 0, 1);
        Vector3f negativeCameraPosition = new Vector3f(-this.gameObject.transform.position.x, -this.gameObject.transform.position.y, -this.gameObject.transform.position.z);
        this.viewMatrix.translate(negativeCameraPosition.x, negativeCameraPosition.y, negativeCameraPosition.z);
    }

    @Override
    public void imgui() {
        this.isMain = EditorGUI.field_Boolean("IsMain", this.isMain);

        EditorGUI.field_Color("BackgroundColor", this.backgroundColor);
        EditorGUI.field_Vector2f("Size", this.projectionSize, new Vector2f(6.0f, 3.0f));

        this.zoom = EditorGUI.field_Float("Zoom", this.zoom);
        ImGui.image(this.outputFbo.getColorTexture(), ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailX() / Window.getTargetAspectRatio(), 0, 1, 1, 0);
    }

    private void adjustProjection() {
        this.projectionMatrix.identity();
        this.projectionMatrix.ortho(-(this.projectionSize.x * this.zoom / 2), this.projectionSize.x * this.zoom / 2, -(this.projectionSize.y * this.zoom / 2), this.projectionSize.y * this.zoom / 2, 0.0f, 100.0f);
    }

    public Matrix4f getViewMatrix() { return this.viewMatrix; }

    public Matrix4f getProjectionMatrix() { return this.projectionMatrix; }

    public boolean isMain() { return this.isMain; }

    public Color getBackgroundColor() { return this.backgroundColor; }

    public Fbo getFob() { return this.outputFbo; }
}
