package editor;

import editor.editor.gui.EditorGUI;
import editor.stuff.customVariables.Color;
import editor.stuff.utils.Time;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiMouseCursor;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class TestFieldsWindow {

    public static final float[] getFloats = { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
    public static final int[] getInts = { 0, 0, 0, 0 };
    public static final String[] getStrings = { "", "", "" };
    public static final boolean[] getBooleans = { false, false, false, false, false, false };
    public static final Vector2f[] getVectors2f = { new Vector2f(0.0f), new Vector2f(0.0f), new Vector2f(0.0f) };
    public static final Vector3f[] getVectors3f = { new Vector3f(0.0f), new Vector3f(0.0f), new Vector3f(0.0f) };
    public static final Color[] getColors = { Color.WHITE.copy(), Color.WHITE.copy(), Color.WHITE.copy(), Color.WHITE.copy(), Color.WHITE.copy(), Color.WHITE.copy() };
    public static final Color[] themeColors;

//    static Texture testTexture;
//    static Texture testTexture2;
//    static Texture testTexture3;

    private static float testFloat = 0.0f;
    private static final float[] color = new float[3];
    private static final float[] color2 = new float[3];

//    private static boolean start = false;

    static {
        themeColors = new Color[55];
        themeColors[0] = Color.BLACK.copy();
        for (int i = 1; i < 55; i++) {
            themeColors[i] = Color.WHITE.copy();
        }
    }

    public static void imgui() {
        ImGui.begin(" Test Fields ");

//        if (!start) {
        ImGui.setNextItemOpen(true, ImGuiCond.Once);
//            start = true;
//        }
        if (ImGui.collapsingHeader("Test")) {
            if (testFloat < 1.0f)
                testFloat += Time.deltaTime() * 0.15f;
            else
                testFloat = 0.0f;

            ImGui.progressBar(testFloat);
            ImGui.progressBar(testFloat, ImGui.getContentRegionAvailX(), ImGui.getFrameHeight(), "Load");
            ImGui.progressBar(testFloat, 50.0f, 50.0f);

            ImGui.colorEdit3("Color", color);
            ImGui.colorPicker3("Color2", color2, ImGuiColorEditFlags.PickerHueWheel);

            ImGui.button("Cursor Hand");
            if (ImGui.isItemHovered())
                ImGui.setMouseCursor(ImGuiMouseCursor.Hand);
            ImGui.button("Cursor NotAllowed");
            if (ImGui.isItemHovered())
                ImGui.setMouseCursor(ImGuiMouseCursor.NotAllowed);
            ImGui.button("Cursor Arrow");
            if (ImGui.isItemHovered())
                ImGui.setMouseCursor(ImGuiMouseCursor.Arrow);
            ImGui.button("Cursor ResizeAll");
            if (ImGui.isItemHovered())
                ImGui.setMouseCursor(ImGuiMouseCursor.ResizeAll);
            ImGui.button("Cursor ResizeEW");
            if (ImGui.isItemHovered())
                ImGui.setMouseCursor(ImGuiMouseCursor.ResizeEW);
            ImGui.button("Cursor ResizeNESW");
            if (ImGui.isItemHovered())
                ImGui.setMouseCursor(ImGuiMouseCursor.ResizeNESW);
            ImGui.button("Cursor ResizeNS");
            if (ImGui.isItemHovered())
                ImGui.setMouseCursor(ImGuiMouseCursor.ResizeNS);
            ImGui.button("Cursor ResizeNWSE");
            if (ImGui.isItemHovered())
                ImGui.setMouseCursor(ImGuiMouseCursor.ResizeNWSE);
            ImGui.button("Cursor TextInput");
            if (ImGui.isItemHovered())
                ImGui.setMouseCursor(ImGuiMouseCursor.TextInput);
        }

        if (ImGui.collapsingHeader("Theme Set")) {
            for (int i = 0; i < themeColors.length; i++)
                EditorGUI.field_Color(ImGui.getStyleColorName(i), themeColors[i]);
        }

//        if (ImGui.collapsingHeader("Boolean Types")) {
//            getBooleans[0] = EditorGUI.field_Boolean("True boolean(Switch)", getBooleans[0], EditorGUI.BooleanType.Switch);
//            getBooleans[1] = !EditorGUI.field_Boolean("False boolean(Switch)", !getBooleans[1], EditorGUI.BooleanType.Switch);
//            getBooleans[2] = EditorGUI.field_Boolean("True boolean(Checkbox)", getBooleans[2], EditorGUI.BooleanType.Checkbox);
//            getBooleans[3] = !EditorGUI.field_Boolean("False boolean(Checkbox)", !getBooleans[3], EditorGUI.BooleanType.Checkbox);
//            getBooleans[4] = EditorGUI.field_Boolean("True boolean(Bullet)", getBooleans[4], EditorGUI.BooleanType.Bullet);
//            getBooleans[5] = !EditorGUI.field_Boolean("False boolean(Bullet)", !getBooleans[5], EditorGUI.BooleanType.Bullet);
//            ImGui.separator();
//        }

//        if (ImGui.collapsingHeader("Float Types")) {
//            getFloats[0] = EditorImGui.field_Float("Float (Drag)", getFloats[0], 0.01f, 0.0f, 1.0f, EditorImGui.FloatType.Drag);
//            getFloats[1] = EditorImGui.field_Float("Float (DragSlider)", getFloats[1], 0.01f, 0.0f, 1.0f, EditorImGui.FloatType.DragSlider);
//            getFloats[2] = EditorImGui.field_Float("Float (Slider)", getFloats[2], 0.01f, 0.0f, 1.0f, EditorImGui.FloatType.Slider);
//            ImGui.separator();
//        }
//
//        if (ImGui.collapsingHeader("Texture Fields")) {
//            EditorImGui.header("Texture Used Intensity, range(-1.0, 1.0)");
//            testTexture = (Texture) EditorImGui.field_Texture("Texture1", testTexture, new Vector2f(1.0f), new Vector2f(0.0f), true, 1, -1.0f, 1.0f).get(0);
//            EditorImGui.header("Texture Used Intensity, range(0.0, 2.0)");
//            testTexture2 = (Texture) EditorImGui.field_Texture("Texture2", testTexture2, new Vector2f(2.0f), new Vector2f(1.0f), true, 0.5f, 0.0f, 2.0f).get(0);
//            EditorImGui.header("Texture Not Use Intensity");
//            testTexture3 = EditorImGui.field_Texture("Texture3", testTexture3, new Vector2f(1.0f), new Vector2f(0.0f));
//            ImGui.separator();
//        }


        for (int i = 0; i < getFloats.length; i++) {
            float[] imFloats = { getFloats[i] };
            if (ImGui.dragFloat("Test Float (" + i + ")", imFloats))
                getFloats[i] = imFloats[0];
        }

        for (int i = 0; i < getInts.length; i++)
            getInts[i] = EditorGUI.field_Int("Test Int (" + i + ")", getInts[i]);

        for (int i = 0; i < getStrings.length; i++)
            getStrings[i] = EditorGUI.field_String("Test String (" + i + ")", getStrings[i]);

        for (int i = 0; i < getBooleans.length; i++)
            getBooleans[i] = EditorGUI.field_Boolean("Test Boolean (" + i + ")", getBooleans[i]);

        for (int i = 0; i < getVectors2f.length; i++)
            EditorGUI.field_Vector2f("Test Vector2 (" + i + ")", getVectors2f[i]);

        for (int i = 0; i < getVectors3f.length; i++)
            EditorGUI.field_Vector3f("Test Vector3 (" + i + ")", getVectors3f[i]);

        for (int i = 0; i < getColors.length; i++)
            EditorGUI.field_Color("Test Color (" + i + ")", getColors[i]);

        ImGui.end();
    }
}
