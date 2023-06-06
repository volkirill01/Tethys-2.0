package engine.editor.gui;

import engine.logging.DebugLog;
import engine.profiling.Profiler;
import imgui.*;

public class GuiFont {

    private static ImFont defaultFont;
    private static ImFont boldFont;
    private static ImFont semiBoldFont;
    private static final float fontSize = 0.9f;

    public static void init(ImGuiIO io) {
        DebugLog.log("GuiFont:Init");

        Profiler.startTimer("Init ImGui Fonts");
        final ImFontAtlas fontAtlas = io.getFonts();
        final ImFontConfig fontConfig = new ImFontConfig(); // Natively allocated object, should be explicitly destroyed

        // Glyphs could be added per-font as well as per config used globally like here
        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());

        ImFontConfig defaultFontConfig = new ImFontConfig();
        defaultFontConfig.setPixelSnapH(true);
        defaultFontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());

        // Fonts merge example
        fontConfig.setPixelSnapH(true);
        defaultFont = fontAtlas.addFontFromFileTTF("Resources/fonts/openSans/OpenSans-Regular.ttf", 20.0f * fontSize, defaultFontConfig);
        fontConfig.setMergeMode(true);
        fontConfig.setGlyphMinAdvanceX(13.0f); // Use if you want to make the icon monospaced
        short[] icons_ranges = { (short) 0xE97A, (short) 0xF02C, 0 }; // Min(0xE97A), Max(0xF02C) icons range // TODO -+-+- Change min and max
        fontAtlas.addFontFromFileTTF("Resources/fonts/icofont_all.ttf", 15.5f * fontSize, fontConfig, icons_ranges);
        boldFont = fontAtlas.addFontFromFileTTF("Resources/fonts/openSans/OpenSans-Bold.ttf", 20.0f * fontSize, defaultFontConfig);
        semiBoldFont = fontAtlas.addFontFromFileTTF("Resources/fonts/openSans/OpenSans-SemiBold.ttf", 20.0f * fontSize, defaultFontConfig);

//        defaultSmallText = fontAtlas.addFontFromFileTTF("engineFiles/fonts/openSans/OpenSans-Regular.ttf", 15.0f * fontSize, defaultFontConfig);
//        semiBoldText = fontAtlas.addFontFromFileTTF("engineFiles/fonts/openSans/OpenSans-SemiBold.ttf", 20.0f * fontSize, defaultFontConfig);
//        italicText = fontAtlas.addFontFromFileTTF("engineFiles/fonts/openSans/OpenSans-Italic.ttf", 20.0f * fontSize, defaultFontConfig);
//
//        notificationFont = fontAtlas.addFontFromFileTTF("engineFiles/fonts/openSans/OpenSans-Regular.ttf", 20.0f * notificationFontSize * fontSize, defaultFontConfig);
//        fontConfig.setMergeMode(true);
//        fontConfig.setGlyphMinAdvanceX(13.0f); // Use if you want to make the icon monospaced
//        fontAtlas.addFontFromFileTTF("engineFiles/fonts/icofont_all.ttf", 13.0f * notificationFontSize * fontSize, fontConfig, icons_ranges);

        fontAtlas.build();
        fontConfig.destroy(); // After all fonts were added we don't need this config more
        Profiler.stopTimer("Init ImGui Fonts");
    }

    public static ImFont getDefaultFont() { return defaultFont; }

    public static ImFont getBoldFont() { return boldFont; }

    public static ImFont getSemiBoldFont() { return semiBoldFont; }

    public static void bindFont(ImFont font) { ImGui.pushFont(font); }

    public static void unbindFont() { ImGui.popFont(); }
}
