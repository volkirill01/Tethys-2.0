package engine.gizmo;

import engine.entity.component.Component;
import engine.eventListeners.Input;
import engine.eventListeners.KeyCode;
import imgui.extension.imguizmo.ImGuizmo;

public class ed_GizmoSystem extends Component {

    public enum GizmoTool {
        Translate,
        Rotate,
        Scale,
        Select
    }

    private static GizmoTool activeTool = GizmoTool.Translate;
    @Override
    public void update() {
        if (Input.buttonDown(KeyCode.G))
            activeTool = GizmoTool.Translate;
        else if (Input.buttonDown(KeyCode.R))
            activeTool = GizmoTool.Rotate;
        else if (Input.buttonDown(KeyCode.T))
            activeTool = GizmoTool.Scale;
        else if (Input.buttonDown(KeyCode.V))
            activeTool = GizmoTool.Select;
    }

    public static GizmoTool getActiveTool() { return activeTool; }

    public static void setActiveTool(GizmoTool tool) { activeTool = tool; }

    public static boolean isGizmoActive() { return ImGuizmo.isUsing(); }
}
