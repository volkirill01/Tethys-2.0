package engine.editor.gui;

import engine.stuff.Settings;
import engine.stuff.customVariables.Color;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.*;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class EditorGUI {

    private static final float DEFAULT_LABEL_WIDTH = 180.0f;
    public static final String DEFAULT_FLOAT_FORMAT = "%.3f";
    private static final String DEFAULT_INTEGER_FORMAT = "%d";
    private static final float DRAG_SPEED = 0.1f;

    private static void beginField(String label) {
        ImGui.pushID(label);

        ImGui.columns(2, "", false);
        ImGui.setColumnWidth(0, ImGui.calcTextSize(label).x + ImGui.getStyle().getFramePaddingX());
        ImGui.text(label);
        ImGui.nextColumn();
        ImGui.setCursorPosX(ImGui.getCursorStartPosX() + DEFAULT_LABEL_WIDTH);
    }

    private static void endField() {
        ImGui.nextColumn();
        ImGui.columns(1);
        ImGui.popID();
    }

    public static String textFieldNoLabel(String id, String text) { return textFieldNoLabel(id, text, "", ImGui.getContentRegionAvailX()); }
    public static String textFieldNoLabel(String id, String text, float width) { return textFieldNoLabel(id, text, "", width); }
    public static String textFieldNoLabel(String id, String text, String hint, float width) {
        ImGui.pushID("TextField_NoLabel_" + id);

        ImGui.pushItemWidth(width);
        ImString ImString = new ImString(text, 256);

        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingX() * 1.5f, ImGui.getStyle().getFramePaddingY());
        ImGui.inputTextWithHint("##TextField_NoLabel_" + id, hint, ImString);
        ImGui.popStyleVar();

        ImGui.popID();

        return ImString.get();
    }

    public static String field_String(String label, String field) { return field_String(label, field, ""); }
    public static String field_String(String label, String field, String hint) {
        beginField(label);

        ImGui.pushItemWidth(ImGui.getContentRegionAvailX());
        ImString ImString = new ImString(field, 256);

        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingX() * 1.5f, ImGui.getStyle().getFramePaddingY());
        ImGui.inputTextWithHint("##field_String_" + label, hint, ImString);
        ImGui.popStyleVar();
        ImGui.popItemWidth();

        endField();

        return ImString.get();
    }

    public static boolean field_Boolean(String label, boolean field) {
        beginField(label);

        if (ImGui.checkbox("##field_Boolean_" + label, field))
            field = !field;

        endField();

        return field;
    }

    public static float field_Float(String label, float field) { return field_Float(label, field, DEFAULT_FLOAT_FORMAT); }
    public static float field_Float(String label, float field, String format) {
        beginField(label);

        ImGui.pushItemWidth(ImGui.getContentRegionAvailX());
        float[] ImFloat = { field };
        if (ImGui.dragFloat("##field_Float_" + label, ImFloat, DRAG_SPEED, -Float.MAX_VALUE, Float.MAX_VALUE, format))
            field = ImFloat[0];
        ImGui.popItemWidth();

        endField();

        return field;
    }

    public static int field_Int(String label, int field) { return field_Int(label, field, DEFAULT_INTEGER_FORMAT); }
    public static int field_Int(String label, int field, String format) {
        beginField(label);

        ImGui.pushItemWidth(ImGui.getContentRegionAvailX());
        int[] ImInt = { field };
        if (ImGui.dragInt("##field_Int_" + label, ImInt, DRAG_SPEED, -Integer.MAX_VALUE, Integer.MAX_VALUE, format))
            field = ImInt[0];
        ImGui.popItemWidth();

        endField();

        return field;
    }

    private static float drawVectorField(float vectorField, float resetValue, ImBoolean isValueChanged, String label, int color, int hoverColor, float itemWidth, String format) {
        int currentColor = color;
        float frameHeight = ImGui.getFrameHeight();
        if (ImGui.isMouseHoveringRect(ImGui.getCursorScreenPosX(), ImGui.getCursorScreenPosY(), ImGui.getCursorScreenPosX() + frameHeight, ImGui.getCursorScreenPosY() + frameHeight))
            currentColor = hoverColor;

        ImGui.getWindowDrawList().addRectFilled(
                ImGui.getCursorScreenPosX(), ImGui.getCursorScreenPosY(),
                ImGui.getCursorScreenPosX() + frameHeight, ImGui.getCursorScreenPosY() + frameHeight,
                currentColor, ImGui.getStyle().getFrameRounding(), ImDrawFlags.RoundCornersLeft
        );
        ImGui.getWindowDrawList().addRect(
                ImGui.getCursorScreenPosX(), ImGui.getCursorScreenPosY(),
                ImGui.getCursorScreenPosX() + frameHeight, ImGui.getCursorScreenPosY() + frameHeight,
                ImGui.getColorU32(ImGuiCol.Border), ImGui.getStyle().getFrameRounding(),
                ImDrawFlags.RoundCornersLeft, ImGui.getStyle().getFrameBorderSize()
        );

        ImVec2 startCursorPos = ImGui.getCursorPos();
        if (ImGui.invisibleButton(label, frameHeight, frameHeight)) {
            vectorField = resetValue;
            isValueChanged.set(true);
        }
        ImGui.sameLine();
        ImVec2 endCursorPos = ImGui.getCursorPos();

        ImGui.setCursorPos(startCursorPos.x, startCursorPos.y);
        ImGui.alignTextToFramePadding();
        ImGui.setCursorPosX(ImGui.getCursorPosX() + ImGui.getStyle().getFramePaddingY() * 1.1f + 4.0f);
        ImGui.text(label);
        ImGui.setCursorPos(endCursorPos.x, endCursorPos.y);

        int currentFrameColor = ImGui.getColorU32(ImGuiCol.FrameBg);
        if (ImGui.isMouseHoveringRect(ImGui.getCursorScreenPosX(), ImGui.getCursorScreenPosY(), ImGui.getCursorScreenPosX() + itemWidth, ImGui.getCursorScreenPosY() + frameHeight)) {
            currentFrameColor = ImGui.getColorU32(ImGuiCol.FrameBgHovered);
            if (ImGui.isMouseDown(ImGuiMouseButton.Left))
                currentFrameColor = ImGui.getColorU32(ImGuiCol.FrameBgActive);
        }

        ImGui.getWindowDrawList().addRectFilled(
                ImGui.getCursorScreenPosX(), ImGui.getCursorScreenPosY(),
                ImGui.getCursorScreenPosX() + itemWidth, ImGui.getCursorScreenPosY() + frameHeight,
                currentFrameColor, ImGui.getStyle().getFrameRounding(), ImDrawFlags.RoundCornersRight
        );
        ImGui.getWindowDrawList().addRect(
                ImGui.getCursorScreenPosX(), ImGui.getCursorScreenPosY(),
                ImGui.getCursorScreenPosX() + itemWidth, ImGui.getCursorScreenPosY() + frameHeight,
                ImGui.getColorU32(ImGuiCol.Border), ImGui.getStyle().getFrameRounding(),
                ImDrawFlags.RoundCornersRight, ImGui.getStyle().getFrameBorderSize()
        );

        ImGui.pushStyleColor(ImGuiCol.Border, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.FrameBg, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.FrameBgHovered, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.FrameBgActive, 0.0f, 0.0f, 0.0f, 0.0f);
        float[] ImFloat = { vectorField };
        if (ImGui.dragFloat("##field_Vectorf_Axis(" + label + ")", ImFloat, DRAG_SPEED, -Float.MAX_VALUE, Float.MAX_VALUE, format)) {
            vectorField = ImFloat[0];
            isValueChanged.set(true);
        }
        ImGui.popStyleColor(4);
        return vectorField;
    }

    public static boolean field_Vector2f(String label, Vector2f field) { return field_Vector2f(label, field, new Vector2f(0.0f), DEFAULT_FLOAT_FORMAT); }
    public static boolean field_Vector2f(String label, Vector2f field, String format) { return field_Vector2f(label, field, new Vector2f(0.0f), format); }
    public static boolean field_Vector2f(String label, Vector2f field, Vector2f resetValue) { return field_Vector2f(label, field, resetValue, DEFAULT_FLOAT_FORMAT); }
    public static boolean field_Vector2f(String label, Vector2f field, Vector2f resetValue, String format) {
        beginField(label);

        ImBoolean isValueChanged = new ImBoolean(false);

        float widthEach = (ImGui.getContentRegionAvailX() - ImGui.getFrameHeight() * 2 - ImGui.getStyle().getItemInnerSpacingX()) / 2; // 2 buttons, 1 spacing between them
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0.0f, 0.0f);
        ImGui.pushItemWidth(widthEach);

        int xButtonColorU32 = ImGui.getColorU32(Settings.xAxisColor.r / 255.0f, Settings.xAxisColor.g / 255.0f, Settings.xAxisColor.b / 255.0f, Settings.xAxisColor.a / 255.0f);
        int xButtonHoverColorU32 = ImGui.getColorU32(Settings.xAxisColor_Hover.r / 255.0f, Settings.xAxisColor_Hover.g / 255.0f, Settings.xAxisColor_Hover.b / 255.0f, Settings.xAxisColor_Hover.a / 255.0f);
        field.x = drawVectorField(field.x, resetValue.x, isValueChanged, "X", xButtonColorU32, xButtonHoverColorU32, widthEach, format);

        ImGui.sameLine();
        ImGui.setCursorPosX(ImGui.getCursorPosX() + ImGui.getStyle().getItemInnerSpacingX());
        int yButtonColorU32 = ImGui.getColorU32(Settings.yAxisColor.r / 255.0f, Settings.yAxisColor.g / 255.0f, Settings.yAxisColor.b / 255.0f, Settings.yAxisColor.a / 255.0f);
        int yButtonHoverColorU32 = ImGui.getColorU32(Settings.yAxisColor_Hover.r / 255.0f, Settings.yAxisColor_Hover.g / 255.0f, Settings.yAxisColor_Hover.b / 255.0f, Settings.yAxisColor_Hover.a / 255.0f);
        field.y = drawVectorField(field.y, resetValue.y, isValueChanged, "Y", yButtonColorU32, yButtonHoverColorU32, widthEach, format);

        ImGui.popItemWidth();
        ImGui.popStyleVar();

        ImGui.setCursorPosY(ImGui.getCursorPosY() + ImGui.getStyle().getItemSpacingY());

        endField();

        return isValueChanged.get();
    }

    public static boolean field_Vector3f(String label, Vector3f field) { return field_Vector3f(label, field, new Vector3f(0.0f), DEFAULT_FLOAT_FORMAT); }
    public static boolean field_Vector3f(String label, Vector3f field, String format) { return field_Vector3f(label, field, new Vector3f(0.0f), format); }
    public static boolean field_Vector3f(String label, Vector3f field, Vector3f resetValue) { return field_Vector3f(label, field, resetValue, DEFAULT_FLOAT_FORMAT); }
    public static boolean field_Vector3f(String label, Vector3f field, Vector3f resetValue, String format) {
        beginField(label);

        ImBoolean isValueChanged = new ImBoolean(false);

        float widthEach = (ImGui.getContentRegionAvailX() - ImGui.getFrameHeight() * 3 - ImGui.getStyle().getItemInnerSpacingX() * 2) / 3; // 3 buttons, 2 spacing between them
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0.0f, 0.0f);
        ImGui.pushItemWidth(widthEach);

        int xButtonColorU32 = ImGui.getColorU32(Settings.xAxisColor.r / 255.0f, Settings.xAxisColor.g / 255.0f, Settings.xAxisColor.b / 255.0f, Settings.xAxisColor.a / 255.0f);
        int xButtonHoverColorU32 = ImGui.getColorU32(Settings.xAxisColor_Hover.r / 255.0f, Settings.xAxisColor_Hover.g / 255.0f, Settings.xAxisColor_Hover.b / 255.0f, Settings.xAxisColor_Hover.a / 255.0f);
        field.x = drawVectorField(field.x, resetValue.x, isValueChanged, "X", xButtonColorU32, xButtonHoverColorU32, widthEach, format);

        ImGui.sameLine();
        ImGui.setCursorPosX(ImGui.getCursorPosX() + ImGui.getStyle().getItemInnerSpacingX());
        int yButtonColorU32 = ImGui.getColorU32(Settings.yAxisColor.r / 255.0f, Settings.yAxisColor.g / 255.0f, Settings.yAxisColor.b / 255.0f, Settings.yAxisColor.a / 255.0f);
        int yButtonHoverColorU32 = ImGui.getColorU32(Settings.yAxisColor_Hover.r / 255.0f, Settings.yAxisColor_Hover.g / 255.0f, Settings.yAxisColor_Hover.b / 255.0f, Settings.yAxisColor_Hover.a / 255.0f);
        field.y = drawVectorField(field.y, resetValue.y, isValueChanged, "Y", yButtonColorU32, yButtonHoverColorU32, widthEach, format);

        ImGui.sameLine();
        ImGui.setCursorPosX(ImGui.getCursorPosX() + ImGui.getStyle().getItemInnerSpacingX());
        int zButtonColorU32 = ImGui.getColorU32(Settings.zAxisColor.r / 255.0f, Settings.zAxisColor.g / 255.0f, Settings.zAxisColor.b / 255.0f, Settings.zAxisColor.a / 255.0f);
        int zButtonHoverColorU32 = ImGui.getColorU32(Settings.zAxisColor_Hover.r / 255.0f, Settings.zAxisColor_Hover.g / 255.0f, Settings.zAxisColor_Hover.b / 255.0f, Settings.zAxisColor_Hover.a / 255.0f);
        field.z = drawVectorField(field.z, resetValue.z, isValueChanged, "Z", zButtonColorU32, zButtonHoverColorU32, widthEach, format);

        ImGui.popItemWidth();
        ImGui.popStyleVar();

        ImGui.setCursorPosY(ImGui.getCursorPosY() + ImGui.getStyle().getItemSpacingY());

        endField();

        return isValueChanged.get();
    }

    public static boolean field_Vector4f(String label, Vector4f field) { return field_Vector4f(label, field, new Vector4f(0.0f), DEFAULT_FLOAT_FORMAT); }
    public static boolean field_Vector4f(String label, Vector4f field, String format) { return field_Vector4f(label, field, new Vector4f(0.0f), format); }
    public static boolean field_Vector4f(String label, Vector4f field, Vector4f resetValue) { return field_Vector4f(label, field, resetValue, DEFAULT_FLOAT_FORMAT); }
    public static boolean field_Vector4f(String label, Vector4f field, Vector4f resetValue, String format) {
        beginField(label);

        ImBoolean isValueChanged = new ImBoolean(false);

        float widthEach = (ImGui.getContentRegionAvailX() - ImGui.getFrameHeight() * 4 - ImGui.getStyle().getItemInnerSpacingX() * 3) / 4; // 4 buttons, 3 spacing between them
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0.0f, 0.0f);
        ImGui.pushItemWidth(widthEach);

        int xButtonColorU32 = ImGui.getColorU32(Settings.xAxisColor.r / 255.0f, Settings.xAxisColor.g / 255.0f, Settings.xAxisColor.b / 255.0f, Settings.xAxisColor.a / 255.0f);
        int xButtonHoverColorU32 = ImGui.getColorU32(Settings.xAxisColor_Hover.r / 255.0f, Settings.xAxisColor_Hover.g / 255.0f, Settings.xAxisColor_Hover.b / 255.0f, Settings.xAxisColor_Hover.a / 255.0f);
        field.x = drawVectorField(field.x, resetValue.x, isValueChanged, "X", xButtonColorU32, xButtonHoverColorU32, widthEach, format);

        ImGui.sameLine();
        ImGui.setCursorPosX(ImGui.getCursorPosX() + ImGui.getStyle().getItemInnerSpacingX());
        int yButtonColorU32 = ImGui.getColorU32(Settings.yAxisColor.r / 255.0f, Settings.yAxisColor.g / 255.0f, Settings.yAxisColor.b / 255.0f, Settings.yAxisColor.a / 255.0f);
        int yButtonHoverColorU32 = ImGui.getColorU32(Settings.yAxisColor_Hover.r / 255.0f, Settings.yAxisColor_Hover.g / 255.0f, Settings.yAxisColor_Hover.b / 255.0f, Settings.yAxisColor_Hover.a / 255.0f);
        field.y = drawVectorField(field.y, resetValue.y, isValueChanged, "Y", yButtonColorU32, yButtonHoverColorU32, widthEach, format);

        ImGui.sameLine();
        ImGui.setCursorPosX(ImGui.getCursorPosX() + ImGui.getStyle().getItemInnerSpacingX());
        int zButtonColorU32 = ImGui.getColorU32(Settings.zAxisColor.r / 255.0f, Settings.zAxisColor.g / 255.0f, Settings.zAxisColor.b / 255.0f, Settings.zAxisColor.a / 255.0f);
        int zButtonHoverColorU32 = ImGui.getColorU32(Settings.zAxisColor_Hover.r / 255.0f, Settings.zAxisColor_Hover.g / 255.0f, Settings.zAxisColor_Hover.b / 255.0f, Settings.zAxisColor_Hover.a / 255.0f);
        field.z = drawVectorField(field.z, resetValue.z, isValueChanged, "Z", zButtonColorU32, zButtonHoverColorU32, widthEach, format);

        ImGui.sameLine();
        ImGui.setCursorPosX(ImGui.getCursorPosX() + ImGui.getStyle().getItemInnerSpacingX());
        int wButtonColorU32 = ImGui.getColorU32(Settings.wAxisColor.r / 255.0f, Settings.wAxisColor.g / 255.0f, Settings.wAxisColor.b / 255.0f, Settings.wAxisColor.a / 255.0f);
        int wButtonHoverColorU32 = ImGui.getColorU32(Settings.wAxisColor_Hover.r / 255.0f, Settings.wAxisColor_Hover.g / 255.0f, Settings.wAxisColor_Hover.b / 255.0f, Settings.wAxisColor_Hover.a / 255.0f);
        field.w = drawVectorField(field.w, resetValue.w, isValueChanged, "W", wButtonColorU32, wButtonHoverColorU32, widthEach, format);

        ImGui.popItemWidth();
        ImGui.popStyleVar();

        ImGui.setCursorPosY(ImGui.getCursorPosY() + ImGui.getStyle().getItemSpacingY());

        endField();

        return isValueChanged.get();
    }

    public static boolean field_Color(String label, Color field) {
        beginField(label);

        boolean isValueChanged = false;

        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        float[] ImColor = { field.r / 255.0f, field.g / 255.0f, field.b / 255.0f, field.a / 255.0f };
        if (ImGui.colorEdit4("##field_Color_" + label, ImColor, ImGuiColorEditFlags.AlphaBar | ImGuiColorEditFlags.AlphaPreviewHalf)) {
            field.set(ImColor[0] * 255.0f, ImColor[1] * 255.0f, ImColor[2] * 255.0f, ImColor[3] * 255.0f);
            isValueChanged = true;
        }

        endField();
        return isValueChanged;
    }

    public static Enum<?> field_Enum(String label, Enum<?> field) {
        beginField(label);

        int currentFrameColor = ImGui.getColorU32(ImGuiCol.Button);
        if (ImGui.isMouseHoveringRect(ImGui.getCursorScreenPosX(), ImGui.getCursorScreenPosY(), ImGui.getCursorScreenPosX() + ImGui.getContentRegionAvailX(), ImGui.getCursorScreenPosY() + ImGui.getFrameHeight())) {
            currentFrameColor = ImGui.getColorU32(ImGuiCol.ButtonHovered);
            if (ImGui.isMouseDown(ImGuiMouseButton.Left))
                currentFrameColor = ImGui.getColorU32(ImGuiCol.ButtonActive);
        }

        ImGui.getWindowDrawList().addRectFilled(
                ImGui.getCursorScreenPosX(),
                ImGui.getCursorScreenPosY(),
                ImGui.getCursorScreenPosX() + ImGui.getContentRegionAvailX(),
                ImGui.getCursorScreenPosY() + ImGui.getFrameHeight(),
                currentFrameColor,
                ImGui.getStyle().getFrameRounding()
        );

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, ImGui.getStyle().getItemSpacingX() * 2, ImGui.getStyle().getItemSpacingY());
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingX() * 2, ImGui.getStyle().getFramePaddingY());
        ImGui.pushStyleColor(ImGuiCol.FrameBg, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.FrameBgHovered, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.FrameBgActive, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.0f, 0.0f, 0.0f, 0.0f);

        ImGui.pushItemWidth(ImGui.getContentRegionAvailX());
        String[] enumValues = getEnumValues(field);
        String enumType = field.name();
        ImInt index = new ImInt(indexOf(enumType, enumValues));
        if (ImGui.combo("##field_Enum_" + label, index, enumValues, enumValues.length))
            field = field.getClass().getEnumConstants()[index.get()];
        ImGui.popItemWidth();
        ImGui.popStyleColor(6);
        ImGui.popStyleVar(2);

        endField();
        return field;
    }
    private static <T extends Enum<T>> String[] getEnumValues(Enum<?> enumType) {
        String[] enumValues = new String[enumType.getClass().getEnumConstants().length];
        int i = 0;
        for (Enum<?> enumIntegerValue : enumType.getClass().getEnumConstants()) {
            enumValues[i] = enumIntegerValue.name();
            i++;
        }
        return enumValues;
    }
    private static int indexOf(String str, String[] array) {
        for (int i = 0; i < array.length; i++)
            if (str.equals(array[i]))
                return i;
        return -1;
    }

    private static final List<String> isCollapsingHeaderBegin = new ArrayList<>();
    public static boolean beginCollapsingHeader(String label) { return beginCollapsingHeader(label, ImGuiTreeNodeFlags.None); }
    public static boolean beginCollapsingHeader(String label, int flags) { // TODO FIX INDENT(LEFT) SPACING OF COLLAPSING HEADER
        if (isCollapsingHeaderBegin.contains(label))
            throw new RuntimeException("Mismatched beginCollapsingHeader vs endCollapsingHeader calls: did you forget to call endCollapsingHeader?");

        boolean isOpen = ImGui.collapsingHeader(label, flags);
        if (isOpen) {
            ImGui.indent();
            isCollapsingHeaderBegin.add(label);
        }
        return isOpen;
    }

    public static void endCollapsingHeader() {
        isCollapsingHeaderBegin.remove(isCollapsingHeaderBegin.size() - 1);
        ImGui.unindent();
    }
}
