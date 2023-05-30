package engine.editor.console;

import engine.editor.gui.EditorGuiWindow;
import engine.editor.gui.EngineGuiLayer;
import engine.observers.EventSystem;
import engine.observers.Observer;
import engine.observers.events.Event;
import engine.observers.events.EventType;
import engine.stuff.customVariables.Color;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

public class Console_Window extends EditorGuiWindow implements Observer {

    public Console_Window() {
        super("\uEDE6 Console", ImGuiWindowFlags.MenuBar);
        EventSystem.addObserver(this);
    }

    @Override
    public void drawWindow() {
        //<editor-fold desc="MenuBar">
        ImGui.beginMenuBar();
        if (ImGui.menuItem("Send test messages")) {
            Console.log("Test simple 1");
            Console.log("Test simple 2");
            Console.logInfo("Test info 1");
            Console.logInfo("Test info 2");
            Console.logWarning("Test warning 1");
            Console.logWarning("Test warning 2");
            Console.logError("Test error 1");
            Console.logError("Test error 2");
            Console.logSuccess("Test success 1");
            Console.logSuccess("Test success 2");
            Console.logCustom("Test custom 1", new Color(255.0f, 255.0f, 0.0f));
            Console.logCustom("Test custom 2", new Color(255.0f, 255.0f, 0.0f));
            Console.logCustom("Test custom 3", new Color(255.0f, 0.0f, 255.0f));
            Console.logCustom("Test custom 4", new Color(255.0f, 0.0f, 255.0f));
        }
        if (ImGui.menuItem("\uEE09 Clear"))
            Console.clear();
        if (ImGui.menuItem("Stop On Error", "", Console.isStopOnError()))
            Console.setStopOnError(!Console.isStopOnError());
        ImGui.endMenuBar();
        //</editor-fold>

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, ImGui.getStyle().getItemSpacingX(), ImGui.getStyle().getItemSpacingY() / 2);
        for (int i = 0; i < Console.getMessages().size(); i++) {
            ConsoleMessage message = Console.getMessages().get(i);
            ImVec4 messageColor = new ImVec4(message.getMessageColor().r / 255.0f, message.getMessageColor().g / 255.0f, message.getMessageColor().b / 255.0f, message.getMessageColor().a / 255.0f);

            //<editor-fold desc="Message Index">
            ImGui.alignTextToFramePadding();
            ImGui.text("" + i);
            ImGui.sameLine();
            ImGui.setCursorPosX(ImGui.getCursorStartPosX() + ImGui.getFontSize() + ImGui.getStyle().getItemInnerSpacingX());
            //</editor-fold>

            //<editor-fold desc="Background">
            ImGui.getWindowDrawList().addRectFilled(
                    ImGui.getCursorScreenPosX(),
                    ImGui.getCursorScreenPosY(),
                    ImGui.getCursorScreenPosX() + ImGui.getContentRegionMaxX(),
                    ImGui.getCursorScreenPosY() + ImGui.getFrameHeight(),
                    i % 2 == 0 ? ImGui.getColorU32(messageColor.x, messageColor.y, messageColor.z, messageColor.w / 10) : ImGui.getColorU32(messageColor.x, messageColor.y, messageColor.z, messageColor.w / 20)
            );
            //</editor-fold>

            //<editor-fold desc="Message">
            ImGui.alignTextToFramePadding();
            ImGui.setCursorPosX(ImGui.getCursorPosX() + ImGui.getStyle().getFramePaddingX());
            ImGui.textColored(messageColor.x, messageColor.y, messageColor.z, messageColor.w, String.format("%s [%s] %s", message.getMessageIcon(), message.type.name(), message.message));
            //</editor-fold>

            //<editor-fold desc="Delete Button">
            ImGui.pushStyleColor(ImGuiCol.Button, messageColor.x, messageColor.y, messageColor.z, messageColor.w / 6);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, messageColor.x + 0.05f, messageColor.y + 0.05f, messageColor.z + 0.05f, messageColor.w / 6 + 0.05f);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, messageColor.x + 0.1f, messageColor.y + 0.1f, messageColor.z + 0.1f, messageColor.w / 6 + 0.1f);
            ImGui.sameLine();
            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingX() / 3, ImGui.getStyle().getFramePaddingY() / 3);
            ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 99.0f);
            ImGui.setCursorPos(ImGui.getCursorStartPosX() + ImGui.getContentRegionMaxX() - ImGui.getFrameHeight() - ImGui.getStyle().getWindowPaddingX(), ImGui.getCursorPosY() + ImGui.getStyle().getFramePaddingY() * 2);
            if (ImGui.button("##Button_DeleteConsoleMessage_" + i, ImGui.getFrameHeight(), ImGui.getFrameHeight()))
                Console.removeMessage(i);
            ImGui.sameLine();
            ImGui.setCursorPos(ImGui.getCursorPosX() - ImGui.getFrameHeight() + ImGui.getStyle().getFramePaddingY() - ImGui.getStyle().getItemSpacingX() + 2.0f, ImGui.getCursorPosY() - ImGui.getStyle().getFramePaddingY() * 2);
            ImGui.text("\uEEE4");
            ImGui.setCursorPosY(ImGui.getCursorPosY() + ImGui.getStyle().getFramePaddingY() * 2);
            ImGui.popStyleVar(2);
            ImGui.popStyleColor(3);
            //</editor-fold>

            ImGui.setCursorPosY(ImGui.getCursorPosY() - 3.0f);
        }
        ImGui.popStyleVar();
    }

    @Override
    public void onNotify(Event event) {
        if (event.type == EventType.Console_SendMessage)
            EngineGuiLayer.selectWindow(this);
    }
}
