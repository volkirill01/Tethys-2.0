package engine.assets.assetTypes;

import engine.assets.Asset;
import engine.assets.AssetPool;

public class Asset_Scene extends Asset {

    public Asset_Scene(String filepath) { super(filepath, AssetType.Scene, AssetPool.getTexture("editorFiles/icons/assets/icon=scene-solid-(256x256).png")); }
}
