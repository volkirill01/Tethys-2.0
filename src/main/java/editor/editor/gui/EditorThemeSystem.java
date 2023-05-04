package editor.editor.gui;

import editor.stuff.customVariables.Color;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;

public class EditorThemeSystem {

    public static final Color activeColor = new Color(18.0f, 113.0f, 235.0f, 255.0f);

    private static final Color textColor                        = Color.WHITE.copy();
    private static final Color textDisabledColor                = Color.WHITE.copy();
    private static final Color windowBgColor                    = Color.WHITE.copy();
    private static final Color childBgColor                     = Color.WHITE.copy();
    private static final Color popupBgColor                     = Color.WHITE.copy();
    private static final Color borderColor                      = Color.WHITE.copy();
    private static final Color borderShadowColor                = Color.WHITE.copy();
    private static final Color frameBgColor                     = Color.WHITE.copy();
    private static final Color frameBgHoveredColor              = Color.WHITE.copy();
    private static final Color frameBgActiveColor               = Color.WHITE.copy();
    private static final Color titleBgColor                     = Color.WHITE.copy();
    private static final Color titleBgActiveColor               = Color.WHITE.copy();
    private static final Color titleBgCollapsedColor            = Color.WHITE.copy();
    private static final Color menuBarBgColor                   = Color.WHITE.copy();
    private static final Color scrollbarBgColor                 = Color.WHITE.copy();
    private static final Color scrollbarGrabColor               = Color.WHITE.copy();
    private static final Color scrollbarGrabHoveredColor        = Color.WHITE.copy();
    private static final Color scrollbarGrabActiveColor         = Color.WHITE.copy();
    private static final Color checkMarkColor                   = Color.WHITE.copy();
    private static final Color sliderGrabColor                  = Color.WHITE.copy();
    private static final Color sliderGrabActiveColor            = Color.WHITE.copy();
    private static final Color buttonColor                      = Color.WHITE.copy();
    private static final Color buttonHoveredColor               = Color.WHITE.copy();
    private static final Color buttonActiveColor                = Color.WHITE.copy();
    private static final Color headerColor                      = Color.WHITE.copy();
    private static final Color headerHoveredColor               = Color.WHITE.copy();
    private static final Color headerActiveColor                = Color.WHITE.copy();
    private static final Color separatorColor                   = Color.WHITE.copy();
    private static final Color separatorHoveredColor            = Color.WHITE.copy();
    private static final Color separatorActiveColor             = Color.WHITE.copy();
    private static final Color resizeGripColor                  = Color.WHITE.copy();
    private static final Color resizeGripHoveredColor           = Color.WHITE.copy();
    private static final Color resizeGripActiveColor            = Color.WHITE.copy();
    private static final Color tabColor                         = Color.WHITE.copy();
    private static final Color tabHoveredColor                  = Color.WHITE.copy();
    private static final Color tabActiveColor                   = Color.WHITE.copy();
    private static final Color tabUnfocusedColor                = Color.WHITE.copy();
    private static final Color tabUnfocusedActiveColor          = Color.WHITE.copy();
    private static final Color dockingPreviewColor              = Color.WHITE.copy();
    private static final Color dockingEmptyBgColor              = Color.WHITE.copy();
    private static final Color plotLinesColor                   = Color.WHITE.copy();
    private static final Color plotLinesHoveredColor            = Color.WHITE.copy();
    private static final Color plotHistogramColor               = Color.WHITE.copy();
    private static final Color plotHistogramHoveredColor        = Color.WHITE.copy();
    private static final Color tableHeaderBgColor               = Color.WHITE.copy();
    private static final Color tableBorderStrongColor           = Color.WHITE.copy();
    private static final Color tableBorderLightColor            = Color.WHITE.copy();
    private static final Color tableRowBgColor                  = Color.WHITE.copy();
    private static final Color tableRowBgAltColor               = Color.WHITE.copy();
    private static final Color textSelectedBgColor              = Color.WHITE.copy();
    private static final Color dragDropTargetColor              = Color.WHITE.copy();
    private static final Color navHighlightColor                = Color.WHITE.copy();
    private static final Color navWindowingHighlightColor       = Color.WHITE.copy();
    private static final Color navWindowingDimBgColor           = Color.WHITE.copy();
    private static final Color modalWindowDimBgColor            = Color.WHITE.copy();

    private static float frameRounding = 0.0f;
    private static float popupRounding = 0.0f;
    private static float windowRounding = 0.0f;
    private static float windowBorderSize = 0.0f;
    private static float frameBorderSize = 0.0f;
    private static final ImVec2 windowPadding = new ImVec2(0.0f, 0.0f);
    private static final ImVec2 framePadding = new ImVec2(0.0f, 0.0f);
    private static final ImVec2 cellPadding = new ImVec2(0.0f, 0.0f);
    private static final ImVec2 itemSpacing = new ImVec2(0.0f, 0.0f);
    private static float scrollbarSize = 0.0f;
    private static float grabRounding = 0.0f;
    private static float tabRounding = 0.0f;
    private static final ImVec2 touchExtraPadding = new ImVec2(0.0f, 0.0f);
    private static int windowMenuButtonPosition = 0;
    private static float circleTessellationMaxError = 0.0f;
    private static float curveTessellationTol = 0.0f;
    private static float indentSpacing = 0.0f;

    public static void updateTheme() {
        ImGui.getStyle().setFrameRounding(frameRounding);
        ImGui.getStyle().setPopupRounding(popupRounding);
        ImGui.getStyle().setWindowRounding(windowRounding);

        ImGui.getStyle().setWindowBorderSize(windowBorderSize);
        ImGui.getStyle().setFrameBorderSize(frameBorderSize);

        ImGui.getStyle().setWindowPadding(windowPadding.x, windowPadding.y);
        ImGui.getStyle().setFramePadding(framePadding.x, framePadding.y);
        ImGui.getStyle().setCellPadding(cellPadding.x, cellPadding.y);

        ImGui.getStyle().setItemSpacing(itemSpacing.x, itemSpacing.y);

        ImGui.getStyle().setScrollbarSize(scrollbarSize);

        ImGui.getStyle().setGrabRounding(grabRounding);
        ImGui.getStyle().setTabRounding(tabRounding);
        ImGui.getStyle().setTouchExtraPadding(touchExtraPadding.x, touchExtraPadding.y);
        ImGui.getStyle().setWindowMenuButtonPosition(windowMenuButtonPosition);
        ImGui.getStyle().setCircleTessellationMaxError(circleTessellationMaxError);
        ImGui.getStyle().setCurveTessellationTol(curveTessellationTol);

        ImGui.getStyle().setIndentSpacing(indentSpacing);

        ImGui.getStyle().setColor(ImGuiCol.Text, textColor.r / 255.0f, textColor.g / 255.0f, textColor.b / 255.0f, textColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.TextDisabled, textDisabledColor.r / 255.0f, textDisabledColor.g / 255.0f, textDisabledColor.b / 255.0f, textDisabledColor.a / 255.0f);

        ImGui.getStyle().setColor(ImGuiCol.WindowBg, windowBgColor.r / 255.0f, windowBgColor.g / 255.0f, windowBgColor.b / 255.0f, windowBgColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.ChildBg, childBgColor.r / 255.0f, childBgColor.g / 255.0f, childBgColor.b / 255.0f, childBgColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.PopupBg, popupBgColor.r / 255.0f, popupBgColor.g / 255.0f, popupBgColor.b / 255.0f, popupBgColor.a / 255.0f);

        ImGui.getStyle().setColor(ImGuiCol.Border, borderColor.r / 255.0f, borderColor.g / 255.0f, borderColor.b / 255.0f, borderColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.BorderShadow, borderShadowColor.r / 255.0f, borderShadowColor.g / 255.0f, borderShadowColor.b / 255.0f, borderShadowColor.a / 255.0f);

        ImGui.getStyle().setColor(ImGuiCol.FrameBg, frameBgColor.r / 255.0f, frameBgColor.g / 255.0f, frameBgColor.b / 255.0f, frameBgColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.FrameBgHovered, frameBgHoveredColor.r / 255.0f, frameBgHoveredColor.g / 255.0f, frameBgHoveredColor.b / 255.0f, frameBgHoveredColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.FrameBgActive, frameBgActiveColor.r / 255.0f, frameBgActiveColor.g / 255.0f, frameBgActiveColor.b / 255.0f, frameBgActiveColor.a / 255.0f);

        ImGui.getStyle().setColor(ImGuiCol.TitleBg, titleBgColor.r / 255.0f, titleBgColor.g / 255.0f, titleBgColor.b / 255.0f, titleBgColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.TitleBgActive, titleBgActiveColor.r / 255.0f, titleBgActiveColor.g / 255.0f, titleBgActiveColor.b / 255.0f, titleBgActiveColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.TitleBgCollapsed, titleBgCollapsedColor.r / 255.0f, titleBgCollapsedColor.g / 255.0f, titleBgCollapsedColor.b / 255.0f, titleBgCollapsedColor.a / 255.0f);

        ImGui.getStyle().setColor(ImGuiCol.MenuBarBg, menuBarBgColor.r / 255.0f, menuBarBgColor.g / 255.0f, menuBarBgColor.b / 255.0f, menuBarBgColor.a / 255.0f);

        ImGui.getStyle().setColor(ImGuiCol.ScrollbarBg, scrollbarBgColor.r / 255.0f, scrollbarBgColor.g / 255.0f, scrollbarBgColor.b / 255.0f, scrollbarBgColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.ScrollbarGrab, scrollbarGrabColor.r / 255.0f, scrollbarGrabColor.g / 255.0f, scrollbarGrabColor.b / 255.0f, scrollbarGrabColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.ScrollbarGrabHovered, scrollbarGrabHoveredColor.r / 255.0f, scrollbarGrabHoveredColor.g / 255.0f, scrollbarGrabHoveredColor.b / 255.0f, scrollbarGrabHoveredColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.ScrollbarGrabActive, scrollbarGrabActiveColor.r / 255.0f, scrollbarGrabActiveColor.g / 255.0f, scrollbarGrabActiveColor.b / 255.0f, scrollbarGrabActiveColor.a / 255.0f);

        ImGui.getStyle().setColor(ImGuiCol.CheckMark, checkMarkColor.r / 255.0f, checkMarkColor.g / 255.0f, checkMarkColor.b / 255.0f, checkMarkColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.SliderGrab, sliderGrabColor.r / 255.0f, sliderGrabColor.g / 255.0f, sliderGrabColor.b / 255.0f, sliderGrabColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.SliderGrabActive, sliderGrabActiveColor.r / 255.0f, sliderGrabActiveColor.g / 255.0f, sliderGrabActiveColor.b / 255.0f, sliderGrabActiveColor.a / 255.0f);

        ImGui.getStyle().setColor(ImGuiCol.Button, buttonColor.r / 255.0f, buttonColor.g / 255.0f, buttonColor.b / 255.0f, buttonColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.ButtonHovered, buttonHoveredColor.r / 255.0f, buttonHoveredColor.g / 255.0f, buttonHoveredColor.b / 255.0f, buttonHoveredColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.ButtonActive, buttonActiveColor.r / 255.0f, buttonActiveColor.g / 255.0f, buttonActiveColor.b / 255.0f, buttonActiveColor.a / 255.0f);

        ImGui.getStyle().setColor(ImGuiCol.Header, headerColor.r / 255.0f, headerColor.g / 255.0f, headerColor.b / 255.0f, headerColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.HeaderHovered, headerHoveredColor.r / 255.0f, headerHoveredColor.g / 255.0f, headerHoveredColor.b / 255.0f, headerHoveredColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.HeaderActive, headerActiveColor.r / 255.0f, headerActiveColor.g / 255.0f, headerActiveColor.b / 255.0f, headerActiveColor.a / 255.0f);

        ImGui.getStyle().setColor(ImGuiCol.Separator, separatorColor.r / 255.0f, separatorColor.g / 255.0f, separatorColor.b / 255.0f, separatorColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.SeparatorHovered, separatorHoveredColor.r / 255.0f, separatorHoveredColor.g / 255.0f, separatorHoveredColor.b / 255.0f, separatorHoveredColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.SeparatorActive, separatorActiveColor.r / 255.0f, separatorActiveColor.g / 255.0f, separatorActiveColor.b / 255.0f, separatorActiveColor.a / 255.0f);

        ImGui.getStyle().setColor(ImGuiCol.ResizeGrip, resizeGripColor.r / 255.0f, resizeGripColor.g / 255.0f, resizeGripColor.b / 255.0f, resizeGripColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.ResizeGripHovered, resizeGripHoveredColor.r / 255.0f, resizeGripHoveredColor.g / 255.0f, resizeGripHoveredColor.b / 255.0f, resizeGripHoveredColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.ResizeGripActive, resizeGripActiveColor.r / 255.0f, resizeGripActiveColor.g / 255.0f, resizeGripActiveColor.b / 255.0f, resizeGripActiveColor.a / 255.0f);

        ImGui.getStyle().setColor(ImGuiCol.Tab, tabColor.r / 255.0f, tabColor.g / 255.0f, tabColor.b / 255.0f, tabColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.TabHovered, tabHoveredColor.r / 255.0f, tabHoveredColor.g / 255.0f, tabHoveredColor.b / 255.0f, tabHoveredColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.TabActive, tabActiveColor.r / 255.0f, tabActiveColor.g / 255.0f, tabActiveColor.b / 255.0f, tabActiveColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.TabUnfocused, tabUnfocusedColor.r / 255.0f, tabUnfocusedColor.g / 255.0f, tabUnfocusedColor.b / 255.0f, tabUnfocusedColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.TabUnfocusedActive, tabUnfocusedActiveColor.r / 255.0f, tabUnfocusedActiveColor.g / 255.0f, tabUnfocusedActiveColor.b / 255.0f, tabUnfocusedActiveColor.a / 255.0f);

        ImGui.getStyle().setColor(ImGuiCol.DockingPreview, dockingPreviewColor.r / 255.0f, dockingPreviewColor.g / 255.0f, dockingPreviewColor.b / 255.0f, dockingPreviewColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.DockingEmptyBg, dockingEmptyBgColor.r / 255.0f, dockingEmptyBgColor.g / 255.0f, dockingEmptyBgColor.b / 255.0f, dockingEmptyBgColor.a / 255.0f);

        ImGui.getStyle().setColor(ImGuiCol.PlotLines, plotLinesColor.r / 255.0f, plotLinesColor.g / 255.0f, plotLinesColor.b / 255.0f, plotLinesColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.PlotLinesHovered, plotLinesHoveredColor.r / 255.0f, plotLinesHoveredColor.g / 255.0f, plotLinesHoveredColor.b / 255.0f, plotLinesHoveredColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.PlotHistogram, plotHistogramColor.r / 255.0f, plotHistogramColor.g / 255.0f, plotHistogramColor.b / 255.0f, plotHistogramColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.PlotHistogramHovered, plotHistogramHoveredColor.r / 255.0f, plotHistogramHoveredColor.g / 255.0f, plotHistogramHoveredColor.b / 255.0f, plotHistogramHoveredColor.a / 255.0f);

        ImGui.getStyle().setColor(ImGuiCol.TableHeaderBg, tableHeaderBgColor.r / 255.0f, tableHeaderBgColor.g / 255.0f, tableHeaderBgColor.b / 255.0f, tableHeaderBgColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.TableBorderStrong, tableBorderStrongColor.r / 255.0f, tableBorderStrongColor.g / 255.0f, tableBorderStrongColor.b / 255.0f, tableBorderStrongColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.TableBorderLight, tableBorderLightColor.r / 255.0f, tableBorderLightColor.g / 255.0f, tableBorderLightColor.b / 255.0f, tableBorderLightColor.a / 255.0f);

        ImGui.getStyle().setColor(ImGuiCol.TableRowBg, tableRowBgColor.r / 255.0f, tableRowBgColor.g / 255.0f, tableRowBgColor.b / 255.0f, tableRowBgColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.TableRowBgAlt, tableRowBgAltColor.r / 255.0f, tableRowBgAltColor.g / 255.0f, tableRowBgAltColor.b / 255.0f, tableRowBgAltColor.a / 255.0f);

        ImGui.getStyle().setColor(ImGuiCol.TextSelectedBg, textSelectedBgColor.r / 255.0f, textSelectedBgColor.g / 255.0f, textSelectedBgColor.b / 255.0f, textSelectedBgColor.a / 255.0f);

        ImGui.getStyle().setColor(ImGuiCol.DragDropTarget, dragDropTargetColor.r / 255.0f, dragDropTargetColor.g / 255.0f, dragDropTargetColor.b / 255.0f, dragDropTargetColor.a / 255.0f);

        ImGui.getStyle().setColor(ImGuiCol.NavHighlight, navHighlightColor.r / 255.0f, navHighlightColor.g / 255.0f, navHighlightColor.b / 255.0f, navHighlightColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.NavWindowingHighlight, navWindowingHighlightColor.r / 255.0f, navWindowingHighlightColor.g / 255.0f, navWindowingHighlightColor.b / 255.0f, navWindowingHighlightColor.a / 255.0f);
        ImGui.getStyle().setColor(ImGuiCol.NavWindowingDimBg, navWindowingDimBgColor.r / 255.0f, navWindowingDimBgColor.g / 255.0f, navWindowingDimBgColor.b / 255.0f, navWindowingDimBgColor.a / 255.0f);

        ImGui.getStyle().setColor(ImGuiCol.ModalWindowDimBg, modalWindowDimBgColor.r / 255.0f, modalWindowDimBgColor.g / 255.0f, modalWindowDimBgColor.b / 255.0f, modalWindowDimBgColor.a / 255.0f);
    }

    public static void setDarkTheme() {
        frameRounding = 5.0f;
        popupRounding = 5.0f;
        windowRounding = 5.0f;

        windowBorderSize = 1.4f;
        frameBorderSize = 1.4f;

        windowPadding.set(8.0f, 8.0f);
        framePadding.set(5.0f, 5.0f);
        cellPadding.set(2.0f, 2.0f);

        itemSpacing.set(3.0f, 3.0f);

        scrollbarSize = 12.0f;

        grabRounding = 7.0f;
        tabRounding = 4.0f;
        touchExtraPadding.set(0.0f, 0.0f);
        windowMenuButtonPosition = 1;
        circleTessellationMaxError = 0.2f;
        curveTessellationTol = 1.2f;

        indentSpacing = 15.0f;

        activeColor.set(0.0f, 122.0f, 204.0f, 255.0f);

        textColor.set(255.0f, 255.0f, 255.0f, 255.0f);
        textDisabledColor.set(128.0f, 128.0f, 128.0f, 255.0f);

        windowBgColor.set(45.0f, 45.0f, 48.0f, 255.0f);
        childBgColor.set(52.0f, 52.0f, 56.0f, 255.0f);
        popupBgColor.set(52.0f, 52.0f, 56.0f, 255.0f);

        borderColor.set(255.0f, 255.0f, 255.0f, 22.0f);
        borderShadowColor.set(0.0f, 0.0f, 0.0f, 0.0f);

        frameBgColor.set(37.0f, 37.0f, 38.0f, 255.0f);
        frameBgHoveredColor.set(41.0f, 41.0f, 43.0f, 255.0f);
        frameBgActiveColor.set(44.0f, 44.0f, 46.0f, 255.0f);

        titleBgColor.set(0.0f, 0.0f, 0.0f, 255.0f);
        titleBgActiveColor.set(0.0f, 0.0f, 0.0f, 255.0f);
        titleBgCollapsedColor.set(0.0f, 0.0f, 0.0f, 255.0f);

        menuBarBgColor.set(0.0f, 0.0f, 0.0f, 255.0f);

        scrollbarBgColor.set(0.0f, 0.0f, 0.0f, 50.0f);
        scrollbarGrabColor.set(62.0f, 62.0f, 66.0f, 255.0f);
        scrollbarGrabHoveredColor.set(68.0f, 68.0f, 75.0f, 255.0f);
        scrollbarGrabActiveColor.set(74.0f, 74.0f, 80.0f, 255.0f);

        checkMarkColor.set(activeColor);
        sliderGrabColor.set(62.0f, 62.0f, 66.0f, 255.0f);
        sliderGrabActiveColor.set(74.0f, 74.0f, 80.0f, 255.0f);

        buttonColor.set(62.0f, 62.0f, 66.0f, 255.0f);
        buttonHoveredColor.set(68.0f, 68.0f, 75.0f, 255.0f);
        buttonActiveColor.set(74.0f, 74.0f, 80.0f, 255.0f);

        headerColor.set(62.0f, 62.0f, 66.0f, 255.0f);
        headerHoveredColor.set(68.0f, 68.0f, 75.0f, 255.0f);
        headerActiveColor.set(74.0f, 74.0f, 80.0f, 255.0f);

        separatorColor.set(50.0f, 50.0f, 50.0f, 255.0f);
        separatorHoveredColor.set(60.0f, 60.0f, 60.0f, 255.0f);
        separatorActiveColor.set(activeColor);

        resizeGripColor.set(51.0f, 51.0f, 51.0f, 150.0f);
        resizeGripHoveredColor.set(0.235f, 0.235f, 0.235f, 150.0f);
        resizeGripActiveColor.set(activeColor);

        tabColor.set(62.0f, 62.0f, 66.0f, 255.0f);
        tabHoveredColor.set(68.0f, 68.0f, 75.0f, 255.0f);
        tabActiveColor.set(74.0f, 74.0f, 80.0f, 255.0f);
        tabUnfocusedColor.set(62.0f, 62.0f, 66.0f, 255.0f);
        tabUnfocusedActiveColor.set(37.0f, 37.0f, 39.0f, 255.0f);

        dockingPreviewColor.set(activeColor.r, activeColor.g, activeColor.b, activeColor.a / 3.0f);
        dockingEmptyBgColor.set(0.0f, 0.0f, 0.0f, 255.0f);

        plotLinesColor.set(255.0f, 192.0f, 0.0f, 255.0f);
        plotLinesHoveredColor.set(255.0f, 255.0f, 255.0f, 255.0f);
        plotHistogramColor.set(255.0f, 124.0f, 0.0f, 255.0f);
        plotHistogramHoveredColor.set(250.0f, 172.0f, 97.0f, 255.0f);

        tableHeaderBgColor.set(62.0f, 62.0f, 66.0f, 255.0f);
        tableBorderStrongColor.set(255.0f, 255.0f, 255.0f, 44.0f);
        tableBorderLightColor.set(255.0f, 255.0f, 255.0f, 22.0f);

        tableRowBgColor.set(52.0f, 52.0f, 56.0f, 255.0f);
        tableRowBgAltColor.set(62.0f, 62.0f, 66.0f, 255.0f);

        textSelectedBgColor.set(18.0f, 113.0f, 235.0f, 100.0f);

        dragDropTargetColor.set(255.0f, 147.0f, 0.0f, 100.0f);

        navHighlightColor.set(activeColor.r, activeColor.g, activeColor.b, activeColor.a / 3.0f);
        navWindowingHighlightColor.set(activeColor.r, activeColor.g, activeColor.b, activeColor.a / 3.0f);
        navWindowingDimBgColor.set(activeColor.r, activeColor.g, activeColor.b, activeColor.a / 3.0f);

        modalWindowDimBgColor.set(0.0f, 0.0f, 0.0f, 150.0f);
        updateTheme();
        updateTheme();
    }


    public static void setLightTheme() {
        textColor.set(0.0f, 0.0f, 0.0f, 255.0f);
        textDisabledColor.set(37.0f, 37.0f, 37.0f, 255.0f);

        windowBgColor.set(220.0f, 220.0f, 220.0f, 255.0f);
        childBgColor.set(201.0f, 201.0f, 201.0f, 255.0f);
        popupBgColor.set(244.0f, 244.0f, 244.0f, 255.0f);

        borderColor.set(0.0f, 0.0f, 0.0f, 25.0f);
        borderShadowColor.set(0.0f, 0.0f, 0.0f, 0.0f);

        frameBgColor.set(199.0f, 199.0f, 199.0f, 255.0f);
        frameBgHoveredColor.set(206.0f, 206.0f, 206.0f, 255.0f);
        frameBgActiveColor.set(211.0f, 211.0f, 211.0f, 255.0f);

        titleBgColor.set(129.0f, 129.0f, 129.0f, 255.0f);
        titleBgActiveColor.set(129.0f, 129.0f, 129.0f, 255.0f);
        titleBgCollapsedColor.set(129.0f, 129.0f, 129.0f, 255.0f);

        menuBarBgColor.set(129.0f, 129.0f, 129.0f, 255.0f);

        scrollbarBgColor.set(0.0f, 0.0f, 0.0f, 20.0f);
        scrollbarGrabColor.set(145.0f, 145.0f, 145.0f, 255.0f);
        scrollbarGrabHoveredColor.set(156.0f, 156.0f, 156.0f, 255.0f);
        scrollbarGrabActiveColor.set(166.0f, 166.0f, 166.0f, 255.0f);

        checkMarkColor.set(activeColor);
        sliderGrabColor.set(232.0f, 232.0f, 232.0f, 255.0f);
        sliderGrabActiveColor.set(240.0f, 240.0f, 240.0f, 255.0f);

        buttonColor.set(168.0f, 168.0f, 168.0f, 255.0f);
        buttonHoveredColor.set(173.0f, 173.0f, 173.0f, 255.0f);
        buttonActiveColor.set(180.0f, 180.0f, 180.0f, 255.0f);

        headerColor.set(168.0f, 168.0f, 168.0f, 255.0f);
        headerHoveredColor.set(173.0f, 173.0f, 173.0f, 255.0f);
        headerActiveColor.set(180.0f, 180.0f, 180.0f, 255.0f);

        separatorColor.set(181.0f, 181.0f, 181.0f, 255.0f);
        separatorHoveredColor.set(199.0f, 199.0f, 199.0f, 255.0f);
        separatorActiveColor.set(activeColor);

        resizeGripColor.set(183.0f, 183.0f, 183.0f, 150.0f);
        resizeGripHoveredColor.set(183.0f, 183.0f, 183.0f, 195.0f);
        resizeGripActiveColor.set(activeColor);

        tabColor.set(168.0f, 168.0f, 168.0f, 255.0f);
        tabHoveredColor.set(173.0f, 173.0f, 173.0f, 255.0f);
        tabActiveColor.set(180.0f, 180.0f, 180.0f, 255.0f);
        tabUnfocusedColor.set(168.0f, 168.0f, 168.0f, 255.0f);
        tabUnfocusedActiveColor.set(173.0f, 173.0f, 173.0f, 255.0f);

        dockingPreviewColor.set(activeColor.r, activeColor.g, activeColor.b, activeColor.a / 3.0f);
        dockingEmptyBgColor.set(0.0f, 0.0f, 0.0f, 255.0f);

        plotLinesColor.set(255.0f, 192.0f, 0.0f, 255.0f);
        plotLinesHoveredColor.set(255.0f, 255.0f, 255.0f, 255.0f);
        plotHistogramColor.set(255.0f, 124.0f, 0.0f, 255.0f);
        plotHistogramHoveredColor.set(250.0f, 172.0f, 97.0f, 255.0f);

        tableHeaderBgColor.set(193.0f, 193.0f, 193.0f, 255.0f);
        tableBorderStrongColor.set(255.0f, 255.0f, 255.0f, 22.0f);
        tableBorderLightColor.set(255.0f, 255.0f, 255.0f, 22.0f);

        tableRowBgColor.set(163.0f, 163.0f, 163.0f, 255.0f);
        tableRowBgAltColor.set(175.0f, 175.0f, 175.0f, 255.0f);

        textSelectedBgColor.set(activeColor);

        dragDropTargetColor.set(255.0f, 147.0f, 0.0f, 100.0f);

        navHighlightColor.set(activeColor.r, activeColor.g, activeColor.b, activeColor.a / 3.0f);
        navWindowingHighlightColor.set(activeColor.r, activeColor.g, activeColor.b, activeColor.a / 3.0f);
        navWindowingDimBgColor.set(activeColor.r, activeColor.g, activeColor.b, activeColor.a / 3.0f);

        modalWindowDimBgColor.set(0.0f, 0.0f, 0.0f, 150.0f);
        updateTheme();
    }
}
