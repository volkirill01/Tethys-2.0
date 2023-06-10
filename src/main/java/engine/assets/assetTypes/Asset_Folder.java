package engine.assets.assetTypes;

import engine.assets.Asset;
import engine.assets.AssetPool;
import engine.renderer.Texture;

import java.io.File;

public class Asset_Folder extends Asset {

    private final File folder;

    public Asset_Folder(String filepath) {
        super(filepath, AssetType.Folder, null);
        this.folder = new File(filepath);
    }

    @Override
    public Texture getIcon() {
        if (folder.exists())
            if (folder.listFiles().length > 0)
                return AssetPool.getTexture("Resources/icons/assets/icon=folder-solid-(256x256).png");

        return AssetPool.getTexture("Resources/icons/assets/icon=folder-open-regular-(256x256).png");
    }
}
