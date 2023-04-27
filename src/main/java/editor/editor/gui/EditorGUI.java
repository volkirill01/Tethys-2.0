package editor.editor.gui;

import editor.TestFieldsWindow;
import editor.stuff.Settings;
import editor.stuff.customVariables.Color;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiColorEditFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class EditorGUI {

    private static final float defaultLabelWidth = 180.0f;

    private static void beginField(String label) {
        ImGui.pushID(label);

        ImGui.columns(2, "", false);
        ImGui.setColumnWidth(0, defaultLabelWidth);
        ImGui.text(label);
        ImGui.nextColumn();
    }

    private static void endField() {
        ImGui.nextColumn();
        ImGui.columns(1);
        ImGui.popID();
    }

    public static String textFieldNoLabel(String id, String text) {
        ImGui.pushID("TextField_NoLabel_" + id);

        ImString result = new ImString(text, 256);

        if (ImGui.inputText("##TextField_NoLabel_" + id, result))
            text = result.get();

        ImGui.popID();

        return text;
    }

    public static String field_String(String label, String field) {
        beginField(label);

        ImGui.pushItemWidth(ImGui.getContentRegionAvailX());
        ImString ImString = new ImString(field, 256);
        if (ImGui.inputText("##field_String_" + label, ImString))
            field = ImString.get();
        ImGui.popItemWidth();

        endField();

        return field;
    }

    public static boolean field_Boolean(String label, boolean field) {
        beginField(label);

        if (ImGui.checkbox("##field_Boolean_" + label, field))
            field = !field;

        endField();

        return field;
    }

    public static float field_Float(String label, float field) {
        beginField(label);

        ImGui.pushItemWidth(ImGui.getContentRegionAvailX());
        float[] ImFloat = { field };
        if (ImGui.dragFloat("##field_Float_" + label, ImFloat))
            field = ImFloat[0];
        ImGui.popItemWidth();

        endField();

        return field;
    }

    public static int field_Int(String label, int field) {
        beginField(label);

        ImGui.pushItemWidth(ImGui.getContentRegionAvailX());
        int[] ImInt = { field };
        if (ImGui.dragInt("##field_Int_" + label, ImInt))
            field = ImInt[0];
        ImGui.popItemWidth();

        endField();

        return field;
    }

    private static float drawVectorField(float vectorField, float resetValue, ImBoolean isValueChanged, String label, int color, int hoverColor, ImVec2 buttonSize) {
        ImGui.pushStyleColor(ImGuiCol.Button, color);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, hoverColor);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, hoverColor);
        if (ImGui.button(label, buttonSize.x, buttonSize.y)) {
            vectorField = resetValue;
            isValueChanged.set(true);
        }
        ImGui.popStyleColor(3);
        ImGui.sameLine();
        float[] ImFloat = { vectorField };
        if (ImGui.dragFloat("##field_Vectorf_Axis(" + label + ")", ImFloat)) {
            vectorField = ImFloat[0];
            isValueChanged.set(true);
        }
        return vectorField;
    }

    public static boolean field_Vector2f(String label, Vector2f field) { return field_Vector2f(label, field, new Vector2f(0.0f)); }
    public static boolean field_Vector2f(String label, Vector2f field, Vector2f resetValue) {
        beginField(label);

        ImBoolean isValueChanged = new ImBoolean(false);

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0.0f, 0.0f);

        float lineHeight = ImGui.getFrameHeight();
        ImVec2 buttonSize = new ImVec2(lineHeight, lineHeight);
        float widthEach = (ImGui.getContentRegionAvailX() - buttonSize.x * 2.0f) / 2.0f;
        ImGui.pushItemWidth(widthEach);

        int xButtonColorU32 = ImGui.getColorU32(Settings.xAxisColor.r / 255.0f, Settings.xAxisColor.g / 255.0f, Settings.xAxisColor.b / 255.0f, Settings.xAxisColor.a / 255.0f);
        int xButtonHoverColorU32 = ImGui.getColorU32(Settings.xAxisColor_Hover.r / 255.0f, Settings.xAxisColor_Hover.g / 255.0f, Settings.xAxisColor_Hover.b / 255.0f, Settings.xAxisColor_Hover.a / 255.0f);
        field.x = drawVectorField(field.x, resetValue.x, isValueChanged, "X", xButtonColorU32, xButtonHoverColorU32, buttonSize);

        ImGui.sameLine();
        int yButtonColorU32 = ImGui.getColorU32(Settings.yAxisColor.r / 255.0f, Settings.yAxisColor.g / 255.0f, Settings.yAxisColor.b / 255.0f, Settings.yAxisColor.a / 255.0f);
        int yButtonHoverColorU32 = ImGui.getColorU32(Settings.yAxisColor_Hover.r / 255.0f, Settings.yAxisColor_Hover.g / 255.0f, Settings.yAxisColor_Hover.b / 255.0f, Settings.yAxisColor_Hover.a / 255.0f);
        field.y = drawVectorField(field.y, resetValue.y, isValueChanged, "Y", yButtonColorU32, yButtonHoverColorU32, buttonSize);

        ImGui.popItemWidth();
        ImGui.popStyleVar();

        ImGui.setCursorPosY(ImGui.getCursorPosY() + ImGui.getStyle().getItemSpacingY());

        endField();

        return isValueChanged.get();
    }

    public static boolean field_Vector3f(String label, Vector3f field) { return field_Vector3f(label, field, new Vector3f(0.0f)); }
    public static boolean field_Vector3f(String label, Vector3f field, Vector3f resetValue) {
        beginField(label);

        ImBoolean isValueChanged = new ImBoolean(false);

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0.0f, 0.0f);

        float lineHeight = ImGui.getFrameHeight();
        ImVec2 buttonSize = new ImVec2(lineHeight, lineHeight);
        float widthEach = (ImGui.getContentRegionAvailX() - buttonSize.x * 3.0f) / 3.0f;
        ImGui.pushItemWidth(widthEach);

        int xButtonColorU32 = ImGui.getColorU32(Settings.xAxisColor.r / 255.0f, Settings.xAxisColor.g / 255.0f, Settings.xAxisColor.b / 255.0f, Settings.xAxisColor.a / 255.0f);
        int xButtonHoverColorU32 = ImGui.getColorU32(Settings.xAxisColor_Hover.r / 255.0f, Settings.xAxisColor_Hover.g / 255.0f, Settings.xAxisColor_Hover.b / 255.0f, Settings.xAxisColor_Hover.a / 255.0f);
        field.x = drawVectorField(field.x, resetValue.x, isValueChanged, "X", xButtonColorU32, xButtonHoverColorU32, buttonSize);

        ImGui.sameLine();
        int yButtonColorU32 = ImGui.getColorU32(Settings.yAxisColor.r / 255.0f, Settings.yAxisColor.g / 255.0f, Settings.yAxisColor.b / 255.0f, Settings.yAxisColor.a / 255.0f);
        int yButtonHoverColorU32 = ImGui.getColorU32(Settings.yAxisColor_Hover.r / 255.0f, Settings.yAxisColor_Hover.g / 255.0f, Settings.yAxisColor_Hover.b / 255.0f, Settings.yAxisColor_Hover.a / 255.0f);
        field.y = drawVectorField(field.y, resetValue.y, isValueChanged, "Y", yButtonColorU32, yButtonHoverColorU32, buttonSize);

        ImGui.sameLine();
        int zButtonColorU32 = ImGui.getColorU32(Settings.zAxisColor.r / 255.0f, Settings.zAxisColor.g / 255.0f, Settings.zAxisColor.b / 255.0f, Settings.zAxisColor.a / 255.0f);
        int zButtonHoverColorU32 = ImGui.getColorU32(Settings.zAxisColor_Hover.r / 255.0f, Settings.zAxisColor_Hover.g / 255.0f, Settings.zAxisColor_Hover.b / 255.0f, Settings.zAxisColor_Hover.a / 255.0f);
        field.z = drawVectorField(field.z, resetValue.z, isValueChanged, "Z", zButtonColorU32, zButtonHoverColorU32, buttonSize);

        ImGui.popItemWidth();
        ImGui.popStyleVar();

        ImGui.setCursorPosY(ImGui.getCursorPosY() + ImGui.getStyle().getItemSpacingY());

        endField();

        return isValueChanged.get();
    }

    public static boolean field_Vector4f(String label, Vector4f field) { return field_Vector4f(label, field, new Vector4f(0.0f)); }
    public static boolean field_Vector4f(String label, Vector4f field, Vector4f resetValue) {
        beginField(label);

        ImBoolean isValueChanged = new ImBoolean(false);

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0.0f, 0.0f);

        float lineHeight = ImGui.getFrameHeight();
        ImVec2 buttonSize = new ImVec2(lineHeight, lineHeight);
        float widthEach = (ImGui.getContentRegionAvailX() - buttonSize.x * 4.0f) / 4.0f;
        ImGui.pushItemWidth(widthEach);

        int xButtonColorU32 = ImGui.getColorU32(Settings.xAxisColor.r / 255.0f, Settings.xAxisColor.g / 255.0f, Settings.xAxisColor.b / 255.0f, Settings.xAxisColor.a / 255.0f);
        int xButtonHoverColorU32 = ImGui.getColorU32(Settings.xAxisColor_Hover.r / 255.0f, Settings.xAxisColor_Hover.g / 255.0f, Settings.xAxisColor_Hover.b / 255.0f, Settings.xAxisColor_Hover.a / 255.0f);
        field.x = drawVectorField(field.x, resetValue.x, isValueChanged, "X", xButtonColorU32, xButtonHoverColorU32, buttonSize);

        ImGui.sameLine();
        int yButtonColorU32 = ImGui.getColorU32(Settings.yAxisColor.r / 255.0f, Settings.yAxisColor.g / 255.0f, Settings.yAxisColor.b / 255.0f, Settings.yAxisColor.a / 255.0f);
        int yButtonHoverColorU32 = ImGui.getColorU32(Settings.yAxisColor_Hover.r / 255.0f, Settings.yAxisColor_Hover.g / 255.0f, Settings.yAxisColor_Hover.b / 255.0f, Settings.yAxisColor_Hover.a / 255.0f);
        field.y = drawVectorField(field.y, resetValue.y, isValueChanged, "Y", yButtonColorU32, yButtonHoverColorU32, buttonSize);

        ImGui.sameLine();
        int zButtonColorU32 = ImGui.getColorU32(Settings.zAxisColor.r / 255.0f, Settings.zAxisColor.g / 255.0f, Settings.zAxisColor.b / 255.0f, Settings.zAxisColor.a / 255.0f);
        int zButtonHoverColorU32 = ImGui.getColorU32(Settings.zAxisColor_Hover.r / 255.0f, Settings.zAxisColor_Hover.g / 255.0f, Settings.zAxisColor_Hover.b / 255.0f, Settings.zAxisColor_Hover.a / 255.0f);
        field.z = drawVectorField(field.z, resetValue.z, isValueChanged, "Z", zButtonColorU32, zButtonHoverColorU32, buttonSize);

        ImGui.sameLine();
        int wButtonColorU32 = ImGui.getColorU32(Settings.wAxisColor.r / 255.0f, Settings.wAxisColor.g / 255.0f, Settings.wAxisColor.b / 255.0f, Settings.wAxisColor.a / 255.0f);
        int wButtonHoverColorU32 = ImGui.getColorU32(Settings.wAxisColor_Hover.r / 255.0f, Settings.wAxisColor_Hover.g / 255.0f, Settings.wAxisColor_Hover.b / 255.0f, Settings.wAxisColor_Hover.a / 255.0f);
        field.w = drawVectorField(field.w, resetValue.w, isValueChanged, "W", wButtonColorU32, wButtonHoverColorU32, buttonSize);

        ImGui.popItemWidth();
        ImGui.popStyleVar();

        ImGui.setCursorPosY(ImGui.getCursorPosY() + ImGui.getStyle().getItemSpacingY());

        endField();

        return isValueChanged.get();
    }

    public static boolean field_Color(String label, Color field) {
        beginField(label);

        boolean isValueChanged = false;

        float[] ImColor = { field.r / 255.0f, field.g / 255.0f, field.b / 255.0f, field.a / 255.0f };
        if (ImGui.colorEdit4("##field_Color_" + label, ImColor, ImGuiColorEditFlags.AlphaBar | ImGuiColorEditFlags.AlphaPreviewHalf)) {
            field.set(ImColor[0] * 255.0f, ImColor[1] * 255.0f, ImColor[2] * 255.0f, ImColor[3] * 255.0f);
            isValueChanged = true;
        }

        endField();
        return isValueChanged;
    }
}
