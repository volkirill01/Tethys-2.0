package engine.assets.assetTypes;

import engine.assets.Asset;
import engine.assets.AssetPool;

public class Asset_Mesh extends Asset {

    public Asset_Mesh(String filepath) { super(filepath, AssetType.Mesh, AssetPool.getTexture("editorFiles/icons/assets/icon=cube-solid-(256x256).png")); }
}
