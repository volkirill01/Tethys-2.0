package engine.assets.assetTypes;

import engine.assets.Asset;
import engine.assets.AssetPool;

public class Asset_Folder extends Asset {

    public Asset_Folder(String filepath) { super(filepath, AssetType.Folder, AssetPool.getTexture("Resources/icons/assets/icon=folder-open-regular-(256x256).png")); }
}
