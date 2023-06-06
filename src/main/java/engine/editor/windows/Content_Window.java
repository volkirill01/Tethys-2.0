package engine.editor.windows;

import engine.TestFieldsWindow;
import engine.assets.Asset;
import engine.assets.AssetPool;
import engine.assets.assetTypes.Asset_Folder;
import engine.assets.assetTypes.Asset_Mesh;
import engine.assets.assetTypes.Asset_Scene;
import engine.assets.assetTypes.Asset_Texture;
import engine.editor.gui.EditorGuiWindow;
import engine.editor.gui.EngineGuiLayer;
import engine.editor.gui.GuiFont;
import engine.stuff.utils.Paths;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.*;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Content_Window extends EditorGuiWindow {

    private final static String assetsDirectory;

    private String currentDirectory;
    private final List<Asset> assets = new ArrayList<>();

    private boolean refreshNextFrame = true;

    private static float thumbnailSize = 70.0f;
    private static final float spacing = 8.0f;
    private static final float textAreaHeight = 42.0f;

    static {
        assetsDirectory = Paths.getProjectDirectory() + "\\Assets";
    }

    public Content_Window() {
        super("\uEF36 Content", ImGuiWindowFlags.MenuBar);
        this.currentDirectory = assetsDirectory;
    }

    @Override
    public void drawWindow() {
        // Temporary(or not) load assets every frame
        this.refreshNextFrame = true;

        ImGui.beginMenuBar();
        drawMenuBar();
        ImGui.endMenuBar();

        ImGui.text(Paths.getRelativePath(this.currentDirectory));


        float windowWidth = ImGui.getContentRegionAvailX();
        drawAssetsGrid(this.assets, windowWidth, true, false);

        if (this.refreshNextFrame)
            refresh();
    }

    public static Asset drawAssetsGrid(List<Asset> assets, float windowWidth, boolean interactive, boolean returnClickedAsset) {
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
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, itemSpacing, itemSpacing);
        int xIndex = 0;
        Asset clickedAsset = null;
        for (int i = 0; i < assets.size(); i++) {
            // If we are using recursive get of all assets, uncomment this
//            String currentAssetDirectory = getFileDirectory(asset.getFilepath());
//            if (!this.currentDirectory.equals(currentAssetDirectory))
//                continue;

            ImGui.pushID("AssetCell_" + i);
            clickedAsset = ((Content_Window) EngineGuiLayer.getWindows_ByType(Content_Window.class, true).get(0)).drawAssetCell(assets.get(i), interactive, returnClickedAsset);
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

    public static Object loadObjectFromAsset(Asset.AssetType assetType, String assetPath) {
        return switch (assetType) {
            case Mesh -> AssetPool.getMesh(assetPath);
            case Texture -> AssetPool.getTexture(assetPath);
            default -> throw new IllegalStateException(String.format("Unknown AssetType - '%s'", assetType.name()));
        };
    }

    private void goToDirectory(String directoryPath) {
        this.currentDirectory = directoryPath;
        setRefreshNextFrame();
    }

    private void goBackFolder() {
        String goToDirectory = Paths.getFileDirectory(this.currentDirectory);
        if (!goToDirectory.equals(Paths.getProjectDirectory()))
            this.currentDirectory = goToDirectory;
        setRefreshNextFrame();
    }

    private Asset drawAssetCell(Asset asset, boolean interactive, boolean returnAssetIfClicked) {
        String assetName = asset.getName();
        int maxWordsCount = 14 + TestFieldsWindow.getInts[0]; // TODO REPLACE CONSTANT VALUE WITH CALCULATED VALUE FROM FONT SIZE
        if (assetName.length() > maxWordsCount)
            assetName = assetName.substring(0, maxWordsCount) + "...";

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
        ImGui.getWindowDrawList().addText(
                GuiFont.getDefaultFont(),
                ImGui.getFontSize(),
                ImGui.getCursorScreenPosX() + ImGui.getStyle().getFramePaddingY(),
                ImGui.getCursorScreenPosY() + thumbnailSize,
                ImGui.getColorU32(ImGuiCol.Text),
                assetName,
                thumbnailSize - ImGui.getStyle().getFramePaddingY() * 2
        );
        //</editor-fold>

        ImGui.setCursorPos(endCursorPos.x, endCursorPos.y);

        if (interactive) {
            //<editor-fold desc="Cell click Events">
            switch (asset.getType()) {
                case Folder -> { // TODO ADD FUNCTIONAL TO ASSET CELLS
                    if (ImGui.isItemHovered())
                        if (ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left))
                            goToDirectory(asset.getFilepath());
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

        if (isInAssetsDirectory)
            ImGui.beginDisabled();
        if (ImGui.menuItem("Go Back"))
            goBackFolder();
        if (isInAssetsDirectory)
            ImGui.endDisabled();

        float[] ImFloat = { thumbnailSize};
        if (ImGui.dragFloat("Thumbnail Size", ImFloat, 1.0f, 40.0f, 250.0f))
            thumbnailSize = ImFloat[0];
    }

    private void setRefreshNextFrame() { this.refreshNextFrame = true; }

    private void refresh() {
        loadFiles(this.currentDirectory, false);
        this.refreshNextFrame = false;
    }

    private void loadFiles(String directoryPath, boolean recursive) { loadAssets(this.assets, getFromDirectory(directoryPath, true, recursive), Asset.AssetType.All); }

    public static void loadAssets(List<Asset> outList, List<File> files, Asset.AssetType... filter) {
        outList.clear();
        List<Asset.AssetType> _filter = new ArrayList<>(List.of(filter));

        for (File file : files) {
            String filepath = file.getAbsolutePath();
            if (_filter.contains(Asset.AssetType.All) || _filter.contains(Asset.AssetType.Folder))
                if (file.isDirectory()) {
                    outList.add(new Asset_Folder(filepath));
                    continue;
                }

            switch (filepath.split("\\.")[filepath.split("\\.").length - 1]) {
                case "png" -> {
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

    public static List<File> getFromDirectory(String directoryPath, boolean includeFolders, boolean recursive) {
        File directory = new File(directoryPath);

        if (!directory.isDirectory())
            throw new IllegalStateException(String.format("This file is not directory - '%s'", directoryPath));

        // get all the files from a directory
        File[] listOfFiles = directory.listFiles();
        List<File> resultList = new ArrayList<>(Arrays.asList(listOfFiles));
        for (File file : listOfFiles) {
            if (file.isFile()) {
//                System.out.println(file.getAbsolutePath());
            } else if (file.isDirectory()) {
                if (recursive)
                    resultList.addAll(getFromDirectory(file.getAbsolutePath(), includeFolders, true));
                if (!includeFolders)
                    resultList.remove(file);
            }
        }
        return resultList;
    }

    public static String getAssetsDirectory() { return assetsDirectory; }
}
