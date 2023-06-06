package engine.editor.windows;

import engine.assets.Asset;
import engine.editor.gui.EditorGuiWindow;
import engine.editor.gui.EngineGuiLayer;
import imgui.ImGui;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ContentFinder_Window extends EditorGuiWindow {

    private static Asset.AssetType[] filter;
    private static Object targetObject;
    private static Field targetField;
    private final List<Asset> assets = new ArrayList<>();

    public ContentFinder_Window() { super("\uED14 Content Finder"); }

    @Override
    public void drawWindow() {
        Content_Window.loadAssets(this.assets, Content_Window.getFromDirectory(Content_Window.getAssetsDirectory(), true, true), filter);
        Asset selectedAsset = Content_Window.drawAssetsGrid(this.assets, ImGui.getContentRegionAvailX(), false, true);
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

    public static void openWindow(Object targetObject, Field targetField, Asset.AssetType... filter) {
        ContentFinder_Window.filter = filter;
        ContentFinder_Window.targetObject = targetObject;
        ContentFinder_Window.targetField = targetField;
        EngineGuiLayer.setWindowsOpen_ByType(ContentFinder_Window.class, true);
    }
}
