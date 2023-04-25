package editor.editor.gui;

import editor.stuff.Settings;
import editor.stuff.customVariables.Color;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiColorEditFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImString;
import org.joml.Vector3f;

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

    public static boolean field_Vector3f(String label, Vector3f field) { return field_Vector3f(label, field, new Vector3f(0.0f)); }

    public static boolean field_Vector3f(String label, Vector3f field, Vector3f resetValue) {
        beginField(label);

        boolean isValueChanged = false;

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0.0f, 0.0f);

        float lineHeight = ImGui.getFrameHeight();
        ImVec2 buttonSize = new ImVec2(lineHeight, lineHeight);
        float widthEach = (ImGui.getContentRegionAvailX() - buttonSize.x * 3.0f) / 3.0f;
        ImGui.pushItemWidth(widthEach);

        int xButtonColorU32 = ImGui.getColorU32(Settings.xAxisColor.r / 255.0f, Settings.xAxisColor.g / 255.0f, Settings.xAxisColor.b / 255.0f, Settings.xAxisColor.a / 255.0f);
        int xButtonHoverColorU32 = ImGui.getColorU32(Settings.xAxisColor_Hover.r / 255.0f, Settings.xAxisColor_Hover.g / 255.0f, Settings.xAxisColor_Hover.b / 255.0f, Settings.xAxisColor_Hover.a / 255.0f);
        ImGui.pushStyleColor(ImGuiCol.Button, xButtonColorU32);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, xButtonHoverColorU32);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, xButtonHoverColorU32);
        if (ImGui.button("X", buttonSize.x, buttonSize.y)) {
            field.x = resetValue.x;
            isValueChanged = true;
        }
        ImGui.popStyleColor(3);
        ImGui.sameLine();
        float[] xImFloat = { field.x };
        if (ImGui.dragFloat("##field_Vector3f_X_" + label, xImFloat)) {
            field.x = xImFloat[0];
            isValueChanged = true;
        }

        ImGui.sameLine();
        int yButtonColorU32 = ImGui.getColorU32(Settings.yAxisColor.r / 255.0f, Settings.yAxisColor.g / 255.0f, Settings.yAxisColor.b / 255.0f, Settings.yAxisColor.a / 255.0f);
        int yButtonHoverColorU32 = ImGui.getColorU32(Settings.yAxisColor_Hover.r / 255.0f, Settings.yAxisColor_Hover.g / 255.0f, Settings.yAxisColor_Hover.b / 255.0f, Settings.yAxisColor_Hover.a / 255.0f);
        ImGui.pushStyleColor(ImGuiCol.Button, yButtonColorU32);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, yButtonHoverColorU32);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, yButtonHoverColorU32);
        if (ImGui.button("Y", buttonSize.x, buttonSize.y)) {
            field.y = resetValue.y;
            isValueChanged = true;
        }
        ImGui.popStyleColor(3);
        ImGui.sameLine();
        float[] yImFloat = { field.y };
        if (ImGui.dragFloat("##field_Vector3f_Y_" + label, yImFloat)) {
            field.y = yImFloat[0];
            isValueChanged = true;
        }

        ImGui.sameLine();
        int zButtonColorU32 = ImGui.getColorU32(Settings.zAxisColor.r / 255.0f, Settings.zAxisColor.g / 255.0f, Settings.zAxisColor.b / 255.0f, Settings.zAxisColor.a / 255.0f);
        int zButtonHoverColorU32 = ImGui.getColorU32(Settings.zAxisColor_Hover.r / 255.0f, Settings.zAxisColor_Hover.g / 255.0f, Settings.zAxisColor_Hover.b / 255.0f, Settings.zAxisColor_Hover.a / 255.0f);
        ImGui.pushStyleColor(ImGuiCol.Button, zButtonColorU32);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, zButtonHoverColorU32);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, zButtonHoverColorU32);
        if (ImGui.button("Z", buttonSize.x, buttonSize.y)) {
            field.z = resetValue.z;
            isValueChanged = true;
        }
        ImGui.popStyleColor(3);
        ImGui.sameLine();
        float[] zImFloat = { field.z };
        if (ImGui.dragFloat("##field_Vector3f_Z_" + label, zImFloat)) {
            field.z = zImFloat[0];
            isValueChanged = true;
        }

        ImGui.popItemWidth();
        ImGui.popStyleVar();

        ImGui.setCursorPosY(ImGui.getCursorPosY() + ImGui.getStyle().getItemSpacingY());

        endField();

        return isValueChanged;
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
