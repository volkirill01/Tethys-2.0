package engine.editor.windows;

import engine.assets.AssetPool;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiPopupFlags;
import imgui.flag.ImGuiTreeNodeFlags;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContentHierarchy_SubWindow {

    private final Content_Window parent;

    public ContentHierarchy_SubWindow(Content_Window parent) { this.parent = parent; }

    public void imgui() { drawDirectory(new File(Content_Window.getAssetsDirectory())); }

    private void drawDirectory(File directory) {
        File[] filesInDirectory = directory.listFiles();
        List<File> filesList = new ArrayList<>();
        for (File file : filesInDirectory)
            if (file.isDirectory())
                filesList.add(file);

        ImVec2 startCursorPos = ImGui.getCursorPos();
        if (ImGui.treeNodeEx("\t  " + directory.getName(), (filesList.size() == 0 ? ImGuiTreeNodeFlags.Leaf : ImGuiTreeNodeFlags.OpenOnArrow))) {
            if (ImGui.beginPopupContextItem(ImGuiPopupFlags.MouseButtonRight)) {
                drawItemContextPopup(directory);
                ImGui.endPopup();
            }

            if (ImGui.isItemHovered())
                if (ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left))
                    parent.goToDirectory(directory.getPath());

            for (File file : filesList)
                drawDirectory(file);
            ImGui.treePop();
        } else {
            if (ImGui.beginPopupContextItem(ImGuiPopupFlags.MouseButtonRight)) {
                drawItemContextPopup(directory);
                ImGui.endPopup();
            }

            if (ImGui.isItemHovered())
                if (ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left))
                    parent.goToDirectory(directory.getPath());
        }

        if (directory.exists()) {
            ImVec2 endCursorPos = ImGui.getCursorPos();

            ImGui.setCursorPos(startCursorPos.x + ImGui.getFrameHeight(), startCursorPos.y + ImGui.getStyle().getFramePaddingY() / 2);
            ImGui.image(directory.listFiles().length > 0 ? AssetPool.getTexture("Resources/icons/assets/icon=folder-solid-(256x256).png").getTextureID() : AssetPool.getTexture("Resources/icons/assets/icon=folder-open-regular-(256x256).png").getTextureID(), ImGui.getFrameHeight() / 2, ImGui.getFrameHeight() / 2, 0.0f, 1.0f, 1.0f, 0.0f);

            ImGui.setCursorPos(endCursorPos.x, endCursorPos.y);
        }
    }

    private void drawItemContextPopup(File file) {
        if (Desktop.getDesktop().isSupported(Desktop.Action.OPEN))
            if (ImGui.menuItem("Open in Explorer")) {
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        if (Desktop.getDesktop().isSupported(Desktop.Action.MOVE_TO_TRASH)) {
            ImGui.separator();
            if (ImGui.menuItem("Delete"))
                Desktop.getDesktop().moveToTrash(file);
        }
    }
}
