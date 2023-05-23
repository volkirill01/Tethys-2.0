package engine.editor.windows;

import engine.editor.gui.EditorImGuiWindow;
import engine.editor.gui.EngineGuiLayer;
import engine.entity.GameObject;
import engine.eventListeners.Input;
import engine.eventListeners.KeyCode;
import engine.eventListeners.MouseListener;
import engine.gizmo.ed_GizmoSystem;
import engine.observers.EventSystem;
import engine.observers.Observer;
import engine.observers.events.Event;
import engine.observers.events.EventType;
import engine.renderer.camera.ed_BaseCamera;
import engine.scenes.SceneManager;
import engine.stuff.Maths;
import engine.stuff.Settings;
import engine.stuff.Window;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SceneView_Window extends EditorImGuiWindow implements Observer {

    private static float leftX, rightX, topY, bottomY;
    private static final float[] transformArray = new float[4 * 4];

    public SceneView_Window() {
        super("\uF02C Scene", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);
        EventSystem.addObserver(this);
    }

    @Override
    public void drawWindow() {
        ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY());
        ImVec2 viewportSize = getLargestSizeForViewport();
        ImVec2 viewportPos = getCenteredPositionForViewport(viewportSize);
        ImGui.setCursorPos(viewportPos.x, viewportPos.y);

        ImVec2 topLeft = ImGui.getCursorScreenPos();
        leftX = topLeft.x;
        bottomY = topLeft.y;
        rightX = topLeft.x + viewportSize.x;
        topY = topLeft.y + viewportSize.y;

        int textureID = Window.getScreenFramebuffer().getColorTexture();
        ImVec2 start = new ImVec2();
        ImGui.getCursorPos(start);
        ImGui.image(textureID, viewportSize.x, viewportSize.y, 0, 1, 1, 0);

        // Gizmos
        GameObject activeObject = Outliner_Window.getActiveGameObject();
        if (activeObject != null) {
            ImGuizmo.setOrthographic(SceneManager.getCurrentScene().getEditorCamera().getProjectionType() == ed_BaseCamera.ProjectionType.Orthographic);
            ImGuizmo.setDrawList();

            // Set Gizmo draw area
            ImGui.setCursorPos(viewportPos.x, viewportPos.y);
            ImGuizmo.setRect(ImGui.getCursorScreenPosX(), ImGui.getCursorScreenPosY(), viewportSize.x, viewportSize.y);

            // TODO MOVE THIS CODE TO GIZMO SYSTEM CLASS
            // Camera
            Matrix4f cameraProjection = SceneManager.getCurrentScene().getEditorCamera().getProjectionMatrix();
            float[] projectionArray = new float[4 * 4];
            cameraProjection.get(projectionArray);

            Matrix4f cameraView = SceneManager.getCurrentScene().getEditorCamera().getViewMatrix();
            float[] viewArray = new float[4 * 4];
            cameraView.get(viewArray);

            // Active object Transform
            Matrix4f transform = Maths.createTransformationMatrix(activeObject.transform.position, activeObject.transform.rotation, activeObject.transform.scale);
            transform.get(transformArray);
//            float[] translation = { activeObject.transform.position.x, activeObject.transform.position.y, activeObject.transform.position.z };
//            float[] rotation = { activeObject.transform.rotation.x, activeObject.transform.rotation.y, activeObject.transform.rotation.z };
//            float[] scale = { activeObject.transform.scale.x, activeObject.transform.scale.y, activeObject.transform.scale.z };
//            ImGuizmo.recomposeMatrixFromComponents(transformArray, translation, rotation, scale);

            // Snapping
            boolean snapping = Input.buttonDown(KeyCode.Left_Control) || Input.buttonDown(KeyCode.Right_Control);
            float snapValue = 0.0f;
            float[] snapValuesArray;

            switch (ed_GizmoSystem.getActiveTool()) {
                case Translate -> {
                    snapValue = Settings.TRANSLATION_SNAPPING;
                    snapValuesArray = new float[]{ snapValue, snapValue, snapValue };

                    ImGuizmo.manipulate(viewArray, projectionArray, transformArray, Operation.TRANSLATE, Mode.LOCAL, snapping ? snapValuesArray : new float[3]);
                }
                case Rotate -> {
                    snapValue = Settings.ROTATE_SNAPPING;
                    snapValuesArray = new float[]{ snapValue, snapValue, snapValue };

                    ImGuizmo.manipulate(viewArray, projectionArray, transformArray, Operation.ROTATE, Mode.LOCAL, snapping ? snapValuesArray : new float[3]);
                }
                case Scale -> {
                    snapValue = Settings.SCALING_SNAPPING;
                    snapValuesArray = new float[]{ snapValue, snapValue, snapValue };

                    ImGuizmo.manipulate(viewArray, projectionArray, transformArray, Operation.SCALE, Mode.LOCAL, snapping ? snapValuesArray : new float[3]);
                }
                case Select -> { }
                default -> throw new IllegalStateException(String.format("Unknown GizmoTool type - '%s'", ed_GizmoSystem.getActiveTool().name()));
            }

            if (ed_GizmoSystem.isGizmoActive()) {
                float[] tmpTranslation = new float[3];
                float[] tmpRotation = new float[3];
                float[] tmpScale = new float[3];
                ImGuizmo.decomposeMatrixToComponents(transformArray, tmpTranslation, tmpRotation, tmpScale);

                activeObject.transform.position.set(tmpTranslation);
                Vector3f deltaRotation = new Vector3f(tmpRotation[0], tmpRotation[1], tmpRotation[2]).sub(activeObject.transform.rotation);
                activeObject.transform.rotation.add(deltaRotation);
                activeObject.transform.scale.set(tmpScale);
            }
        }

        MouseListener.setGameViewportPos(topLeft);
        MouseListener.setGameViewportSize(viewportSize);
    }

    private ImVec2 getLargestSizeForViewport() {
        ImVec2 viewportSize = ImGui.getContentRegionAvail();

        float aspectWidth = viewportSize.x;
        float aspectHeight = aspectWidth / Window.getTargetAspectRatio();

        if (aspectHeight > viewportSize.y) {
            // We must switch to pillar box mode (black bars on left and right sides of screen)
            aspectHeight = viewportSize.y;
            aspectWidth = aspectHeight * Window.getTargetAspectRatio();
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 viewportSize = ImGui.getContentRegionAvail();

        float viewportPositionX = (viewportSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportPositionY = (viewportSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewportPositionX + ImGui.getCursorPosX(), viewportPositionY + ImGui.getCursorPosY());
    }

    public boolean getWantCaptureMouse() {
        if (!this.isVisible() || !this.isHover())
            return false;

        return Input.getMousePositionX() >= leftX && Input.getMousePositionX() <= rightX &&
                Input.getMousePositionY() >= bottomY && Input.getMousePositionY() <= topY;
    }

    @Override
    public void onNotify(Event event) {
        if (event.type == EventType.Engine_StopPlay)
            EngineGuiLayer.selectWindow(this);
    }
}
