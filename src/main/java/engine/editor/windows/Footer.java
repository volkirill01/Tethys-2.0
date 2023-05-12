package engine.editor.windows;

import engine.editor.console.Console;
import engine.editor.console.ConsoleMessage;
import engine.editor.console.LogType;
import engine.stuff.Window;
import engine.stuff.customVariables.Color;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

public class Footer {

    public static float imgui() {
        int windowFlags = ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoTitleBar |
                ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

        float footerHeight = ImGui.getFrameHeight();

        ImGui.setNextWindowPos(0.0f, Window.getHeight() - footerHeight, ImGuiCond.Always);
        ImGui.setNextWindowSize(Window.getWidth(), footerHeight); // Set Footer window size
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.WindowBg, ImGui.getColorU32(ImGuiCol.MenuBarBg));
        ImGui.begin("Footer", windowFlags);
        ImGui.popStyleVar(3);
        ImGui.popStyleColor();

        drawFooter();

        ImGui.end();
        return footerHeight;
    }

    private static void drawFooter() {
        float offset = 270.0f;

        //<editor-fold desc="Console Messages Count">
        ImGui.alignTextToFramePadding();
        ImGui.setCursorPosX(ImGui.getCursorStartPosX() + ImGui.getContentRegionMaxX() / 1.2f - offset);
        float startX = ImGui.getCursorPosX();
        for (LogType type : LogType.values()) {
            Color tmpColor = ConsoleMessage.getMessageColor(type);
            String tmpString = ConsoleMessage.getMessageIcon(type);
            ImVec4 tmpVec4 = new ImVec4(tmpColor.r / 255.0f, tmpColor.g / 255.0f, tmpColor.b / 255.0f, tmpColor.a / 255.0f);
            ImGui.textColored(tmpVec4.x, tmpVec4.y, tmpVec4.z, tmpVec4.w, tmpString + " " + Console.getMessagesCount(type));
            ImGui.sameLine();
            ImGui.setCursorPosX(ImGui.getCursorPosX() + ImGui.getStyle().getItemSpacingX());
        }
        float endX = ImGui.getCursorPosX();
        //</editor-fold>

        ImGui.setCursorPosX(ImGui.getCursorStartPosX() + ImGui.getContentRegionMaxX() / 1.2f - ImGui.calcTextSize("Last Message:").x + (endX - startX) + 120.0f - offset);

        //<editor-fold desc="Last Console Message">
        if (Console.getMessages().size() > 0) {
            ImGui.text("Last Message: ");
            ConsoleMessage message = Console.getMessages().get(Console.getMessages().size() - 1);
            ImVec4 messageColor = new ImVec4(message.getMessageColor().r / 255.0f, message.getMessageColor().g / 255.0f, message.getMessageColor().b / 255.0f, message.getMessageColor().a / 255.0f);
            ImGui.sameLine();
            ImGui.textColored(messageColor.x, messageColor.y, messageColor.z, messageColor.w, String.format("[%s] %s", message.type.name(), message.message));
        }
        //</editor-fold>
    }
}
