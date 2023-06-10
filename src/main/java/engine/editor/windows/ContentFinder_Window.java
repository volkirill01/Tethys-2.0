package engine.editor.windows;

import engine.assets.Asset;
import engine.assets.assetTypes.Asset_Mesh;
import engine.editor.gui.EditorGuiWindow;
import engine.editor.gui.EngineGuiLayer;
import imgui.ImGui;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ContentFinder_Window extends EditorGuiWindow {

    private static Asset.AssetType[] filter;
    private static Object targetObject;
    private static Field targetField;
    private final List<Asset> assets = new LinkedList<>();

    public ContentFinder_Window() { super("\uED14 Content Finder"); }

    @Override
    public void drawWindow() {
        Content_Window.loadAssets(this.assets, Content_Window.getFromDirectory(Content_Window.getAssetsDirectory(), true, true), filter);
        addDefaultAssets(this.assets, filter);

        Asset selectedAsset = Content_Window.drawContentGrid(this.assets, ImGui.getContentRegionAvailX(), false, true, null);
        if (selectedAsset != null) {
            try {
                boolean isPrivate = Modifier.isPrivate(targetField.getModifiers());
                if (isPrivate)
                    targetField.setAccessible(true);

                targetField.set(targetObject, Content_Window.loadObjectFromAsset(selectedAsset.getType(), selectedAsset.getFilepath()));

                if (isPrivate)
                    targetField.setAccessible(false);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            close();
        }
    }

    private static void addDefaultAssets(List<Asset> outList, Asset.AssetType... filter) {
        List<Asset> tmp = new LinkedList<>(outList);
        outList.clear();

        outList.add(new Asset("none", null, null));

        List<Asset.AssetType> _filter = Arrays.asList(filter);
        if (_filter.contains(Asset.AssetType.Mesh)) {
            File defaultMeshesDirectory = new File("Resources/meshes/defaultMeshes");
            for (File file : defaultMeshesDirectory.listFiles())
                outList.add(new Asset_Mesh(file.getAbsolutePath()));
        }

        outList.addAll(tmp);
    }

    public static void openWindow(Object targetObject, Field targetField, Asset.AssetType... filter) {
        ContentFinder_Window.filter = filter;
        ContentFinder_Window.targetObject = targetObject;
        ContentFinder_Window.targetField = targetField;
        EngineGuiLayer.setWindowsOpen_ByType(ContentFinder_Window.class, true);
    }
}
