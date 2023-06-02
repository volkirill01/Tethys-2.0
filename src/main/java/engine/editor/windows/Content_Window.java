package engine.editor.windows;

import engine.TestFieldsWindow;
import engine.assets.Asset;
import engine.assets.AssetPool;
import engine.assets.assetTypes.Asset_Folder;
import engine.assets.assetTypes.Asset_Mesh;
import engine.assets.assetTypes.Asset_Scene;
import engine.assets.assetTypes.Asset_Texture;
import engine.editor.gui.EditorGuiWindow;
import engine.editor.gui.GuiFont;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.*;

import java.io.File;
import java.net.URI;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Content_Window extends EditorGuiWindow {

    private final static String assetsDirectory;
    private List<File> contentFiles = new ArrayList<>();

    private String currentDirectory;
    private final List<Asset> assets = new ArrayList<>();

    private boolean refreshNextFrame = true;

    private float thumbnailSize = 70.0f;
    private static final float spacing = 8.0f;
    private static final float textAreaHeight = 42.0f;

    static {
        assetsDirectory = getProjectDirectory() + "\\Assets";
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

        ImGui.text(getRelativePath(this.currentDirectory));

        //<editor-fold desc="Calculating Cell size and spacing">
        float windowWidth = ImGui.getContentRegionAvailX();

        int columnsCount = (int) (windowWidth / (this.thumbnailSize + spacing));
        if (columnsCount < 1)
            columnsCount = 1;
        float availableWidth = windowWidth - columnsCount * (this.thumbnailSize + spacing);
        float itemSpacing = availableWidth / (columnsCount + 1) + spacing;
        if (columnsCount == 1)
            itemSpacing = 0.0f;
        //</editor-fold>

        //<editor-fold desc="Draw Assets grid">
        ImGui.setCursorPos(ImGui.getCursorPosX() + itemSpacing / 2, ImGui.getCursorPosY() + itemSpacing / 2);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, itemSpacing, itemSpacing);
        int xIndex = 0;
        for (Asset asset : this.assets) {
            // If we are using recursive get of all assets, uncomment this
//            String currentAssetDirectory = getFileDirectory(asset.getFilepath());
//            if (!this.currentDirectory.equals(currentAssetDirectory))
//                continue;

            drawAssetCell(asset);

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

            //<editor-fold desc="Check if current cell fits in bounds">
            if (xIndex < columnsCount - 1) {
                ImGui.sameLine();
                xIndex++;
            } else {
                ImGui.setCursorPosX(ImGui.getCursorPosX() + itemSpacing / 2);
                xIndex = 0;
            }
            //</editor-fold>
        }
        ImGui.popStyleVar();
        //</editor-fold>

        if (this.refreshNextFrame)
            refresh();
    }

    private String getAbsoluteDirectory(String filepath) {
        File file = new File(filepath);
        return file.getAbsolutePath();
    }

    private String getFileDirectory(String filepath) {
        File file = new File(filepath);
        return file.getParent();
    }

    private void goToDirectory(String directoryPath) {
        this.currentDirectory = directoryPath;
        setRefreshNextFrame();
    }

    private String getRelativePath(String filepath) {
        // Creating a Files from directories
        File projectDirectory = new File(getProjectDirectory());
        File goToDirectory = new File(filepath);

        // Convert the absolute path to URI
        URI goToURL = goToDirectory.toURI();
        URI projectURL = projectDirectory.toURI();

        // Creating a relative path from the two paths
        URI relativePath = projectURL.relativize(goToURL);

        // Convert the URI to string and set current directory to it
        return relativePath.getPath();
    }

    private void goBackFolder() {
        String goToDirectory = getFileDirectory(this.currentDirectory);
        if (!goToDirectory.equals(getProjectDirectory()))
            this.currentDirectory = goToDirectory;
        setRefreshNextFrame();
    }

    private static String getProjectDirectory() { return System.getProperty("user.dir"); }

    private void drawAssetCell(Asset asset) {
        String assetName = asset.getName();
        int maxWordsCount = 14 + TestFieldsWindow.getInts[0]; // TODO REPLACE CONSTANT VALUE WITH CALCULATED VALUE FROM FONT SIZE
        if (assetName.length() > maxWordsCount)
            assetName = assetName.substring(0, maxWordsCount) + "...";

        ImVec2 startCursorPos = ImGui.getCursorPos();

        //<editor-fold desc="Cell Background">
        ImGui.button("##AssetButton_" + asset.getFilepath(), this.thumbnailSize, this.thumbnailSize + textAreaHeight);
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
                ImGui.getCursorScreenPosX() + this.thumbnailSize - ImGui.getStyle().getFramePaddingY(),
                ImGui.getCursorScreenPosY() + this.thumbnailSize - ImGui.getStyle().getFramePaddingY(),
                ImGui.getColorU32(ImGuiCol.FrameBg),
                ImGui.getStyle().getFrameRounding()
        );
        //</editor-fold>

        //<editor-fold desc="Icon">
        ImGui.getWindowDrawList().addImage(
                asset.getIcon().getTextureID(),
                ImGui.getCursorScreenPosX() + ImGui.getStyle().getFramePaddingY() * 2,
                ImGui.getCursorScreenPosY() + ImGui.getStyle().getFramePaddingY() * 2,
                ImGui.getCursorScreenPosX() + this.thumbnailSize - ImGui.getStyle().getFramePaddingY() * 2,
                ImGui.getCursorScreenPosY() + this.thumbnailSize - ImGui.getStyle().getFramePaddingY() * 2,
                0, 1, 1, 0
        );
        //</editor-fold>

        //<editor-fold desc="Cell Label">
        ImGui.getWindowDrawList().addText(
                GuiFont.getDefaultFont(),
                ImGui.getFontSize(),
                ImGui.getCursorScreenPosX() + ImGui.getStyle().getFramePaddingY(),
                ImGui.getCursorScreenPosY() + this.thumbnailSize,
                ImGui.getColorU32(ImGuiCol.Text),
                assetName,
                this.thumbnailSize - ImGui.getStyle().getFramePaddingY() * 2
        );
        //</editor-fold>

        ImGui.setCursorPos(endCursorPos.x, endCursorPos.y);
    }

    private void drawMenuBar() {
//        if (ImGui.menuItem("Refresh"))
//            refresh();

        boolean isInAssetsDirectory = getFileDirectory(this.currentDirectory).equals(getProjectDirectory());

        if (isInAssetsDirectory)
            ImGui.beginDisabled();
        if (ImGui.menuItem("Go Back"))
            goBackFolder();
        if (isInAssetsDirectory)
            ImGui.endDisabled();

        float[] ImFloat = { this.thumbnailSize};
        if (ImGui.dragFloat("Thumbnail Size", ImFloat, 1.0f, 40.0f, 250.0f))
            this.thumbnailSize = ImFloat[0];
    }

    private void setRefreshNextFrame() { this.refreshNextFrame = true; }

    private void refresh() {
        loadFiles(this.currentDirectory);
        this.refreshNextFrame = false;
    }

    private void loadFiles(String directoryPath) {
        this.contentFiles.clear();
        this.contentFiles = getFromDirectory(directoryPath, true);
        loadAssets();
    }

    private void loadAssets() {
        this.assets.clear();
        for (File file : this.contentFiles) {
            String filepath = file.getAbsolutePath();
            if (file.isDirectory()) {
                this.assets.add(new Asset_Folder(filepath));
                continue;
            }

            switch (filepath.split("\\.")[filepath.split("\\.").length - 1]) {
                case "png" -> this.assets.add(new Asset_Texture(filepath, AssetPool.getTexture(file.getAbsolutePath())));
                case "obj" -> this.assets.add(new Asset_Mesh(filepath));
                case "scene" -> this.assets.add(new Asset_Scene(filepath));
                default -> this.assets.add(new Asset(filepath, Asset.AssetType.Other, AssetPool.getTexture("editorFiles/icons/assets/icon=file-solid-(256x256).png")));
            }
        }
    }

    private static List<File> getFromDirectory(String directoryPath, boolean includeFolders) {
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
                if (!includeFolders)
                    resultList.remove(file);
            }
        }
        return resultList;
    }

    private static List<File> recursiveGetFromDirectory(String directoryPath, boolean includeFolders) {
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
                resultList.addAll(recursiveGetFromDirectory(file.getAbsolutePath(), includeFolders));
                if (!includeFolders)
                    resultList.remove(file);
            }
        }
        return resultList;
    }
}
