package editor.editor.gui;

import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;

public class EditorThemeSystem {

    public static final ImVec4 activeColor = new ImVec4(0.071f, 0.442f, 0.923f, 1.0f);

    public static void changeTheme(ImGuiStyle theme) {
        ImGui.getStyle().setColors(theme.getColors());

        ImGui.getStyle().setFrameRounding(theme.getFrameRounding());
        ImGui.getStyle().setPopupRounding(theme.getPopupRounding());
        ImGui.getStyle().setWindowRounding(theme.getWindowRounding());

        ImGui.getStyle().setWindowBorderSize(theme.getWindowBorderSize());
        ImGui.getStyle().setFrameBorderSize(theme.getFrameBorderSize());

        ImGui.getStyle().setWindowPadding(theme.getWindowPadding().x, theme.getWindowPadding().y);
        ImGui.getStyle().setFramePadding(theme.getFramePadding().x, theme.getFramePadding().y);
        ImGui.getStyle().setCellPadding(theme.getCellPadding().x, theme.getCellPadding().y);

        ImGui.getStyle().setItemSpacing(theme.getItemSpacing().x, theme.getItemSpacing().y);

        ImGui.getStyle().setScrollbarSize(theme.getScrollbarSize());

        ImGui.getStyle().setGrabRounding(theme.getGrabRounding());
        ImGui.getStyle().setTabRounding(theme.getTabRounding());
        ImGui.getStyle().setTouchExtraPadding(theme.getTouchExtraPadding().x, theme.getTouchExtraPadding().y);
        ImGui.getStyle().setWindowMenuButtonPosition(theme.getWindowMenuButtonPosition());
        ImGui.getStyle().setCircleTessellationMaxError(theme.getCircleTessellationMaxError());
        ImGui.getStyle().setCurveTessellationTol(theme.getCurveTessellationTol());

        ImGui.getStyle().setIndentSpacing(theme.getIndentSpacing());
    }

    public static ImGuiStyle darkTheme() {
        ImGuiStyle theme = new ImGuiStyle();
        theme.setFrameRounding(5.0f);
        theme.setPopupRounding(5.0f);
        theme.setWindowRounding(5.0f);

        theme.setWindowBorderSize(1.4f);
        theme.setFrameBorderSize(1.4f);

        theme.setWindowPadding(8.0f, 8.0f);
        theme.setFramePadding(5.0f, 5.0f);
        theme.setCellPadding(2.0f, 2.0f);

        theme.setItemSpacing(3.0f, 3.0f);

        theme.setScrollbarSize(12.0f);

        theme.setGrabRounding(7.0f);
        theme.setTabRounding(4.0f);
        theme.setTouchExtraPadding(0.0f, 0.0f);
        theme.setWindowMenuButtonPosition(1);
        theme.setCircleTessellationMaxError(0.2f);
        theme.setCurveTessellationTol(1.2f);

        theme.setIndentSpacing(0.0f);

        // For setting Theme
//        for (int i = 0; i < 55; i++) {
//            ImVec4 tmpColor = new ImVec4(TestFieldsWindow.getColors[i].r / 255.0f, TestFieldsWindow.getColors[i].g / 255.0f, TestFieldsWindow.getColors[i].b / 255.0f, TestFieldsWindow.getColors[i].a / 255.0f);
//            theme.setColor(i, tmpColor.x, tmpColor.y, tmpColor.z, tmpColor.w);
//        }

        theme.setColor(ImGuiCol.Text, 1.0f, 1.0f, 1.0f, 1.0f);
        theme.setColor(ImGuiCol.TextDisabled, 0.5f, 0.5f, 0.5f, 1.0f);

        theme.setColor(ImGuiCol.WindowBg, 0.137f, 0.137f, 0.137f, 1.0f);
        theme.setColor(ImGuiCol.ChildBg, 0.236f, 0.236f, 0.236f, 1.0f);
        theme.setColor(ImGuiCol.PopupBg, 0.172f, 0.172f, 0.172f, 1.0f);

        theme.setColor(ImGuiCol.Border, 1.0f, 1.0f, 1.0f, 0.086f);
        theme.setColor(ImGuiCol.BorderShadow, 0.0f, 0.0f, 0.0f, 0.0f);

        theme.setColor(ImGuiCol.FrameBg, 0.099f, 0.099f, 0.099f, 1.0f);
        theme.setColor(ImGuiCol.FrameBgHovered, 0.128f, 0.128f, 0.128f, 1.0f);
        theme.setColor(ImGuiCol.FrameBgActive, 0.144f, 0.144f, 0.144f, 1.0f);

        theme.setColor(ImGuiCol.TitleBg, 0.0f, 0.0f, 0.0f, 1.0f);
        theme.setColor(ImGuiCol.TitleBgActive, 0.0f, 0.0f, 0.0f, 1.0f);
        theme.setColor(ImGuiCol.TitleBgCollapsed, 0.0f, 0.0f, 0.0f, 1.0f);

        theme.setColor(ImGuiCol.MenuBarBg, 0.0f, 0.0f, 0.0f, 1.0f);

        theme.setColor(ImGuiCol.ScrollbarBg, 0.0f, 0.0f, 0.0f, 0.196f);
        theme.setColor(ImGuiCol.ScrollbarGrab, 0.199f, 0.199f, 0.199f, 1.0f);
        theme.setColor(ImGuiCol.ScrollbarGrabHovered, 0.213f, 0.213f, 0.213f, 1.0f);
        theme.setColor(ImGuiCol.ScrollbarGrabActive, 0.236f, 0.236f, 0.236f, 1.0f);

        theme.setColor(ImGuiCol.CheckMark, activeColor.x, activeColor.y, activeColor.z, activeColor.w);
        theme.setColor(ImGuiCol.SliderGrab, 0.199f, 0.199f, 0.199f, 1.0f);
        theme.setColor(ImGuiCol.SliderGrabActive, 0.236f, 0.236f, 0.236f, 1.0f);

        theme.setColor(ImGuiCol.Button, 0.236f, 0.236f, 0.236f, 1.0f);
        theme.setColor(ImGuiCol.ButtonHovered, 0.262f, 0.262f, 0.262f, 1.0f);
        theme.setColor(ImGuiCol.ButtonActive, 0.292f, 0.292f, 0.292f, 1.0f);

        theme.setColor(ImGuiCol.Header, 0.236f, 0.236f, 0.236f, 1.0f);
        theme.setColor(ImGuiCol.HeaderHovered, 0.262f, 0.262f, 0.262f, 1.0f);
        theme.setColor(ImGuiCol.HeaderActive, 0.292f, 0.292f, 0.292f, 1.0f);

        theme.setColor(ImGuiCol.Separator, 0.199f, 0.199f, 0.199f, 1.0f);
        theme.setColor(ImGuiCol.SeparatorHovered, 0.236f, 0.236f, 0.236f, 1.0f);
        theme.setColor(ImGuiCol.SeparatorActive, activeColor.x, activeColor.y, activeColor.z, activeColor.w);

        theme.setColor(ImGuiCol.ResizeGrip, 0.200f, 0.200f, 0.200f, 0.588f);
        theme.setColor(ImGuiCol.ResizeGripHovered, 0.235f, 0.235f, 0.235f, 0.588f);
        theme.setColor(ImGuiCol.ResizeGripActive, activeColor.x, activeColor.y, activeColor.z, activeColor.w);

        theme.setColor(ImGuiCol.Tab, 0.236f, 0.236f, 0.236f, 1.0f);
        theme.setColor(ImGuiCol.TabHovered, 0.262f, 0.262f, 0.262f, 1.0f);
        theme.setColor(ImGuiCol.TabActive, 0.292f, 0.292f, 0.292f, 1.0f);
        theme.setColor(ImGuiCol.TabUnfocused, 0.236f, 0.236f, 0.236f, 1.0f);
        theme.setColor(ImGuiCol.TabUnfocusedActive, 0.292f, 0.292f, 0.292f, 1.0f);

        theme.setColor(ImGuiCol.DockingPreview, activeColor.x, activeColor.y, activeColor.z, activeColor.w / 3.0f);
        theme.setColor(ImGuiCol.DockingEmptyBg, 0.0f, 0.0f, 0.0f, 1.0f);

        theme.setColor(ImGuiCol.PlotLines, 1.0f, 0.753f, 0.0f, 1.0f);
        theme.setColor(ImGuiCol.PlotLinesHovered, 1.0f, 1.0f, 1.0f, 1.0f);
        theme.setColor(ImGuiCol.PlotHistogram, 1.0f, 0.487f, 0.0f, 1.0f);
        theme.setColor(ImGuiCol.PlotHistogramHovered, 0.982f, 0.673f, 0.380f, 1.0f);

        theme.setColor(ImGuiCol.TableHeaderBg, 0.236f, 0.236f, 0.236f, 1.0f);
        theme.setColor(ImGuiCol.TableBorderStrong, 1.0f, 1.0f, 1.0f, 0.086f);
        theme.setColor(ImGuiCol.TableBorderLight, 1.0f, 1.0f, 1.0f, 0.086f);

        theme.setColor(ImGuiCol.TableRowBg, 0.1f, 0.1f, 0.1f, 1.0f);
        theme.setColor(ImGuiCol.TableRowBgAlt, 0.129f, 0.129f, 0.129f, 1.0f);

        theme.setColor(ImGuiCol.TextSelectedBg, 0.071f, 0.443f, 0.922f, 0.392f);

        theme.setColor(ImGuiCol.DragDropTarget, 1.0f, 0.576f, 0.0f, 0.392f);

        theme.setColor(ImGuiCol.NavHighlight, activeColor.x, activeColor.y, activeColor.z, activeColor.w / 3.0f);
        theme.setColor(ImGuiCol.NavWindowingHighlight, activeColor.x, activeColor.y, activeColor.z, activeColor.w / 3.0f);
        theme.setColor(ImGuiCol.NavWindowingDimBg, activeColor.x, activeColor.y, activeColor.z, activeColor.w / 3.0f);

        theme.setColor(ImGuiCol.ModalWindowDimBg, 0.0f, 0.0f, 0.0f, 0.588f);

        return theme;
    }

    public static ImGuiStyle lightTheme() {
        return darkTheme();
    }
}
