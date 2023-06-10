package engine.editor.windows;

import engine.assets.Asset;
import engine.assets.AssetPool;
import engine.assets.assetTypes.Asset_Folder;
import engine.assets.assetTypes.Asset_Mesh;
import engine.assets.assetTypes.Asset_Scene;
import engine.assets.assetTypes.Asset_Texture;
import engine.editor.gui.CustomImGuiWindowFlags;
import engine.editor.gui.EditorGUI;
import engine.editor.gui.EditorGuiWindow;
import engine.editor.gui.EditorGuiFont;
import engine.stuff.utils.Paths;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Content_Window extends EditorGuiWindow {

    private final static String assetsDirectory;

    private String currentDirectory;
    private final List<Asset> assets = new ArrayList<>();

    private boolean refreshNextFrame = true;

    private final ContentHierarchy_SubWindow contentHierarchy;

    private static float thumbnailSize = 70.0f;
    private static final float spacing = 8.0f;
    private static final float textAreaHeight = 40.0f;

    private static String stringFilter = "";

    static {
        assetsDirectory = Paths.getProjectDirectory() + "\\Assets";
    }

    public Content_Window() {
        super("\uEF36 Content", ImGuiWindowFlags.MenuBar, CustomImGuiWindowFlags.NoWindowPadding);
        this.currentDirectory = assetsDirectory;
        this.contentHierarchy = new ContentHierarchy_SubWindow(this);
    }

    @Override
    public void drawWindow() {
        // Temporary(or not) load assets every frame
        this.refreshNextFrame = true;

        ImGui.beginMenuBar();
        drawMenuBar();
        ImGui.endMenuBar();

        //<editor-fold desc="Content Hierarchy">
        ImGui.pushStyleColor(ImGuiCol.ChildBg, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, ImGui.getStyle().getWindowPaddingX() / 2, ImGui.getStyle().getWindowPaddingY() / 2);
        ImGui.beginChild("ContentWindow_ContentHierarchy", ImGui.getContentRegionAvailX() / 6, ImGui.getContentRegionAvailY(), true);
        ImGui.popStyleVar();
        ImGui.popStyleColor();

        this.contentHierarchy.imgui();
        ImGui.endChild();
        //</editor-fold>

        //<editor-fold desc="Content Grid">
        ImGui.sameLine();
        ImGui.setCursorPosX(ImGui.getCursorPosX() - ImGui.getStyle().getItemSpacingX());
        ImGui.pushStyleColor(ImGuiCol.ChildBg, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, ImGui.getStyle().getWindowPaddingX() / 2, ImGui.getStyle().getWindowPaddingY() / 2);
        ImGui.beginChild("ContentWindow_ContentGrid", ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY(), true);
        ImGui.popStyleVar();
        ImGui.popStyleColor();

        drawContentGrid(this.assets, ImGui.getContentRegionAvailX(), true, false, this);
        if (ImGui.beginPopupContextWindow(ImGuiPopupFlags.NoOpenOverItems | ImGuiPopupFlags.MouseButtonRight)) {
            drawContextPopup(this.currentDirectory);
            ImGui.endPopup();
        }
        ImGui.endChild();
        //</editor-fold>

        if (!new File(this.currentDirectory).exists())
            this.currentDirectory = assetsDirectory;

        if (this.refreshNextFrame)
            refresh();
    }

    public static Asset drawContentGrid(List<Asset> assets, float windowWidth, boolean interactive, boolean returnClickedAsset, Content_Window window) {
        //<editor-fold desc="Calculating Cell size and spacing">
        int columnsCount = (int) (windowWidth / (thumbnailSize + spacing));
        if (columnsCount < 1)
            columnsCount = 1;
        float availableWidth = windowWidth - columnsCount * (thumbnailSize + spacing);
        float itemSpacing = availableWidth / (columnsCount + 1) + spacing;
        if (columnsCount == 1)
            itemSpacing = 0.0f;
        //</editor-fold>

        //<editor-fold desc="Draw Assets grid">
        ImGui.setCursorPos(ImGui.getCursorPosX() + itemSpacing / 2, ImGui.getCursorPosY() + itemSpacing / 2);
        ImVec2 defaultItemSpacing = ImGui.getStyle().getItemSpacing();
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, itemSpacing, itemSpacing);
        int xIndex = 0;
        Asset clickedAsset = null;
        for (int i = 0; i < assets.size(); i++) {
            // If we are using recursive get of all assets, uncomment this
            String currentAssetDirectory = Paths.getFileDirectory(assets.get(i).getFilepath());
            if (window != null) {
                if (stringFilter.equals("")) {
                    if (!window.currentDirectory.equals(currentAssetDirectory))
                        continue;
                } else if (!assets.get(i).getName().contains(stringFilter))
                    continue;
            }

            ImGui.pushID("AssetCell_" + i);
            clickedAsset = drawAssetCell(assets.get(i), interactive, returnClickedAsset, window, defaultItemSpacing);
            ImGui.popID();

            //<editor-fold desc="Check if current cell fits in bounds">
            if (xIndex < columnsCount - 1) {
                ImGui.sameLine();
                xIndex++;
            } else {
                ImGui.setCursorPosX(ImGui.getCursorPosX() + itemSpacing / 2);
                xIndex = 0;
            }
            //</editor-fold>

            if (clickedAsset != null)
                break;
        }
        ImGui.popStyleVar();
        //</editor-fold>

        return clickedAsset;
    }

    private static void drawContextPopup(String currentDirectory) {
        if (Desktop.getDesktop().isSupported(Desktop.Action.OPEN))
            if (ImGui.menuItem("Open in Explorer")) {
                try {
                    Desktop.getDesktop().open(new File(currentDirectory));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
    }

    private static void drawItemContextPopup(Asset asset) {
        if (Desktop.getDesktop().isSupported(Desktop.Action.OPEN))
            if (ImGui.menuItem("Open File")) {
                try {
                    Desktop.getDesktop().open(new File(asset.getFilepath()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        if (Desktop.getDesktop().isSupported(Desktop.Action.OPEN))
            if (ImGui.menuItem("Open in Explorer")) {
                try {
                    Desktop.getDesktop().open(new File(asset.getFilepath()).getParentFile());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        if (Desktop.getDesktop().isSupported(Desktop.Action.MOVE_TO_TRASH)) {
            ImGui.separator();
            if (ImGui.menuItem("Delete"))
                Desktop.getDesktop().moveToTrash(new File(asset.getFilepath()));
        }
    }

    public static Object loadObjectFromAsset(Asset.AssetType assetType, String assetPath) {
        if (assetType == null)
            return null;

        return switch (assetType) {
            case Mesh -> AssetPool.getMesh(assetPath);
            case Texture -> AssetPool.getTexture(assetPath);
            default -> throw new IllegalStateException(String.format("Unknown AssetType - '%s'", assetType.name()));
        };
    }

    public void goToDirectory(String directoryPath) {
        this.currentDirectory = directoryPath;
        setRefreshNextFrame();
    }

    private void goBackFolder() {
        String goToDirectory = Paths.getFileDirectory(this.currentDirectory);
        if (!goToDirectory.equals(Paths.getProjectDirectory()))
            this.currentDirectory = goToDirectory;
        setRefreshNextFrame();
    }

    private static Asset drawAssetCell(Asset asset, boolean interactive, boolean returnAssetIfClicked, Content_Window window, ImVec2 defaultItemSpacing) {
        String assetName = asset.getName();
//        int maxWordsCount = 14 + TestFieldsWindow.getInts[0];
//        if (assetName.length() > maxWordsCount)
//            assetName = assetName.substring(0, maxWordsCount) + "..."; // TODO ADD LIMITING OF ASSET NAME

        ImVec2 startCursorPos = ImGui.getCursorPos();

        //<editor-fold desc="Cell Background">
        Asset result = null;
        if (ImGui.button("##AssetButton_" + asset.getFilepath(), thumbnailSize, thumbnailSize + textAreaHeight))
            if (returnAssetIfClicked)
                result = asset;

        ImVec2 endCursorPos = ImGui.getCursorPos();
        if (ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload("Asset", new AbstractMap.SimpleEntry<>(asset.getType(), asset.getFilepath()), ImGuiCond.Once);
            ImGui.text(asset.getName());
            ImGui.endDragDropSource();
        }

        ImGui.setCursorPos(startCursorPos.x, startCursorPos.y);
        //</editor-fold>

        //<editor-fold desc="Icon Background">
        ImGui.getWindowDrawList().addRectFilled(
                ImGui.getCursorScreenPosX() + ImGui.getStyle().getFramePaddingY(),
                ImGui.getCursorScreenPosY() + ImGui.getStyle().getFramePaddingY(),
                ImGui.getCursorScreenPosX() + thumbnailSize - ImGui.getStyle().getFramePaddingY(),
                ImGui.getCursorScreenPosY() + thumbnailSize - ImGui.getStyle().getFramePaddingY(),
                ImGui.getColorU32(ImGuiCol.FrameBg),
                ImGui.getStyle().getFrameRounding()
        );
        //</editor-fold>

        //<editor-fold desc="Icon">
        if (asset.getIcon() != null)
            ImGui.getWindowDrawList().addImage(
                    asset.getIcon().getTextureID(),
                    ImGui.getCursorScreenPosX() + ImGui.getStyle().getFramePaddingY() * 2,
                    ImGui.getCursorScreenPosY() + ImGui.getStyle().getFramePaddingY() * 2,
                    ImGui.getCursorScreenPosX() + thumbnailSize - ImGui.getStyle().getFramePaddingY() * 2,
                    ImGui.getCursorScreenPosY() + thumbnailSize - ImGui.getStyle().getFramePaddingY() * 2,
                    0, 1, 1, 0
            );
        //</editor-fold>

        //<editor-fold desc="Cell Label">
        ImGui.pushClipRect(
                ImGui.getCursorScreenPosX() + ImGui.getStyle().getFramePaddingY(),
                ImGui.getCursorScreenPosY() + thumbnailSize,
                ImGui.getCursorScreenPosX() + ImGui.getStyle().getFramePaddingY() + thumbnailSize,
                ImGui.getCursorScreenPosY() + thumbnailSize + textAreaHeight,
                true
        );
        ImGui.getWindowDrawList().addText(
                EditorGuiFont.getDefaultFont(),
                ImGui.getFontSize(),
                ImGui.getCursorScreenPosX() + ImGui.getStyle().getFramePaddingY(),
                ImGui.getCursorScreenPosY() + thumbnailSize,
                ImGui.getColorU32(ImGuiCol.Text),
                assetName,
                thumbnailSize - ImGui.getStyle().getFramePaddingY() * 2
        );
        ImGui.popClipRect();
        //</editor-fold>

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, defaultItemSpacing.x, defaultItemSpacing.y);
        if (ImGui.beginPopupContextItem(ImGuiPopupFlags.MouseButtonRight)) {
            drawItemContextPopup(asset);
            ImGui.endPopup();
        }
        ImGui.popStyleVar();

        ImGui.setCursorPos(endCursorPos.x, endCursorPos.y);

        if (interactive) {
            //<editor-fold desc="Cell click Events">
            switch (asset.getType()) {
                case Folder -> { // TODO ADD FUNCTIONAL TO ASSET CELLS
                    if (ImGui.isItemHovered())
                        if (ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left))
                            window.goToDirectory(asset.getFilepath());
                }
                case Texture -> {
                }
                case Mesh -> {
                }
                case Scene -> {
                }
                case Other -> {
                }
                default -> throw new IllegalStateException(String.format("Unknown AssetType - '%s'", asset.getType()));
            }
            //</editor-fold>
        }

        return result;
    }

    private void drawMenuBar() {
//        if (ImGui.menuItem("Refresh"))
//            refresh();

        boolean isInAssetsDirectory = Paths.getFileDirectory(this.currentDirectory).equals(Paths.getProjectDirectory());

        //<editor-fold desc="Go back button">
        ImGui.pushStyleColor(ImGuiCol.Button, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.Border, 0.0f, 0.0f, 0.0f, 0.0f);
        if (isInAssetsDirectory)
            ImGui.beginDisabled();
        if (ImGui.button("\uEA5C", ImGui.getFrameHeight(), ImGui.getFrameHeight()))
            goBackFolder();
        if (isInAssetsDirectory)
            ImGui.endDisabled();
        ImGui.popStyleColor(2);
        //</editor-fold>

        ImGui.separator();

        //<editor-fold desc="Draw current directory path">
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.Border, 0.0f, 0.0f, 0.0f, 0.0f);

        String relativePath = Paths.getRelativePath(this.currentDirectory);
        String[] directoriesNames = relativePath.replace("\\", "/").split("/");
        String[] directories = new String[directoriesNames.length];
        for (int i = 0; i < directories.length; i++)
            directories[i] = relativePath.split(directoriesNames[i])[0] + directoriesNames[i];

        for (int i = 0; i < directoriesNames.length; i++) {
            if (i == directoriesNames.length - 1)
                ImGui.beginDisabled();
            if (ImGui.button(directoriesNames[i]))
                goToDirectory(Paths.getAbsoluteDirectory(directories[i]));
            if (i == directoriesNames.length - 1)
                ImGui.endDisabled();

            if (i != directoriesNames.length - 1)
                ImGui.text("\uEA69");
        }
        ImGui.popStyleColor(2);
        ImGui.popStyleVar();
        //</editor-fold>

        //<editor-fold desc="Search field">
        ImGui.setCursorPosX(ImGui.getCursorStartPosX() + ImGui.getContentRegionAvailX() - ImGui.getContentRegionAvailX() / 4);
        stringFilter = EditorGUI.field_TextNoLabel("##Contet_FindField", stringFilter, "Search...", '\uED1B', ImGui.getContentRegionAvailX() - ImGui.getStyle().getItemSpacingX() - ImGui.getFrameHeight());
        //</editor-fold>

        //<editor-fold desc="Setting dropdown">
        if (ImGui.button("\uEFE1", ImGui.getFrameHeight(), ImGui.getFrameHeight()))
            ImGui.openPopup("ContentWindow_Settings");

        if (ImGui.beginPopup("ContentWindow_Settings")) {
            ImGui.alignTextToFramePadding();
            ImGui.text("Thumbnail size");

            ImGui.sameLine();
            ImGui.setNextItemWidth(80.0f);
            float[] ImFloat = { thumbnailSize};
            if (ImGui.dragFloat("##ThumbnailSize", ImFloat, 1.0f, 40.0f, 250.0f))
                thumbnailSize = ImFloat[0];
            ImGui.endPopup();
        }
        //</editor-fold>
    }

    private void setRefreshNextFrame() { this.refreshNextFrame = true; }

    private void refresh() {
        loadFiles(this.currentDirectory, true);
        this.refreshNextFrame = false;
    }

    private void loadFiles(String directoryPath, boolean recursive) { loadAssets(this.assets, getFromDirectory(directoryPath, true, recursive), Asset.AssetType.All); }

    public static void loadAssets(List<Asset> outList, File[] files, Asset.AssetType... filter) {
        outList.clear();
        List<Asset.AssetType> _filter = new ArrayList<>(List.of(filter));

        for (File file : files) {
            String filepath = file.getAbsolutePath();
            if (_filter.contains(Asset.AssetType.All) || _filter.contains(Asset.AssetType.Folder))
                if (file.isDirectory()) {
                    outList.add(new Asset_Folder(filepath));
                    continue;
                }

            switch (Paths.getFileExtensionFromFilepath(filepath)) {
                case "png", "jpg" -> {
                    if (_filter.contains(Asset.AssetType.All) || _filter.contains(Asset.AssetType.Texture))
                        outList.add(new Asset_Texture(filepath, AssetPool.getTexture(file.getAbsolutePath())));
                }
                case "obj" -> {
                    if (_filter.contains(Asset.AssetType.All) || _filter.contains(Asset.AssetType.Mesh))
                        outList.add(new Asset_Mesh(filepath));
                }
                case "scene" -> {
                    if (_filter.contains(Asset.AssetType.All) || _filter.contains(Asset.AssetType.Scene))
                        outList.add(new Asset_Scene(filepath));
                }
                default -> {
                    if (_filter.contains(Asset.AssetType.All) || _filter.contains(Asset.AssetType.Other))
                        outList.add(new Asset(filepath, Asset.AssetType.Other, AssetPool.getTexture("Resources/icons/assets/icon=file-solid-(256x256).png")));
                }
            }
        }
    }

    public static File[] getFromDirectory(String directoryPath, boolean includeFolders, boolean recursive) {
        File directory = new File(directoryPath);

        if (!directory.isDirectory())
            throw new IllegalStateException(String.format("This file is not directory - '%s'", directoryPath));

        // get all the files from a directory
        File[] listOfFiles = directory.listFiles();
        List<File> tmpList = new ArrayList<>(Arrays.asList(listOfFiles));
        for (File file : listOfFiles) {
            if (file.isFile()) {
//                System.out.println(file.getAbsolutePath());
            } else if (file.isDirectory()) {
                if (recursive)
                    tmpList.addAll(List.of(getFromDirectory(file.getAbsolutePath(), includeFolders, true)));
                if (!includeFolders)
                    tmpList.remove(file);
            }
        }

        File[] resultArray = new File[tmpList.size()];
        tmpList.toArray(resultArray);

        Paths.sortFiles(resultArray);

        return resultArray;
    }

    public List<Asset> getAssets() { return this.assets; }

    public static String getAssetsDirectory() { return assetsDirectory; }
}
