package engine.editor.windows;

import engine.assets.Asset;
import engine.assets.AssetPool;
import engine.editor.gui.EditorGUI;
import engine.editor.gui.EditorGuiWindow;
import engine.editor.gui.EditorThemeSystem;
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
import engine.renderer.Texture2D;
import engine.renderer.camera.ed_BaseCamera;
import engine.scenes.SceneManager;
import engine.stuff.utils.Maths;
import engine.stuff.Settings;
import engine.stuff.Window;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class SceneView_Window extends EditorGuiWindow implements Observer {

    private static float leftX, rightX, topY, bottomY;
    private static final float[] transformArray = new float[4 * 4];

    private static boolean dragPreviousFrame = false;
    private static final Vector2f mouseStartPosition = new Vector2f();

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

        int textureID = Window.getScreenFramebuffer().getColorAttachmentID();
        ImVec2 start = new ImVec2();
        ImGui.getCursorPos(start);
        ImGui.image(textureID, viewportSize.x, viewportSize.y, 0, 1, 1, 0);

        String assetPath = EditorGUI.getDragDrop_Asset("Asset", Asset.AssetType.Scene);
        if (assetPath != null)
            SceneManager.changeScene(assetPath);

        // TODO MOVE THIS CODE TO GIZMO SYSTEM CLASS
        ImGuizmo.setOrthographic(SceneManager.getCurrentScene().getEditorCamera().getProjectionType() == ed_BaseCamera.ProjectionType.Orthographic);
        ImGuizmo.setDrawList();

        // Set Gizmo draw area
        ImGui.setCursorPos(viewportPos.x, viewportPos.y);
        ImGuizmo.setRect(ImGui.getCursorScreenPosX(), ImGui.getCursorScreenPosY(), viewportSize.x, viewportSize.y);

        // Camera
        Matrix4f cameraProjection = SceneManager.getCurrentScene().getEditorCamera().getProjectionMatrix();
        float[] projectionArray = new float[4 * 4];
        cameraProjection.get(projectionArray);

        Matrix4f cameraView = SceneManager.getCurrentScene().getEditorCamera().getViewMatrix();
        float[] viewArray = new float[4 * 4];
        cameraView.get(viewArray);

        // Gizmos
        GameObject activeObject = Outliner_Window.getActiveGameObject();
        if (activeObject != null) {
            // Active object Transform
            Matrix4f transform = Maths.createTransformationMatrix(activeObject.transform.position, activeObject.transform.rotation, activeObject.transform.scale);
            transform.get(transformArray);
//            float[] translation = { activeObject.transform.position.x, activeObject.transform.position.y, activeObject.transform.position.z };
//            float[] rotation = { activeObject.transform.rotation.x, activeObject.transform.rotation.y, activeObject.transform.rotation.z };
//            float[] scale = { activeObject.transform.scale.x, activeObject.transform.scale.y, activeObject.transform.scale.z };
//            ImGuizmo.recomposeMatrixFromComponents(transformArray, translation, rotation, scale);

            // Snapping
            boolean snapping = Input.buttonDown(KeyCode.Left_Control) || Input.buttonDown(KeyCode.Right_Control);
            float snapValue;
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
//                Matrix4f tmpMatrix = new Matrix4f();
//                tmpMatrix.set(transformArray);
//                Maths.decomposeTransformationMatrix(tmpMatrix, activeObject.transform.position, activeObject.transform.rotation, activeObject.transform.scale);

                switch (ed_GizmoSystem.getActiveTool()) {
                    case Translate -> activeObject.transform.position.set(tmpTranslation);
//                    case Rotate -> activeObject.transform.rotation.set(Math.toRadians(tmpRotation[0]), Math.toRadians(tmpRotation[1]), Math.toRadians(tmpRotation[2]));
                    case Rotate -> {
                        Vector3f deltaRotation = new Vector3f(Math.toRadians(tmpRotation[0]), Math.toRadians(tmpRotation[1]), Math.toRadians(tmpRotation[2])).sub(activeObject.transform.rotation); // TODO FIX ROTATION
                        activeObject.transform.rotation.add(deltaRotation);
                    }
                    case Scale -> activeObject.transform.scale.set(tmpScale);
                }
            }
        }

        Matrix4f identityMatrix = new Matrix4f().identity();
        float[] identityArray = new float[4 * 4];
        identityMatrix.get(identityArray);

        if (SceneManager.getCurrentScene().getEditorCamera().getProjectionType() == ed_BaseCamera.ProjectionType.Perspective)
            ImGuizmo.drawGrid(viewArray, projectionArray, identityArray, 5); // TODO WRITE CUSTOM 3D GRID FUNCTION

        MouseListener.setGameViewportPos(topLeft);
        MouseListener.setGameViewportSize(viewportSize);

        if (ImGui.isWindowHovered() && ImGui.isMouseClicked(ImGuiMouseButton.Right))
            dragPreviousFrame = false;

        if (ImGui.isWindowHovered())
            if (ImGui.isMouseReleased(ImGuiMouseButton.Right) && !dragPreviousFrame) {
                mouseStartPosition.set(Input.getMouseScreenPosition());
                ImGui.openPopup("Popup_SceneViewWindow_Context");
            }

        if (ImGui.isWindowHovered() && ImGui.isMouseDragging(ImGuiMouseButton.Right))
            dragPreviousFrame = true;

        if (ImGui.beginPopup("Popup_SceneViewWindow_Context")) {
            Vector2f worldPos = new Vector2f(0.0f);
            if (SceneManager.getCurrentScene().getEditorCamera().getProjectionType() == ed_BaseCamera.ProjectionType.Orthographic)
                worldPos.set(Input.screenToWorld(mouseStartPosition));

            SceneHierarchy_Window.drawWindowContextPopup(new Vector3f(worldPos.x, worldPos.y, 0.0f));
            ImGui.endPopup();
        }

        ImGui.setNextWindowPos(topLeft.x + ImGui.getStyle().getWindowPaddingX(), topLeft.y + ImGui.getStyle().getWindowPaddingY());
        drawToolsWindow();
        ImGui.sameLine();
        ImGui.setNextWindowPos(rightX - ImGui.getFrameHeight() - ImGui.getStyle().getWindowPaddingX() * 2, topLeft.y + ImGui.getStyle().getWindowPaddingY());
        drawCameraControls();
    }

    private void drawToolsWindow() {
        ImGui.pushStyleColor(ImGuiCol.WindowBg, 0.0f, 0.0f, 0.0f, 0.5f);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.Border, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, ImGui.getStyle().getWindowPaddingX() / 2, ImGui.getStyle().getWindowPaddingY() / 2);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 5.0f);
        ImGui.begin("Tools", ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoDecoration);

        if (drawToolButton(AssetPool.getTexture("Resources/icons/tools/selectTool.png"), ed_GizmoSystem.getActiveTool() == ed_GizmoSystem.GizmoTool.Select))
            ed_GizmoSystem.setActiveTool(ed_GizmoSystem.GizmoTool.Select);
        ImGui.sameLine();
        if (drawToolButton(AssetPool.getTexture("Resources/icons/tools/translateTool.png"), ed_GizmoSystem.getActiveTool() == ed_GizmoSystem.GizmoTool.Translate))
            ed_GizmoSystem.setActiveTool(ed_GizmoSystem.GizmoTool.Translate);
        ImGui.sameLine();
        if (drawToolButton(AssetPool.getTexture("Resources/icons/tools/rotateTool.png"), ed_GizmoSystem.getActiveTool() == ed_GizmoSystem.GizmoTool.Rotate))
            ed_GizmoSystem.setActiveTool(ed_GizmoSystem.GizmoTool.Rotate);
        ImGui.sameLine();
        if (drawToolButton(AssetPool.getTexture("Resources/icons/tools/scaleTool.png"), ed_GizmoSystem.getActiveTool() == ed_GizmoSystem.GizmoTool.Scale))
            ed_GizmoSystem.setActiveTool(ed_GizmoSystem.GizmoTool.Scale);

        ImGui.end();
        ImGui.popStyleVar(2);
        ImGui.popStyleColor(3);
    }

    private void drawCameraControls() {
        ImGui.pushStyleColor(ImGuiCol.WindowBg, 0.0f, 0.0f, 0.0f, 0.5f);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.Border, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, ImGui.getStyle().getWindowPaddingX() / 2, ImGui.getStyle().getWindowPaddingY() / 2);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 5.0f);
        ImGui.begin("CameraSettings", ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoDecoration);

        if (ImGui.button(SceneManager.getCurrentScene().getEditorCamera().getProjectionType() == ed_BaseCamera.ProjectionType.Perspective ? "3D" : "2D", ImGui.getFrameHeight(), ImGui.getFrameHeight())) {
            if (SceneManager.getCurrentScene().getEditorCamera().getProjectionType() == ed_BaseCamera.ProjectionType.Perspective)
                SceneManager.getCurrentScene().getEditorCamera().setProjectionType(ed_BaseCamera.ProjectionType.Orthographic);
            else
                SceneManager.getCurrentScene().getEditorCamera().setProjectionType(ed_BaseCamera.ProjectionType.Perspective);
        }

        ImGui.end();
        ImGui.popStyleVar(2);
        ImGui.popStyleColor(3);
//        if (ImGui.button(SceneManager.getCurrentScene().getEditorCamera().getProjectionType() == ed_BaseCamera.ProjectionType.Perspective ? "3D" : "2D", ImGui.getFrameHeight(), ImGui.getFrameHeight())) {
//            if (SceneManager.getCurrentScene().getEditorCamera().getProjectionType() == ed_BaseCamera.ProjectionType.Perspective)
//                SceneManager.getCurrentScene().getEditorCamera().setProjectionType(ed_BaseCamera.ProjectionType.Orthographic);
//            else
//                SceneManager.getCurrentScene().getEditorCamera().setProjectionType(ed_BaseCamera.ProjectionType.Perspective);
//        }
    }

    private boolean drawToolButton(Texture2D icon, boolean isSelected) {
        ImVec4 color = isSelected ? new ImVec4(EditorThemeSystem.selectionColor.r / 255.0f, EditorThemeSystem.selectionColor.g / 255.0f, EditorThemeSystem.selectionColor.b / 255.0f, 1.0f) : ImGui.getStyle().getColor(ImGuiCol.Text);
        return ImGui.imageButton(icon.getTextureID(), ImGui.getFrameHeight() / 1.4f, ImGui.getFrameHeight() / 1.4f, 0.0f, 1.0f, 1.0f, 0.0f, (int) ImGui.getStyle().getFramePadding().y, 1.0f, 1.0f, 1.0f, 0.0f, color.x, color.y, color.z, color.w);
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
