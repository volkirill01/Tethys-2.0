package editor;

import editor.renderer.Texture;
import editor.stuff.customVariables.Color;
import editor.stuff.utils.Time;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;
import imgui.flag.ImGuiMouseCursor;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class TestFieldsWindow {

    public static float[] getFloats = { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
    public static int[] getInts = { 0, 0, 0, 0 };
    public static String[] getStrings = { "", "", "" };
    public static boolean[] getBooleans = { false, false, false, false, false, false };
    public static Vector2f[] getVectors2f = { new Vector2f(0.0f), new Vector2f(0.0f), new Vector2f(0.0f) };
    public static Vector3f[] getVectors3f = { new Vector3f(0.0f), new Vector3f(0.0f), new Vector3f(0.0f) };
    public static Color[] getColors = { Color.WHITE.copy(), Color.WHITE.copy(), Color.WHITE.copy(), Color.WHITE.copy(), Color.WHITE.copy(), Color.WHITE.copy() };

    static Texture testTexture;
    static Texture testTexture2;
    static Texture testTexture3;

    private static float testFloat = 0.0f;
    private static float[] color = new float[3];
    private static float[] color2 = new float[3];

    private static boolean start = false;

    public static void imgui() {
        ImGui.begin(" Test Fields ");

        if (!start) {
            ImGui.setNextItemOpen(true);
            start = true;
        }
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

            ImGui.button("Hand");
            if (ImGui.isItemHovered())
                ImGui.setMouseCursor(ImGuiMouseCursor.Hand);
        }

//        if (ImGui.collapsingHeader("Boolean Types")) {
//            getBooleans[0] = EditorImGui.field_Boolean("True boolean(Switch)", getBooleans[0], EditorImGui.BooleanType.Switch);
//            getBooleans[1] = !EditorImGui.field_Boolean("False boolean(Switch)", !getBooleans[1], EditorImGui.BooleanType.Switch);
//            getBooleans[2] = EditorImGui.field_Boolean("True boolean(Checkbox)", getBooleans[2], EditorImGui.BooleanType.Checkbox);
//            getBooleans[3] = !EditorImGui.field_Boolean("False boolean(Checkbox)", !getBooleans[3], EditorImGui.BooleanType.Checkbox);
//            getBooleans[4] = EditorImGui.field_Boolean("True boolean(Bullet)", getBooleans[4], EditorImGui.BooleanType.Bullet);
//            getBooleans[5] = !EditorImGui.field_Boolean("False boolean(Bullet)", !getBooleans[5], EditorImGui.BooleanType.Bullet);
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

//        for (int i = 0; i < getInts.length; i++)
//            getInts[i] = EditorImGui.field_Int_WithButtons("Test Int (" + i + ")", getInts[i]);
////                        EditorImGui.field_Int("Tets int", 0);
//
//        for (int i = 0; i < getStrings.length; i++)
//            getStrings[i] = EditorImGui.field_Text("Test String (" + i + ")", getStrings[i], "Test");
//
//        for (int i = 0; i < getBooleans.length; i++)
//            getBooleans[i] = EditorImGui.field_Boolean("Test Boolean (" + i + ")", getBooleans[i]);
//
//        for (int i = 0; i < getVectors2f.length; i++)
//            getVectors2f[i] = EditorImGui.field_Vector2f("Test Vector2 (" + i + ")", getVectors2f[i]);
//
//        for (int i = 0; i < getVectors3f.length; i++)
//            getVectors3f[i] = EditorImGui.field_Vector3f("Test Vector3 (" + i + ")", getVectors3f[i]);
//
//        for (int i = 0; i < getColors.length; i++)
//            EditorImGui.field_Color_WithAlpha("Test Color (" + i + ")", getColors[i]);

        ImGui.end();
    }
}
