package engine.entity;

import com.google.gson.Gson;
import engine.TestFieldsWindow;
import engine.editor.gui.EditorGUI;
import engine.editor.gui.EditorGuiFont;
import engine.entity.component.Component;
import engine.entity.component.Transform;
import engine.stuff.utils.EditorGson;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;

public class GameObject_GUI {

    public static void imgui(GameObject obj) {
        ImGui.setCursorPos(ImGui.getCursorStartPosX() + ImGui.getStyle().getWindowPaddingX() / 2, ImGui.getCursorStartPosY() + ImGui.getStyle().getWindowPaddingY() / 2);
        obj.setName(EditorGUI.field_TextNoLabel("GameObject_NameField_" + obj.tagComponent.id.toString(), obj.getName(), "Name", ImGui.getContentRegionAvailX() / 1.5f));
        ImGui.sameLine();

        //<editor-fold desc="Add Component Button">
        float width = (ImGui.getContentRegionAvailX() / 2) - (ImGui.calcTextSize("Add component").x / 2) - ImGui.getStyle().getWindowPaddingX() / 2;
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, width, ImGui.getStyle().getFramePaddingY());
        if (ImGui.button("Add component"))
            ImGui.openPopup("Popup_AddComponent_Button");
        ImGui.popStyleVar();
        //</editor-fold>

        //<editor-fold desc="Add Component Popup">
        if (ImGui.beginPopup("Popup_AddComponent_Button")) {
            drawAddComponentPopup(obj);
            ImGui.endPopup();
        }

        if (ImGui.beginPopupContextWindow("Popup_AddComponent_Context")) {
            if (ImGui.beginMenu("Add Component")) {
                drawAddComponentPopup(obj);
                ImGui.endMenu();
            }
            ImGui.endPopup();
        }
        //</editor-fold>

        ImGui.setCursorPosY(ImGui.getCursorPosY() + ImGui.getStyle().getItemSpacingY());

        for (Component c : obj.getAllComponents()) {
            if (drawComponentHeader(obj, c))
                return;
        }
    }

    private static boolean drawComponentHeader(GameObject obj, Component c) {
        ImGui.pushID("ComponentGUI_" + c);

        int treeNodeColor = ImGuiCol.FrameBg;
        if (ImGui.isMouseHoveringRect(
                ImGui.getCursorScreenPosX(),
                ImGui.getCursorScreenPosY(),
                ImGui.getCursorScreenPosX() + ImGui.getContentRegionAvailX() + TestFieldsWindow.getFloats[0],
                ImGui.getCursorScreenPosY() + ImGui.getFrameHeight() + TestFieldsWindow.getFloats[1]
        )) {
            treeNodeColor = ImGuiCol.FrameBgHovered;
            if (ImGui.isMouseDown(ImGuiMouseButton.Left))
                treeNodeColor = ImGuiCol.FrameBgActive;
        }

        ImGui.getWindowDrawList().addRectFilled(
                ImGui.getCursorScreenPosX(),
                ImGui.getCursorScreenPosY(),
                ImGui.getCursorScreenPosX() + ImGui.getContentRegionAvailX(),
                ImGui.getCursorScreenPosY() + ImGui.getFrameHeight(),
                ImGui.getColorU32(treeNodeColor)
        );
        // Black line on top of tree node
        ImGui.getWindowDrawList().addRectFilled(
                ImGui.getCursorScreenPosX(),
                ImGui.getCursorScreenPosY(),
                ImGui.getCursorScreenPosX() + ImGui.getContentRegionAvailX(),
                ImGui.getCursorScreenPosY() + ImGui.getFrameHeight() / 10,
                ImGui.getColorU32(ImGui.getStyle().getColor(ImGuiCol.TitleBg).x, ImGui.getStyle().getColor(ImGuiCol.TitleBg).y, ImGui.getStyle().getColor(ImGuiCol.TitleBg).z, ImGui.getStyle().getColor(ImGuiCol.TitleBg).w / 4)
        );

        ImGui.pushStyleColor(ImGuiCol.Header, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.HeaderActive, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.Border, 0.0f, 0.0f, 0.0f, 0.0f);
        EditorGuiFont.bindFont(EditorGuiFont.getSemiBoldFont());
        boolean treeNodeOpen = EditorGUI.beginCollapsingHeader(c.getClass().getSimpleName(), (c.getClass() == Transform.class ? ImGuiTreeNodeFlags.DefaultOpen : ImGuiTreeNodeFlags.None) | ImGuiTreeNodeFlags.AllowItemOverlap);
        EditorGuiFont.unbindFont();

        boolean removeComponent = false;
        ImGui.popStyleColor(4);
        if (ImGui.beginPopupContextItem("Component_Context_Popup_" + c)) {
            if (c.getClass() != Transform.class) {
                if (ImGui.selectable("Remove"))
                    removeComponent = true;
            }
            if (ImGui.selectable("Reset"))
                c.reset();
            ImGui.endPopup();
        }

        ImGui.sameLine();
        ImGui.setCursorPosX(ImGui.getCursorStartPosX() + ImGui.getContentRegionMaxX() - ImGui.getFrameHeight());
        if (ImGui.invisibleButton("Component_Context_Button_" + c, ImGui.getFrameHeight(), ImGui.getFrameHeight()))
            ImGui.openPopup("Component_Context_Popup_" + c);
        ImGui.sameLine();
        ImGui.setCursorPosX(ImGui.getCursorStartPosX() + ImGui.getContentRegionMaxX() - ImGui.getFrameHeight() + ImGui.getStyle().getFramePaddingY());
        ImGui.text("\uEFE1"); // \uEFA2 \uEFE1 \uEFE2

        if (treeNodeOpen) {
            c.imgui();
            EditorGUI.endCollapsingHeader();
        }
        ImGui.popID();

        if (removeComponent) {
            obj.removeComponent(c.getClass());
            return true;
        }
        return false;
    }

    private static void drawAddComponentPopup(GameObject obj) {
        for (Component c : Component.allComponents()) {
            if (!obj.hasComponent(c.getClass()))
                if (ImGui.menuItem(c.getClass().getSimpleName())) {
                    Gson gson = EditorGson.getGsonBuilder();
                    String gsonString = gson.toJson(c, Component.class);
                    Component copy = gson.fromJson(gsonString, Component.class);
                    obj.addComponent(copy);
                }
        }
    }
}
